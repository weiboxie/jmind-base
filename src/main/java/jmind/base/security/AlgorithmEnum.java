package jmind.base.security;

/**
 * description:
 该项目使用Cipher类完成aes，des，des3和rsa加密.

 获取Cipher类的对象：Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding"); 参数按"算法/模式/填充模式"，有以下的参数

 * AES/CBC/NoPadding (128)
 * AES/CBC/PKCS5Padding (128)
 * AES/ECB/NoPadding (128)
 * AES/ECB/PKCS5Padding (128)
 * DES/CBC/NoPadding (56)
 * DES/CBC/PKCS5Padding (56)
 * DES/ECB/NoPadding (56)
 * DES/ECB/PKCS5Padding (56)
 * DESede/CBC/NoPadding (168)
 * DESede/CBC/PKCS5Padding (168)
 * DESede/ECB/NoPadding (168)
 * DESede/ECB/PKCS5Padding (168)
 * RSA/ECB/PKCS1Padding (1024, 2048)
 * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
 * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
 (1)加密算法有：AES，DES，DESede(DES3)和RSA 四种
 (2) 模式有CBC(有向量模式)和ECB(无向量模式)，向量模式可以简单理解为偏移量，使用CBC模式需要定义一个IvParameterSpec对象
 (3) 填充模式:
 * NoPadding: 加密内容不足8位用0补足8位, Cipher类不提供补位功能，需自己实现代码给加密内容添加0, 如{65,65,65,0,0,0,0,0}
 * PKCS5Padding: 加密内容不足8位用余位数补足8位, 如{65,65,65,5,5,5,5,5}或{97,97,97,97,97,97,2,2}; 刚好8位补8位8
 * @author weibo.xie
 * @date : create in 7:27 下午 2020/8/31
 */
public enum AlgorithmEnum {
    AES(128,"AES/ECB/PKCS5Padding"),  // 128, 192 or 256
    DES(56,"DES/ECB/PKCS5Padding"),
    DESede(168,"DESede/ECB/PKCS5Padding");
//    RSA(1024,"RSA/ECB/PKCS1Padding");
    public final int size;
    public final String transformation;

     AlgorithmEnum(int size,String transformation){
        this.size=size;
        this.transformation=transformation;
    }

}
