package jmind.base.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于linkedHashMap 的线程安全实现
 * @author weibo.xie
 *  2011-2-16
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> implements MemCache<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int DEFAULT_MAX_CAPACITY = 1000;

    private volatile int maxCapacity;

    private final Lock lock = new ReentrantLock();

    public LRUCache() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public LRUCache(int maxCapacity) {
        super(16, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            lock.lock();
            return super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        try {
            lock.lock();
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lock();
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        try {
            lock.lock();
            return super.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean set(K key, V value) {
        this.put(key, value);
        return true;
    }

    public boolean set(K key, int seconds, V value) {
        set(key, value);
        return true;
    }

    @Override
    public boolean delete(K key) {
        return this.remove(key) != null;
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
    public Object getCache() {
        return this;
    }

    @Override
    public boolean exists(K key) {

        return containsKey(key);
    }

}
