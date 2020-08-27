 package jmind.base.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;

public class MD5 {

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    /**
     * 对字符串进行MD5 32 位 加密 
     * @param text 
     * @return
     */
    public static String md5(String text) {
        if (DataUtil.isEmpty(text))
            return DataUtil.EMPTY;
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        return byteHex(bytes);
    }

    /**
     * md5 16位加密
     * @param text
     * @return
     */
    public static String md5_16(String text) {
        if (DataUtil.isEmpty(text))
            return DataUtil.EMPTY;
        return md5(text).substring(8, 24);
    }

    public static String MD5_2(String str) {
        if (DataUtil.isEmpty(str))
            return DataUtil.EMPTY;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(str.getBytes(GlobalConstants.UTF8));
            byte b[] = md.digest();
            String result = "";
            for (int i = 0; i < b.length; i++)
                result = new StringBuilder(result).append(byteHex(b[i])).toString();
            return result;
        } catch (Exception e) {
            return DataUtil.EMPTY;
        }
    }

    private static String byteHex(byte ib) {
        char ob[] = new char[2];
        ob[0] = DIGITS[ib >>> 4 & 15];
        ob[1] = DIGITS[ib & 15];
        String s = new String(ob);
        return s;
    }

    public static String byteHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    // 编码
    public static String encodeBASE64(String s) {
        if (s == null)
            return null;
        String code = new String(Base64.encode(s.getBytes()));

        code = code.replaceAll("=", ",");
        code = code.replaceAll("\\+", ".");
        return code;
    }

    //		 将 BASE64 编码的字符串 s 进行解码
    public static String decoderBASE64(String s) {
        if (s == null)
            return null;
        s = s.replaceAll(",", "=");
        s = s.replace(".", "+");

        try {
            byte[] b = Base64.decode(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

}
