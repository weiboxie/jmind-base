package jmind.base.security;

/**
 * Created by weibo.xwb on 2018/3/2.
 */
public class HexString {

    static final String chars = "0123456789ABCDEF";
    static final char[] hexs = chars.toCharArray();
    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String toHex(String str) {
        return toHex(str.getBytes());
    }

    public static String toHex(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(hexs[bit]);
            bit = bs[i] & 0x0f;
            sb.append(hexs[bit]);
        }
        return sb.toString().trim();
    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexToStr(String hexStr) {
        return new String(hexToByte(hexStr));
    }

    public static byte[] hexToByte(String hexStr) {
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = chars.indexOf(hexs[2 * i]) * 16;
            n += chars.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return bytes;
    }

    /**
     * 字符串转换UNICODE
     */
    public static String toUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
// 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicodeToStr(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }

    public static void main(String[] args) {
        String source = "wavez中文";
        String hex = toHex(source);
        System.err.println(hex);
        System.err.println(hexToStr(hex));
        hex = toUnicode(source);
        System.err.println(hex);
        System.err.println(unicodeToStr(hex));
    }
}
