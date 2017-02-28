package jmind.base.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jmind.base.lang.IProperties;
import jmind.base.lang.SourceProperties;

/**
 * 参考dubbo SPI（Service Provider Interface) ExtensionLoader 的简单实现。
 * 配置文件 放在classpath META-INF/jmind/ 下。 以接口名定义文件名
 * 内容为 lru=jmind.core.cache.LRUCache 这样的key-value 形式
 * 扩展 ServiceLoader 不能定义key 的形式
 * {@link http://blog.csdn.net/fenglibing/article/details/7083071}
 * @see ServiceLoader
 * @see com.alibaba.dubbo.common.extension.ExtensionLoader
 * @author wbxie
 * 2013-12-11
 */
public class JmindLoader<T> {

    private static final String PREFIX = "META-INF/jmind/";

    private static final ConcurrentMap<Class<?>, JmindLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, JmindLoader<?>>();

    private final ConcurrentMap<String, T> INSTANCES;
    private final IProperties p;

    private final Class<?> type;

    @SuppressWarnings("unchecked")
    public static <T> JmindLoader<T> getJmindLoader(Class<T> type) {
        JmindLoader<T> loader = (JmindLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            synchronized (type){
                loader = new JmindLoader<T>(type);
                EXTENSION_LOADERS.put(type,loader );
            }
        }
        return loader;
    }

    private JmindLoader(Class<?> type) {
        this.type = type;
        this.INSTANCES = new ConcurrentHashMap<String, T>();
        p = new SourceProperties(PREFIX + this.type.getName());

    }

    /**
     * 
     * 2013-12-11 
     * @param name 配置文件配置的key
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (DataUtil.isEmpty(name))
            throw new IllegalArgumentException("Extension name is null");
        T t = INSTANCES.get(name);
        if (t == null) {
            synchronized (type){
                String className = p.getProperty(name);
                if (className == null)
                    return null;
                t=(T) ClassUtil.newInstance(className);
                INSTANCES.put(name,t);
            }
        }
        return t;
    }

}
