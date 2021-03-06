package jmind.base.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface MemCache<K, V> {


    public boolean exists(K key);

    public boolean set(K key, V value);

    /**
     * 
     * 2013-12-4 
     * @param key
     * @param seconds 过期时间，单位秒，目前只有xmemcache 实现有效  和xmemcache 接口保持一致 过期参数
     * @param value
    
     */
    public boolean set(K key, int seconds, V value);

    public boolean delete(K key);

    public V get(K key);

    public Map<K, V> getMulti(Collection<K> keys);

    public Object getCache();

    public void clear();

//    default V computeIfAbsent(K key,final int exp,
//                              Function<? super K, ? extends V> mappingFunction) {
//        Objects.requireNonNull(mappingFunction);
//        V v;
//        if ((v = get(key)) == null) {
//
//            V newValue;
//
//            if ((newValue = mappingFunction.apply(key)) != null) {
//                set(key, exp,newValue);
//                return newValue;
//            }
//        }
//        return v;
//    }
//
//    default V computeIfAbsent(K key,
//                              Function<? super K, ? extends V> mappingFunction) {
//        Objects.requireNonNull(mappingFunction);
//        V v;
//        if ((v = get(key)) == null) {
//            V newValue;
//            if ((newValue = mappingFunction.apply(key)) != null) {
//                set(key,newValue);
//                return newValue;
//            }
//        }
//
//        return v;
//    }
}
