package jmind.base.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 可以基于 SoftReference 或 WeakReference 的方式进行软引用缓存
 google guava http://www.doc88.com/p-673304385367.html
 * @author weiboxie
 * 2010-12-3
 * @param <K>
 * @param <V>
 */
public class ObjectCachePool<K, V> {
    public static final int FIFO_POLICY = 1;
    public static final int LRU_POLICY = 2;
    public static final int DEFAULT_SIZE = 10;
    private Map<K, V> cacheObject;

    public ObjectCachePool() {
        this(DEFAULT_SIZE);
    }

    public ObjectCachePool(final int defaultSize) {
        this(defaultSize, FIFO_POLICY);
    }

    public ObjectCachePool(final int defaultSize, final int policy) {
        switch (policy) {
        case FIFO_POLICY:
            cacheObject = new LinkedHashMap<K, V>(defaultSize) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 2L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> a) {
                    return size() > defaultSize;
                }

            };
            break;
        case LRU_POLICY:
            cacheObject = new LinkedHashMap<K, V>(defaultSize, 0.75f, true) {
                /**
                 * 
                 */
                private static final long serialVersionUID = -1897812966134497523L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> a) {
                    return size() > defaultSize;
                }
            };
            break;

        default:
            cacheObject = new LinkedHashMap<K, V>(defaultSize) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 2L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> a) {
                    return size() > defaultSize;
                }

            };
            break;
        }
    }

    public void put(K key, V value) {
        cacheObject.put(key, value);
    }

    public V get(K key) {
        return cacheObject.get(key);
    }

    public boolean containsKey(K key) {
        return cacheObject.containsKey(key);
    }

    public boolean containsValue(V v) {
        return cacheObject.containsValue(v);
    }

    public void remove(K key) {
        cacheObject.remove(key);
    }

    public void clear() {
        cacheObject.clear();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Set<Entry<K, V>> set = cacheObject.entrySet();

        sb.append("{");
        //		for(K  key:cacheObject.keySet()){
        //			System.out.println(cacheObject.keySet().size());
        //			sb.append(key);
        //			sb.append(":");
        //			sb.append(cacheObject.get(key));
        //			sb.append(",");
        //			System.out.println(cacheObject.keySet().size());
        //		}
        for (Iterator<Entry<K, V>> it = set.iterator(); it.hasNext();) {
            Entry<K, V> key = it.next();
            sb.append(key.getKey());
            sb.append(":");
            sb.append(key.getValue());
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }

    public static void main(String[] args) {
        ObjectCachePool<Integer, String> cache = new ObjectCachePool<Integer, String>(10, LRU_POLICY);
        for (int i = 0; i < 100; i++) {

            cache.put(i, "v" + i);
            cache.get(0);
        }

        System.out.println(cache);
    }
}
