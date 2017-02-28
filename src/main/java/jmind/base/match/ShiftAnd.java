package jmind.base.match;

/**
 *  * Shift-And 算法
 * Shift-And算法使用的是一个简单的自动机。
 * @author weibo.xie
 * 2011-11-29
 */
public class ShiftAnd {

    /**
     * ~ 按位非（NOT）<br>
     * & 按位与（AND）<br>
     * | 按位或（OR） <br>
     * ^ 按位异或（XOR） <br>
     * >> 右移 <br>
     * >>> 无符号右移 <br>
     * << 左移 <br>
     * 
     * @param args
     */
    public static void main(String[] args) {
        String a = "011";
        String b = "010";
        int binaryA = Integer.parseInt(a, 2);
        int binaryB = Integer.parseInt(b, 2);

        int c = binaryA & binaryB;

        System.out.println(c);
    }

}
