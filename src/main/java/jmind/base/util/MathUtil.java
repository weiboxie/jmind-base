package jmind.base.util;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 
 * @author weibo.xie
 * 2011-12-2
 */
public class MathUtil {

    /**
     * 判断分成比例大于等于0且小于1
     * @param bd
     * @return
     */
    public static boolean isValid(BigDecimal bd) {
        if (bd.compareTo(BigDecimal.ZERO) >= 0 && bd.compareTo(BigDecimal.ONE) == -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算y的 以x 为底的对数
     * @param x
     * @param y
     * @return
     */
    public static Double log(double x, double y) {
        return Math.log(y) / Math.log(x);
    }

    /**
     * 获取整数二进制的某一位的值
     * @param num 整数
     * @param position 数位（从右到左，从0算起，最右边算第0位）
     * @return 该数位上的值，0或1
     * @author Luo
     */
    public static int getBinaryBit(int num, int position) {
        return num >> position & 1;
    }

    /**
     * 给整数的二进制某一位设置零
     * @param num 整数
     * @param position 数位（从右到左，从0算起，最右边算第0位）
     * @return 该位设置为零后得到的整数
     * @author Luo
     */
    public static int setBinaryBit0(int num, int position) {
        return num & ~(1 << position);
    }

    /**
     * 给整数二进制某一位设置一
     * @param num 整数
     * @param position 数位（从右到左，从0算起，最右边算第0位）
     * @return 该位设置为一后得到的整数
     * @author Luo
     */
    public static int setBinaryBit1(int num, int position) {
        return num | (1 << position);
    }

    // 判断一个数是否是偶数 
    public static boolean isEven(int i) {
        return (i & 1) == 0;
    }

    public static int min(int... is) {
        Arrays.sort(is);
        return is[0];

    }

    public static int max(int... is) {
        Arrays.sort(is);
        return is[is.length - 1];

    }
}
