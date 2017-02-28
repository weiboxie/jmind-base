package jmind.base.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 *  lru 实现，不允许null 建 和null 值
 * @author weibo.xie
 *  2011-2-16
 */
public class ConcurrentLRUCache<K, V> implements MemCache<K, V> {

    private final ConcurrentMap<K, V> cache;

    public ConcurrentLRUCache(ConcurrentMap<K, V> cache) {
        this.cache = cache;
    }

    public ConcurrentLRUCache(int maxSize) {
        cache = new ConcurrentLRUHashMap<K, V>(maxSize);
    }


    public boolean delete(K key) {
        return cache.remove(key) != null;
    }


    public V get(K key) {
        return cache.get(key);
    }


    public Map<K, V> getMulti(Collection<K> keys) {
        Map<K, V> map = new HashMap<K, V>();
        for (K key : keys) {
            V value = get(key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;

    }


    public boolean set(K key, V o) {
        if (key != null && o != null)
            cache.put(key, o);
        return true;
    }


    public String toString() {
        return cache.toString();
    }


    public Map<K, V> getCache() {
        return cache;
    }


    public void clear() {
        cache.clear();

    }


    public boolean exists(K key) {

        return cache.containsKey(key);
    }

    public boolean set(K key, int seconds, V value) {
        set(key, value);
        return true;
    }

}
