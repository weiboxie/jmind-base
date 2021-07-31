package jmind.base.security;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;


/**
 * java 对称加密算法
 * https://www.jianshu.com/p/d28138f86887
 *
 * https://www.cnblogs.com/caizhaokai/p/10944667.html
 */
public class SymmetricCipher{


    public static void main(String[] args) throws NoSuchAlgorithmException {
     //   byte[] aes = generateKey(AlgorithmEnum.AES);
        String plaintext="中国人民万岁!";


        AlgorithmEnum algorithmEnum=AlgorithmEnum.DESede;

         CipherResult ciphertext=encode(algorithmEnum,plaintext);
        System.err.println(algorithmEnum+" 密文："+ciphertext.getCiphertext());
        System.err.println("明文： "+decode(algorithmEnum,ciphertext));


         ciphertext= encodePBE( plaintext);
       System.err.println("PBE:   "+ciphertext.getCiphertext());
        System.err.println(decodePBE(ciphertext));
    }

    /**
     * 支持的算法有:
     * AES
     * ARCFOUR
     * Blowfish
     * DES
     * DESede
     * HmacMD5
     * HmacSHA1,HmacSHA256,HmacSHA384,HmacSHA512
     * RC2
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateKeyBytes(AlgorithmEnum algorithm) throws NoSuchAlgorithmException {

        // 生成KEY
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.name());
        keyGenerator.init(algorithm.size);
        // 产生密钥
        SecretKey secretKey = keyGenerator.generateKey();
        // 获取密钥
        byte[] keyBytes = secretKey.getEncoded();
        return keyBytes;
    }

    public static Key generateKey(AlgorithmEnum algorithm,CipherResult cipherResult) throws Exception {
        Key key=null;
        if(cipherResult.getKey()==null){
            cipherResult.setKey(generateKeyBytes(algorithm));
        }
        byte[] keyBytes=cipherResult.getKey();

        switch (algorithm){
            case AES:{
                 key = new SecretKeySpec(keyBytes, algorithm.name());
                break;
            }
            case DES:{
                KeySpec desKeySpec = new DESKeySpec(keyBytes);
                SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm.name());
                key = factory.generateSecret(desKeySpec);
                break;
            }
            case DESede:{
                // KEY转换
                KeySpec desKeySpec = new DESedeKeySpec(keyBytes);
                SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm.name());
                key = factory.generateSecret(desKeySpec);
            }

        }
        return key;
    }

    // 用jdk实现:
    public static CipherResult encode(AlgorithmEnum algorithm,String plaintext){
        CipherResult cipherResult=new CipherResult();
        try{
            // KEY转换
            Key key = generateKey(algorithm,cipherResult);
            // 加密
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(plaintext.getBytes());
            cipherResult.setCiphertext(HexString.toHex(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherResult;
    }

    public static String decode(AlgorithmEnum algorithm,CipherResult cipherResult){
        try{
            // KEY转换
            Key key = generateKey(algorithm,cipherResult);
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            // 解密
            cipher.init(Cipher.DECRYPT_MODE, key);
           byte[] result = cipher.doFinal(HexString.hexToByte(cipherResult.getCiphertext()));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 用jdk实现:
    public static CipherResult encodePBE(String plaintext){
        CipherResult cipherResult=new CipherResult();
        try{
            SecureRandom random = new SecureRandom();
            byte[] salt = random.generateSeed(8);
            // KEY转换
            PBEKeySpec pbeKeySpec = new PBEKeySpec(HexString.toHex(salt).toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);
            // 加密
            PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpac);
            byte[] result = cipher.doFinal(plaintext.getBytes());
            cipherResult.setKey(salt);
            cipherResult.setCiphertext(HexString.toHex(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherResult;
    }

    public static String decodePBE(CipherResult cipherResult){
        try{
            PBEKeySpec pbeKeySpec = new PBEKeySpec(HexString.toHex(cipherResult.getKey()).toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);
            // 加密
            PBEParameterSpec pbeParameterSpac = new PBEParameterSpec(cipherResult.getKey(), 100);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            // 解密
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpac);
            byte[] result = cipher.doFinal(HexString.hexToByte(cipherResult.getCiphertext()));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}