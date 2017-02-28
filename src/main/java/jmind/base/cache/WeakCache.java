package jmind.base.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 弱缓存，允许null建 和null 值
 * @author wbxie
 * 2013-8-15
 * @param <K>
 * @param <V>
 */
public class WeakCache<K, V> implements MemCache<K, V> {

    private final Map<K, V> cache;

    public WeakCache() {
        cache = new WeakHashMap<K, V>();
    }

    public WeakCache(Map<K, V> m) {
        cache = m;
    }

    @Override
    public boolean delete(K key) {
        return cache.remove(key) != null;

    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
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

    @Override
    public boolean set(K key, V o) {
        cache.put(key, o);
        return true;
    }

    @Override
    public boolean set(K key, int seconds, V value) {
        set(key, value);
        return true;
    }

    @Override
    public String toString() {
        return cache.toString();
    }

    @Override
    public Map<K, V> getCache() {
        return cache;
    }

    @Override
    public void clear() {
        cache.clear();

    }

    @Override
    public boolean exists(K key) {

        return cache.containsKey(key);
    }

}
