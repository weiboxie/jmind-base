package jmind.base.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class RC4Crypt {

    // S盒的byte数组;
    private byte[] box;
    // 明文的字符串;
    public String plaintext;
    // 明文byte数组;
    public byte[] bytePlaintext;
    // 密文结果的byte数组;
    public byte[] result;
    // 输入密钥的字符串;
    public String mainKey;
    // 子密钥的byte数组;
    public byte[] subKey;
    // 本加/解密明/密的字节长度;
    public int length;

    // 无参数构造方法;**********************************************************
    public RC4Crypt() {
        // 顺序初始化S盒;
        makeBox();
    }

    // 用于实现明文加密的构造方法;**********************************************
    public RC4Crypt(String a, String b) {
        // 明文,密钥字符串参数传递;
        plaintext = a;
        mainKey = b;
        // 顺序初始化S盒;
        makeBox();
        // 用明文字符串初始化明文byte数组;
        getBytePlaintext();
        try {
            // 初始化本对象内容长度;
            length = bytePlaintext.length;
            // 初始化子密钥byte数组;
            getSubKey();
            // 进行加密计算;
            encrypt();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // 实现密文解密的构造方法;**************************************************
    public RC4Crypt(byte[] a, String b) {
        // 密文byte数组,密钥字符串参数传递;
        result = a;
        mainKey = b;
        // 顺序初始化S盒;
        makeBox();
        try {
            // 初始化本对象内容长度;
            length = result.length;
            // 初始化子密钥byte数组;
            getSubKey();
            // 进行解密运算;
            decrypt();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // 初始化S盒内数据的方法;***************************************************
    private void makeBox() {
        box = new byte[256];
        // 用00000000B到11111111B的内容依次初始化S盒;
        // 注意byte类型数据表示0-255无符号数字的顺序;
        // 00000000B(0)-01111111B(127)-10000000B(-128)-11111111B(-1)
        for (int i = 0; i < 256; i++) {
            if (i < 127) {
                box[i] = (byte) i;
            } else {
                box[i] = (byte) (i - 256);
            }
        }
    }

    // 把明文改变为相应的byte数组的方法;****************************************
    private void getBytePlaintext() {
        try {
            bytePlaintext = plaintext.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 把byte数组恢复成字符串的方法;********************************************
    public String recoverToString() {
        return new String(bytePlaintext);
    }

    // 获得自密钥的方法;********************************************************
    private void getSubKey() throws Exception {
        // 声明所需要的局部变量
        int j = 0; //hold = 0;
        byte temp = 0;
        byte[] key = mainKey.getBytes("UTF-8");
        // 通过j值的变化和密钥内容,逐步混淆S盒;
        for (int i = 0; i < 256; i++) {
            // j的混淆变化;
            j = (j + (box[i] > 0 ? box[i] : (box[i] + 256)) + (key[i % key.length] > 0 ? key[i % key.length] : (key[i
                    % key.length] + 256))) % 256;
            // 空杯法换位;
            temp = box[i];
            box[i] = box[j];
            box[j] = temp;
        }
        int i = j = 0;
        // 声明一个容量等于本对象内容长度的byte数组来跟新子密钥数组;
        subKey = new byte[length];
        for (int k = 0; k < length; k++) {
            // 遍历i;
            i = ++i % 256;
            // j第二次混淆变化;
            j = (j + (box[i] > 0 ? box[i] : (box[i] + 256))) % 256;
            // 空杯发换位;
            temp = box[i];
            box[i] = box[j];
            box[j] = temp;
            // 子密钥byte数组各元素混淆赋值;
            subKey[k] = box[((box[i] > 0 ? box[i] : (box[i] + 256)) + (box[j] > 0 ? box[j] : (box[j] + 256))) % 256];
        }
    }

    // 加密计算方法;************************************************************
    private void encrypt() throws Exception {
        result = new byte[length];
        // 明文byte数组与子密钥byte数组异或,得到密文byte数组;
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (bytePlaintext[i] ^ subKey[i]);
        }
    }

    // 解密计算方法;************************************************************
    private void decrypt() throws Exception {
        bytePlaintext = new byte[length];
        // 密文byte数组于子密钥byte数组异或,得到明文byte数组;
        for (int i = 0; i < length; i++) {
            bytePlaintext[i] = (byte) (result[i] ^ subKey[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        //        byte[] result = new RC4Crypt("wave中文", "a2sdas1*)(fyd9").result;
        //        byte[] str = Base64.encode(result);
        //        System.out.println(new String(str));
        //        String str2 = new RC4Crypt(Base64.decode(str), "a2sdas1*)(fyd9").recoverToString();
        //        System.out.println(str2);

        byte[] result = new RC4Crypt("wave中文", "a2sdas1*)(fyd9").result;
        String str = new String(result, "UTF-8");
        str = URLEncoder.encode(str, "UTF-8");
        System.out.println(str);
        str = URLDecoder.decode(str, "UTF-8");
        String str2 = new RC4Crypt(str.getBytes("UTF-8"), "a2sdas1*)(fyd9").recoverToString();
        System.out.println(str2);
    }
}