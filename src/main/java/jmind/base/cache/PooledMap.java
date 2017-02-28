package jmind.base.cache;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The PooledMap is used to cache some Objects in a certain count.
 *
 * @param <Key>
 * @param <Value>
 */
public class PooledMap<Key, Value> extends AbstractMap<Key, Value> {
    private int maxCount = 16;
    private Queue<Entry> queue = new LinkedList<Entry>();

    /**
     * The Entry for this Map.
     * @author Andy Han
     *
     */
    private class Entry implements Map.Entry<Key, Value> {
        private Key key;
        private Value value;

        public Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        public Key getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }

        public Value setValue(Value value) {
            return this.value = value;
        }
    }

    /**
     * Default Constructor.
     */
    public PooledMap() {
    }

    /**
     * Constructor.
     * @param size the size of the pooled map;
     */
    public PooledMap(int size) {
        maxCount = size;
    }

    @Override
    public Value put(Key key, Value value) {
        while (queue.size() >= maxCount) {
            queue.remove();
        }
        queue.add(new Entry(key, value));
        return value;
    }

    @Override
    public Value get(Object key) {
        for (Iterator<Entry> iter = queue.iterator(); iter.hasNext();) {
            Entry type = iter.next();
            if (type.key.equals(key)) {
                queue.remove(type);
                queue.add(type);
                return type.value;
            }
        }
        return null;
    }

    @Override
    public Set<Map.Entry<Key, Value>> entrySet() {
        Set<Map.Entry<Key, Value>> set = new HashSet<Map.Entry<Key, Value>>();
        set.addAll(queue);
        return set;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Set<Key> keySet() {
        Set<Key> set = new HashSet<Key>();
        for (Entry e : queue) {
            set.add(e.getKey());
        }
        return set;
    }

    @Override
    public Value remove(Object obj) {
        for (Entry e : queue) {
            if (e.getKey().equals(obj)) {
                queue.remove(e);
                return e.getValue();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        PooledMap<String, String> map = new PooledMap<String, String>(3);
        map.put("1", "11111");
        map.put("2", "22222");
        map.put("3", "33333");
        System.out.println(map.size() == 3);
        System.out.println(map.values().size() == 3);
        System.out.println(map.remove("3") == "33333");
        System.out.println(map.size() == 2);

        System.out.println(map.get("1") == "11111");
        map.put("4", "44444");

        System.out.println(map.get("6") == null);
        System.out.println(map.get("4") == "44444");
        System.out.println(map.size() == 3);
        System.out.println(map.containsKey("4"));
        System.out.println(map.containsValue("44444"));
        System.out.println(map.containsKey("1"));
        System.out.println(map.containsKey("2"));

        System.out.println(map.isEmpty() == false);

        map.remove("1");
        map.remove("4");
        map.remove("2");
        System.out.println(map.isEmpty() == true);
        map.clear();
    }
}