package jmind.base.algo.atomic;



import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * description:  获取Unsafe实例
 *  https://blog.csdn.net/db2china/article/details/110953736
 * @author weibo.xie
 * @date : create in 3:38 下午 2021/8/2
 */
public class TheUnsafe {

    private static Unsafe unsafe;

    /**
     * 两种方法可以获取到Unsafe实例
     *
     * 1. 把要获取Unsafe实例的类所在jar包路径追加到默认的bootstrap路径中。
     *
     *     Unsafe类为一单例实现，提供静态方法getUnsafe获取Unsafe实例，当且仅当调用getUnsafe方法的类为引导类加载器所加载时才合法，否则抛出SecurityException异常。
          2. 反射获取
     */
    static {
        try {
            final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
                public Unsafe run() throws Exception {
                    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    return (Unsafe) theUnsafe.get(null);
                }
            };
            unsafe = AccessController.doPrivileged(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Unsafe  getUnsafe(){
        return unsafe;
    }
}