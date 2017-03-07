package jmind.base.cache;

import java.util.Collection;
import java.util.Map;

public interface MemCache<K, V> {

    public static enum Type {
        XMEM, GUAVA, PERSISTENT, REDIS
    };

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
}
