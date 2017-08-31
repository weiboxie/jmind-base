package jmind.base.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

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


    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "specified class is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", e);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", e);
        }
    }

    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException ex) {
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        }
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
        try {
            makeAccessible(ctor);
            return ctor.newInstance(args);
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(),
                    "Constructor threw exception", ex.getTargetException());
        }
    }

    public static <T> T newProxy(
            Class<T> interfaceType, InvocationHandler handler) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("expected an interface to proxy, but " + interfaceType);
        }
        Object object = Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                handler);
        return interfaceType.cast(object);
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) &&
                !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    public static Set<Annotation> getAnnotations(Class<?> clazz) {
        Set<Annotation> annos = new HashSet<Annotation>();
        getAnnotations(clazz, annos);
        return annos;
    }

    static void getAnnotations(Class<?> clazz, Set<Annotation> annos) {
        if (clazz == null) {
            return;
        }
        annos.addAll(Arrays.asList(clazz.getDeclaredAnnotations()));
        for (Class<?> parent : clazz.getInterfaces()) {
            getAnnotations(parent, annos);
        }
        getAnnotations(clazz.getSuperclass(), annos);
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


   // 推断并设置main方法所在类
    public static Class<?> deduceMainClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        }
        catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

}
