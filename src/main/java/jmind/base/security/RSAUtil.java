package jmind.base.security;

/**
 * Created by fangyanpeng on 16-5-16.
 */

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

/**
 * RSA安全编码组件
 *
 * @author fangyanpeng1
 */
public abstract class RSAUtil {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnqAAR7vpFBe+39wb4s7NdL+XDT6LJaTbWgRVRYVTIVOjLPtmSiOII//x/igHcRj5+gCMB3tmkfOb3LG4Gs74FSA62zbJR6+A65rMv231LhHRMGIPDAHbmlzUm/h5aIb08pOR9jtxzi0hU5IyXaT74o7yfT0UQFUwE2ERkXmeLFwIDAQAB";
    public static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKeoABHu+kUF77f3Bvizs10v5cNPoslpNtaBFVFhVMhU6Ms+2ZKI4gj//H+KAdxGPn6AIwHe2aR85vcsbgazvgVIDrbNslHr4Drmsy/bfUuEdEwYg8MAduaXNSb+HlohvTyk5H2O3HOLSFTkjJdpPvijvJ9PRRAVTATYRGReZ4sXAgMBAAECgYBlZtEUB6xLDvocFEadKWL0m8GlzYaxtvc56bzRCcNTnzdlpk/FtxCvTNSU9w2FAt9Nd4a03OpZ+ElheffaCeCFKa/QBxr2hV7RkIVI9U5cxm4mk7lBB9c9/RD7l8UMUeBXrXONT4cn1EswbEQDNX52axRo9YHCW6BOHgOLDQvIMQJBAOIAqK+p7FzWqJ40lI2NWOxVT9cBpOZxSh+mvRQqmLgTQNsyzj253U0wASxVp7oq/UgBbKebx/PHNoRgHoUyLLUCQQC96MqvZ/VVpuFaDllpWfFz0Y4TWCib4vtqycM6t6cRuLLi38R0dbYWFbaTfTHVoxoGp7ta4OpsPR5sXxRwAgQbAkEAm0nGnoUMYrIgRCxu0mCH9EOeS2bWznyTaPIhw0SNkRWxn6vmKGWYmNveQn9Mc0/snthTt0sD9g4w+H4vGmhibQJABWVFzPIBMGvLX0mlFK8vgwDjLn2EudkbF4+f8l0hbAl5W6TL6hsmDz6Sr2IRNmap6KJ/WjelsWmTIbUZ2YzVHQJBALcgah/pQCEFwLxtNGqpNkVX6J2jHrc7cV3UW+VyDtt5bJTJivEWhhg2kC51Iu21RY7UhxNMSPdd6j4VMccv5rI=";

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data
     *            加密数据
     * @param privateKey
     *            私钥
     *
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        return encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data
     *            加密数据
     * @param publicKey
     *            公钥
     * @param sign
     *            数字签名
     *
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    public static String encryptBASE64(byte[] content) {
        return new String(Base64.decode(content));
    }

    public static byte[] decryptBASE64(String content) {
        return Base64.decode(content);
    }

    public static String encryByPublicKey(String str) throws Exception {
        byte[] strBytes = DatatypeConverter.parseBase64Binary(str);
        return DatatypeConverter.printBase64Binary(encryptByPublicKey(strBytes, publicKey));
    }

    public static String decryByPublicKey(String str) throws Exception {
        byte[] strBytes = DatatypeConverter.parseBase64Binary(str);
        return DatatypeConverter.printBase64Binary(decryptByPublicKey(strBytes, publicKey));
    }

    public static String encryByPrivateKey(String str) throws Exception {
        byte[] strBytes = DatatypeConverter.parseBase64Binary(str);
        return DatatypeConverter.printBase64Binary(encryptByPrivateKey(strBytes, privateKey));
    }

    public static String decryByPrivateKey(String str) throws Exception {
        byte[] strBytes = DatatypeConverter.parseBase64Binary(str);
        return DatatypeConverter.printBase64Binary(decryptByPrivateKey(strBytes, privateKey));
    }
}