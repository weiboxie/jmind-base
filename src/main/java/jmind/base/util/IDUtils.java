package jmind.base.util;

/**
 * 数字Long ID <=> String 互相转换
 * 2013年9月23日 下午5:08:36
 * @author youfang.mu
 */
public class IDUtils {
    public static void main(String[] args) {
        String s = IDUtils.encode(1000000000);
        long j = IDUtils.decode(s);
        System.out.println("s=" + s + "\tj=" + j);
    }

    /** 字典表 */
    private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'w', 'x', 'y', 'u', 'z' };

    private final static long zhimeiOffset = 1000000000L;//偏移量 10E 

    /**
     * 获得加密数字
     * @param id
     * @return
     */
    public static String encode(long id) {
        return numericToString(id + zhimeiOffset, 32);
    }

    /**
     * 
     * @param str
     * @return
     */
    public static long decode(String str) {
        return stringToNumeric(str, 32) - zhimeiOffset;
    }

    /**
     * 将十进制的数字转换为指定进制的字符串。
     *
     * @param i  十进制的数字。
     * @param system  指定的进制，常见的2/8/16。
     * @return 转换后的字符串。
     */
    protected static String numericToString(long i, int system) {
        long num = 0;
        if (i < 0) {
            num = ((long) 2 * 0x7fffffff) + i + 2;
        } else {
            num = i;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((num / system) > 0) {
            buf[--charPos] = digits[(int) (num % system)];
            num /= system;
        }
        buf[--charPos] = digits[(int) (num % system)];
        return new String(buf, charPos, (32 - charPos));
    }

    /**
     * 将其它进制的数字（字符串形式）转换为十进制的数字。
     *
     * @param s 其它进制的数字（字符串形式）
     * @param system 指定的进制，常见的2/8/16。
     * @return 转换后的数字。
     */
    protected static long stringToNumeric(String s, int system) {
        char[] buf = new char[s.length()];
        s.getChars(0, s.length(), buf, 0);
        long num = 0;
        for (int i = 0; i < buf.length; i++) {
            for (int j = 0; j < digits.length; j++) {
                if (digits[j] == buf[i]) {
                    num += j * Math.pow(system, buf.length - i - 1);
                    break;
                }
            }
        }
        return num;
    }
}
