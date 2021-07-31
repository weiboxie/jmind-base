package jmind.base.security;

/**
 * description:
 *
 * @author weibo.xie
 * @date : create in 11:10 上午 2020/9/1
 */
public class CipherResult {

    private String ciphertext ;
    private byte[] key ;

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}