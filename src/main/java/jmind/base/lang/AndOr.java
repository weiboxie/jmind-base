package jmind.base.lang;

/**
 * 可以通过一个标志位，判断含有多种状态的实现
 * @author xieweibo
 * @date 2015年6月8日
 */
public class AndOr {

    public static final long BASE_VALUE = 2;

    /**
     * 获取状态值
     * @param b 从0开始
     * @return
     */
    public static long getStatus(long b) {
        return (long) Math.pow(BASE_VALUE, b);
    }

    /**
     * 设置某种状态
     * @param value 当前值
     * @param status 状态值，必须是2的次方
     * @return 设置后的值
     */
    public static long setOn(long value, long status) {
        return value | status;
    }

    /***
     * 取消某种状态
     * @param value 当前值
     * @param status 状态值，必须是2的次方
     * @return
     */
    public static long setOff(long value, long status) {
        return value & (~status);
    }

    /**
     * 是否包含某种状态
     * @param value
     * @param status
     * @return
     */
    public static boolean has(long value, long status) {
        return (value & status) > 0;
    }

    public static void main(String[] args) {
        long[] val = { 1, 2, 4 };
        long a = 0;
        long b = 7;
        for (long i : val) {
            a = setOn(a, i);
            System.out.println(has(7, i));
        }
        System.out.println(a);

    }
}
