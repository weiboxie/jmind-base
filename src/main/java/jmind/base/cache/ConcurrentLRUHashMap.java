package jmind.base.cache;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于lru策略的并发闭散列哈希表。 <br>
 * 该实现采用了以下策略：
 * <ul>
 * <ol>
 * 沿用了{@link ConcurrentHashMap}的分离锁策略，在将内部其内部将桶划分为若干个分区（{@link Segment}）,每个分区都是一个基于lru策略的
 * {@link Hashtable}。
 * </ol>
 * <ol>
 * 基于双向链表实现的lru策略，每个分区内部都自行维护一个双向链表，当修改和查找方法被调用时，会将被命中（hit）的单元移动至链表头部。
 * </ol>
 * </ul>
 * 该实现不是无阻塞的。
 * 该实现返回的所有迭代器都不会抛出{@link ConcurrentModificationException}异常，但是通过该迭代器获取到的结果是“弱一致性”的。
 * 
 * @author  weiboxie
 * @param <K>
 * @param <V>
 */
public class ConcurrentLRUHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {

    /* ---------------- 静态常量 -------------- */
    private static final long serialVersionUID = -71218921488561397L;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    static final int MAX_SEGMENTS = 1 << 16; // slightly
    // conservative
    static final int RETRIES_BEFORE_LOCK = 2;

    /* ---------------- Fields -------------- */
    final int maxSize;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;

    transient boolean debug = false;

    transient Set<K> keySet;
    transient Set<Map.Entry<K, V>> entrySet;
    transient Collection<V> values;

    /* ---------------- 工具函数 -------------- */
    private static int hash(int h) {
        // 一个散列算法，能够保证散列效果比较均匀，原理还不清楚。。。
        // using variant of single-word Wang/Jenkins hash.
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    private final <K, V> Segment<K, V>[] newArray(int i) {
        return new Segment[i];
    }

    final Segment<K, V> segmentFor(int hash) {
        return segments[(hash >>> segmentShift) & segmentMask];
    }

    /* ---------------- 内部类 -------------- */
    static final class HashEntry<K, V> {

        final K key;
        final int hash;
        volatile V value;
        final HashEntry<K, V> next;
        HashEntry<K, V> before;
        HashEntry<K, V> after;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        static final <K, V> HashEntry<K, V>[] newArray(int i) {
            return new HashEntry[i];
        }

        /**
         * 让节点脱离lru链表。
         */
        void remove() {
            before.after = after;
            after.before = before;
            before = after = null;
        }

        /**
         * 让节点移动到lru链表的头部。
         * 
         * @param entry
         */
        void addBefore(HashEntry<K, V> entry) {
            before = entry.before;
            after = entry;
            before.after = this;
            after.before = this;
        }

        /**
         * 继承目标的lru顺序。
         * 
         * @param entry
         */
        void reuseLRU(HashEntry<K, V> entry) {
            entry.before.after = this;
            entry.after.before = this;
            before = entry.before;
            after = entry.after;
            entry.before = null;
            entry.after = null;
        }
    }

    /**
     * segment实际上是{@link Hashtable}的一种实现，该类继承了{@link ReentrantLock}
     * 类，为得是加锁与释放锁的操作更加方便。另外，也使得俩者不必分别获取。（管程模式） <br>
     * 为了让Segment的变为定长，需要使用一个全局的maxsize实例变量，因此Segment类只能重新定义为非静态类。
     * Segment类中的实例变量都被设为<code>final</code>或<code>volatile</code型，这种处理可以保证变量在并发条件下的“可见性”。
     * 但由于静态域不可改变，remove操作只能只是复制的方式重新定义。
     * 
     * @author huaiyu.du@opi-corp.com
     * @param <K>
     * @param <V>
     */
    @SuppressWarnings("hiding")
    final class Segment<K, V> extends ReentrantLock implements Serializable {

        private static final long serialVersionUID = 7568413189932886369L;

        transient volatile int count;
        transient int modCount;
        transient volatile HashEntry<K, V>[] table;

        transient final HashEntry<K, V> header;

        /**
         * 负载因子，该值影响segment的存储效率，值越大，空间利用率越高，查找效率越低；值越小，空间利用率越高，查找效率越高。
         */

        Segment(int initialCapacity) {
            setTable(HashEntry.<K, V> newArray(initialCapacity));
            header = new HashEntry<K, V>(null, -1, null, null);
            header.before = header.after = header;
        }

        void setTable(HashEntry<K, V>[] newTable) {
            table = newTable;
        }

        /**
         * 获取hash桶内的第一个元素。因为HashEntry类将内部next指针设为final的，因此，每次删除节点都比须从头部遍历然后复制其他，此方法能提高代码通用性。
         * 
         * @param hash
         * @return
         */
        HashEntry<K, V> getFirst(int hash) {
            HashEntry<K, V>[] tab = table;
            return tab[hash & (tab.length - 1)];
        }

        /**
         * 为防止rehash导致的get value失败，需要进行一次加锁的get，此时会对lru链表进行调整。
         */
        V readValueUnderLock(HashEntry<K, V> e) {
            lock();
            try {
                e.remove();
                e.addBefore(header);
                return e.value;
            } finally {
                unlock();
            }
        }

        /* 实现map接口的方法 */

        V get(Object key, int hash) {
            if (count != 0) { // read-volatile
                HashEntry<K, V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V v = e.value;
                        if (v != null)
                            return v;
                        return readValueUnderLock(e); // recheck
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(Object key, int hash) {
            if (count != 0) { // read-volatile
                HashEntry<K, V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key))
                        return true;
                    e = e.next;
                }
            }
            return false;
        }

        /**
         * 判断一个value是否在集合中。该判断会导致目标value在lru中的更新。
         * 
         * @param value
         * @return
         */
        boolean containsValue(Object value) {
            if (count != 0) { // read-volatile
                HashEntry<K, V>[] tab = table;
                int len = tab.length;
                for (int i = 0; i < len; i++) {
                    for (HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                        V v = e.value;
                        if (v == null) // recheck
                            v = readValueUnderLock(e);
                        if (value.equals(v))
                            return true;
                    }
                }
            }
            return false;
        }

        /**
         * 比较并替代一个变量，提供一种原子性的“判断并修改”的操作实现。使用该方法可以完成“CAS”型的自旋操作。
         * 
         * @param key
         * @param hash
         * @param oldValue
         * @param newValue
         * @return
         */
        boolean replace(K key, int hash, V oldValue, V newValue) {
            lock();
            try {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                boolean replaced = false;
                if (e != null && oldValue.equals(e.value)) {
                    replaced = true;
                    e.value = newValue;
                    e.remove();
                    e.addBefore(header);
                }
                return replaced;
            } finally {
                unlock();
            }
        }

        /**
         * 替代一个变量，提供一种原子性的“判断并修改”的操作实现。
         * 
         * @param key
         * @param hash
         * @param oldValue
         * @param newValue
         * @return
         */
        V replace(K key, int hash, V newValue) {
            lock();
            try {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    e.value = newValue;
                    e.remove();
                    e.addBefore(header);
                }
                return oldValue;
            } finally {
                unlock();
            }
        }

        /**
         * 将给定的数值安排到hash桶中。
         * 
         * @param key
         * @param hash
         * @param value
         * @param onlyIfAbsent
         * @return
         */
        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            V v = addEntry(key, hash, value, onlyIfAbsent);
            return v;
        }

        /**
         * 新增的方法，初始化一个新的entry，并将该entry加入到lru链表中。
         * 
         * @param tableIndex
         * @param entry
         * @return
         */
        V addEntry(K key, int hash, V value, boolean onlyIfAbsent) {
            lock();
            checkFull();
            try {
                int c = count;
                c++;
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue;
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent)
                        e.value = value;
                    e.remove();
                    e.addBefore(header);
                } else {
                    oldValue = null;
                    ++modCount;
                    e = tab[index] = new HashEntry<K, V>(key, hash, first, value);
                    e.addBefore(header);
                    count = c; // write-volatile
                }
                return oldValue;
            } finally {
                unlock();
            }
        }

        /**
         * 新增的方法，初始化一个新的entry，并将该entry加入到lru链表中。
         * 
         * @param tableIndex
         * @param entry
         * @return
         */
        void checkFull() {
            if (isFull()) {
                HashEntry<K, V> removal = header.after;
                remove(removal.key, removal.hash, removal.value);

                if (debug) {
                    System.out.println("segment is full so remove:" + removal.key);
                }
                removal = null;
            }
        }

        /**
         * 判断该segment是否已经“满了”，此方法的返回值是进行rehash和移除least used entry的依据。
         * 
         * @return
         */
        boolean isFull() {
            return count >= maxSize;
        }

        /**
         * Remove; match on key only if value null, else match both.
         */
        V remove(Object key, int hash, Object value) {
            lock();
            try {
                int c = count;
                c--;
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key))) {
                    e = e.next;
                }

                V oldValue = null;
                if (e != null) {
                    V v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        // All entries following removed node can stay
                        // in list, but all preceding ones need to be
                        // cloned.
                        ++modCount;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next) {
                            newFirst = new HashEntry<K, V>(p.key, p.hash, newFirst, p.value);
                            newFirst.reuseLRU(p);
                        }
                        tab[index] = newFirst;
                        count = c; // write-volatile
                        e.remove();
                    }
                }

                return oldValue;
            } finally {
                unlock();
            }
        }

        void clear() {
            if (count != 0) {
                lock();
                try {
                    HashEntry<K, V>[] tab = table;
                    for (int i = 0; i < tab.length; i++)
                        tab[i] = null;
                    ++modCount;
                    header.before = header.after = header;
                    count = 0; // write-volatile
                } finally {
                    unlock();
                }
            }
        }
    }

    /* ---------------- 公有方法 -------------- */

    /**
     * 创建一个基于lru策略的并发哈希表。
     */
    private ConcurrentLRUHashMap(int maxSize, int concurrencyLevel, boolean debug) {
        if (maxSize < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();

        this.debug = debug;

        if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;

        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = newArray(ssize);

        int c = maxSize / ssize;
        this.maxSize = c;
        if (c * ssize < this.maxSize)
            ++c;
        int cap = 1;
        while (cap < c)
            cap <<= 1;

        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<K, V>(cap);
        }

    }

    public ConcurrentLRUHashMap(int maxSize) {
        this(maxSize, DEFAULT_CONCURRENCY_LEVEL, false);
    }

    @Override
    public boolean isEmpty() {
        final Segment<K, V>[] segments = this.segments;
        /*
         * 每次都必须检查modcounts,防止ABA问题。
         */
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0)
                return false;
            else
                mcsum += mc[i] = segments[i].modCount;
        }
        // If mcsum happens to be zero, then we know we got a snapshot
        // before any modifications at all were made. This is
        // probably common enough to bother tracking.
        if (mcsum != 0) {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount)
                    return false;
            }
        }
        return true;
    }

    /**
     * 获取当前存在的对象总数。
     * 在低并发的情况下，该方法会采用乐观的方式来获取每个segment的总数，并对其验证，当这种方式多次失败后，该方法将升级为锁定，此时会对内部所有segment依次加锁，因而效率相对较低。
     * 
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        final Segment<K, V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                sum += segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
            }
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        check = -1; // force retry
                        break;
                    }
                }
            }
            if (check == sum)
                break;
        }
        /**
         * 必须每次都按同样顺序锁定，否则将导致死锁！
         */
        if (check != sum) { // Resort to locking all segments
            sum = 0;
            for (int i = 0; i < segments.length; ++i)
                segments[i].lock();
            for (int i = 0; i < segments.length; ++i)
                sum += segments[i].count;
            for (int i = 0; i < segments.length; ++i)
                segments[i].unlock();
        }
        if (sum > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else
            return (int) sum;
    }

    @Override
    public V get(Object key) {
        int hash = hash(key.hashCode());
        V v = segmentFor(hash).get(key, hash);
        return v;
    }

    @Override
    public boolean containsKey(Object key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).containsKey(key, hash);
    }

    /**
     * 判断对象是否在集合中，该方法会对内部分区依次加锁，因此相对会较“慢”。另外，该结果是“弱一致性“的。
     */
    @Override
    @SuppressWarnings("unused")
    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();

        // See explanation of modCount use above

        final Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];

        // Try a few times without locking
        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k) {
            int sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                int c = segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
                if (segments[i].containsValue(value))
                    return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    int c = segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep)
                return false;
        }
        // Resort to locking all segments
        for (int i = 0; i < segments.length; ++i)
            segments[i].lock();
        boolean found = false;
        try {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].containsValue(value)) {
                    found = true;
                    break;
                }
            }
        } finally {
            for (int i = 0; i < segments.length; ++i)
                segments[i].unlock();
        }
        return found;
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        V v = segmentFor(hash).put(key, hash, value, false);
        return v;
    }

    /**
     * 打印数据结构细节，调试方法。
     */
    public void showDetail() {
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < segments.length; i++) {
            bf.append("segment" + i + " length is:" + segments[i].count);
            bf.append("\n");
            bf.append("table length is: " + segments[i].table.length);
            bf.append("\n");
            for (int j = 0; j < segments[i].table.length; j++) {
                if (segments[i].table[j] != null) {
                    int buckSize = 0;
                    HashEntry<K, V> next = segments[i].table[j].next;
                    while (next != null) {
                        buckSize++;
                        next = next.next;
                    }
                    bf.append("bucksize " + j + " length is: " + buckSize);
                    bf.append("\n");
                }
            }
        }
        bf.append("------------------------");
        bf.append("\n");
        System.out.println(bf.toString());
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).put(key, hash, value, true);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).remove(key, hash, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean remove(Object key, Object value) {
        int hash = hash(key.hashCode());
        if (value == null)
            return false;
        return segmentFor(hash).remove(key, hash, value) != null;
    }

    /**
     * 根据旧的值，来确定是否更新。该方法提供了原子性的“判断并更新”的操作，使用者可以结合自旋来完成并发的修改。
     * 
     * @throws NullPointerException 如果有某项参数为null。
     * @return 更新的结果，成功为true，失败为false。
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, value);
    }

    /**
     * 清除所有映射关系。
     */
    @Override
    public void clear() {
        for (int i = 0; i < segments.length; ++i)
            segments[i].clear();
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa. The set supports element
     * removal, which removes the corresponding mapping from this map,
     * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>
     * , and <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     * <p>
     * The view's <tt>iterator</tt> is a "weakly consistent" iterator that will never throw
     * {@link ConcurrentModificationException}, and guarantees to traverse elements as they existed
     * upon construction of the iterator, and may (but is not guaranteed to) reflect any
     * modifications subsequent to construction.
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }

    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    public Enumeration<V> elements() {
        return new ValueIterator();
    }

    /* ---------------- 迭代器支持 -------------- */

    abstract class HashIterator {

        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K, V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;

        HashIterator() {
            nextSegmentIndex = segments.length - 1;
            nextTableIndex = -1;
            advance();
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        final void advance() {
            if (nextEntry != null && (nextEntry = nextEntry.next) != null)
                return;

            while (nextTableIndex >= 0) {
                if ((nextEntry = currentTable[nextTableIndex--]) != null)
                    return;
            }

            while (nextSegmentIndex >= 0) {
                Segment<K, V> seg = segments[nextSegmentIndex--];
                if (seg.count != 0) {
                    currentTable = seg.table;
                    for (int j = currentTable.length - 1; j >= 0; --j) {
                        if ((nextEntry = currentTable[j]) != null) {
                            nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }

        public boolean hasNext() {
            return nextEntry != null;
        }

        HashEntry<K, V> nextEntry() {
            if (nextEntry == null)
                throw new NoSuchElementException();
            lastReturned = nextEntry;
            advance();
            return lastReturned;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            ConcurrentLRUHashMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<K>, Enumeration<K> {

        @Override
        public K next() {
            return super.nextEntry().key;
        }

        @Override
        public K nextElement() {
            return super.nextEntry().key;
        }
    }

    final class ValueIterator extends HashIterator implements Iterator<V>, Enumeration<V> {

        @Override
        public V next() {
            return super.nextEntry().value;
        }

        @Override
        public V nextElement() {
            return super.nextEntry().value;
        }
    }

    @SuppressWarnings("serial")
    final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {

        WriteThroughEntry(K k, V v) {
            super(k, v);
        }

        @Override
        public V setValue(V value) {
            if (value == null)
                throw new NullPointerException();
            V v = super.setValue(value);
            ConcurrentLRUHashMap.this.put(getKey(), value);
            return v;
        }
    }

    final class EntryIterator extends HashIterator implements Iterator<Entry<K, V>> {

        @Override
        public Map.Entry<K, V> next() {
            HashEntry<K, V> e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }

    final class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return ConcurrentLRUHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return ConcurrentLRUHashMap.this.remove(o) != null;
        }

        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
    }

    final class Values extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return ConcurrentLRUHashMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V v = ConcurrentLRUHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return ConcurrentLRUHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public int size() {
            return ConcurrentLRUHashMap.this.size();
        }

        @Override
        public void clear() {
            ConcurrentLRUHashMap.this.clear();
        }
    }

//    public static void main(String[] args) {
//        ConcurrentLRUHashMap<String, String> m = new ConcurrentLRUHashMap<String, String>(60);
//        for (int i = 1; i <= 60; i++) {
//            m.put(i + "", i + "");
//
//        }
//        for (int i = 61; i <= 7; i++) {
//            m.put(i + "", i + "");
//        }
//        for (Iterator<String> it = m.keySet().iterator(); it.hasNext();) {
//            System.out.println(it.next());
//        }
//
//        System.out.println("****************");
//
//
//
//    }
}
