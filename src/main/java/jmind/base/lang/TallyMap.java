package jmind.base.lang;

import java.util.HashMap;
import java.util.Map;

;
/**
 * 计数的map，如果不存在则添加，如果存在则增加计数,非线程安全
 * 
 * 一键多值的Map
 * @see  Multimap ArrayListMultimap
 * BiMap：双向Map
 * @author weibo-xie
 * 2012-1-31
 * @param <K>
 */
public class TallyMap<K> extends HashMap<K, Integer> {

    public TallyMap(Map<K, Integer> m) {
        super(m);
    }

    public TallyMap() {
        super();
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param key
     * @param value 增加的值
     * @return
     */
    public Integer add(K key, Integer value) {
        Integer v = this.get(key);
        if (v != null) {
            value += v;
        }
        this.put(key, value);
        return value;
    }

    /**
     * 默认增加值为1
     * @param key
     * @return
     */
    public Integer add(K key) {
        return this.add(key, 1);
    }

}
