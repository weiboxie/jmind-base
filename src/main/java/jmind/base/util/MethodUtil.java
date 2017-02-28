package jmind.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodUtil {

    public static Object invokeMethod(Object owner, String methodName, Object... args) throws Exception {

        Class<?>[] argsClass = new Class[args.length];
        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();

        }
        Method method = owner.getClass().getMethod(methodName, argsClass);
        return method.invoke(owner, args);
    }

    public static Object invokeMethod(Class<?> ownerClass, String methodName, Object... args) throws Exception {
        Class<?>[] argsClass = new Class[args.length];
        for (int i = 0, j = args.length; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(ownerClass.newInstance(), args);
    }

    public static Object invokeMethod(String className, String methodName, Object... args) throws Exception {
        Class<?> ownerClass = Class.forName(className);
        return invokeMethod(ownerClass, methodName, args);
    }

    /**
     * 获取类属性,不包含父类的属性
     * @param clazz
     * @return
     */
    public static List<String> getPropertys(Class<?> clazz) {
        List<String> list = new ArrayList<String>();
        for (Field f : clazz.getDeclaredFields()) {
            list.add(f.getName());
        }
        return list;
    }

    /**
     * 获取类方法，需要包含父类方法用 getMethods
     * @param clazz
     * @return
     */
    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> list = new ArrayList<Method>();
        //    clazz.getMethods()  包含父类方法
        for (Method f : clazz.getDeclaredMethods()) {
            list.add(f);
        }
        return list;
    }

    /**
     * 获取作为常量的字段
     * @param clazz
     * @return
     */
    public static List<Field> constFields(final Class<?> clazz) {
        final List<Field> result = new LinkedList<Field>();

        for (final Field field : clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers(); // 修饰符
            final Class<?> fieldType = field.getType(); // 声明的类型
            final boolean isAccessible = field.isAccessible(); // 可访问
            final boolean isPublic = Modifier.isPublic(modifiers); // 公共
            final boolean isStatic = Modifier.isStatic(modifiers); // 静态
            final boolean isFinal = Modifier.isFinal(modifiers); // 常量
            final boolean isPrimitiveOrCharSequence = fieldType.isPrimitive() // 
                    || CharSequence.class.isAssignableFrom(fieldType); // 基本类型或字符序列

            if ((isAccessible || isPublic) && isStatic && isFinal && isPrimitiveOrCharSequence) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * 将类型的常量存入map 中
     * @param clazz
     * @return
     */
    public static final Map<String, Object> parseConsts(final Class<?> clazz) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (final Field field : constFields(clazz)) {
            try {
                result.put(field.getName(), field.get(clazz));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
