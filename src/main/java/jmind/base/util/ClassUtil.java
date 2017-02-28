package jmind.base.util;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ClassUtil {

    /**
     * 获取classpath 下文件的具体位置
     * @param resource
     * @return
     */
    public static String getResourcePath(String resource) {
        return ClassUtil.class.getClassLoader().getResource(resource).getPath();
    }

    /**
     * 根据全类名，获取所在jar包，以及所在位置
     * @param className
     * @return
     */
    public static String getProtectionDomain(String className) {
        try {
            return Class.forName(className).getProtectionDomain().toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过类名 得到实例
     * 2013-12-11 
     * @param name
     * @return
     */
    public static Object newInstance(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用ServiceLoader 实现
     * 配置文件必须在 META-INF/services/ 目录下
     * 文件名为接口全名， 内容为类名
     * 例 jmind.core.cache.MemCache 文件，
     * 内容为 jmind.core.cache.LRUCache
     * 多个实例换行增加
     * 2013-12-11 
     * @param clazz 接口
     * @param name 实例名
     * @return
     */
    public static <T> T load(Class<T> clazz, String name) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> it = serviceLoader.iterator();
        while (it.hasNext()) {
            T next = it.next();
            if (name.equals(next.getClass().getName()))
                return next;
        }
        return null;
    }

}
