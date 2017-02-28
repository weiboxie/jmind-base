package jmind.base.lang.shard;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jmind.base.algo.Hashing;
import jmind.base.util.SafeEncoder;

/**
 *  一致性Hash，相同参数的请求总是发到同一提供者。
    当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
    算法参见：http://en.wikipedia.org/wiki/Consistent_hashing。
 * @author wbxie
 * 2014-1-26
 * @param <T>
 */
public class ConsistentHashLoadBalance<T> extends LoadBalance<T> {

    private final TreeMap<Long, T> nodes;
    private final Hashing algo;

    public ConsistentHashLoadBalance(List<T> shards) {
        this(shards, Hashing.MURMUR_HASH); // MD5 is really not good as we works
        // with 64-bits not 128
    }

    public ConsistentHashLoadBalance(List<T> shards, Hashing algo) {
        super(shards);
        nodes = new TreeMap<Long, T>();
        this.algo = algo;
        if (shards.size() > 1) {
            initialize(shards);
        }

    }

    private void initialize(List<T> shards) {
        for (int i = 0; i < shards.size(); i++) {
            for (int n = 0; n < 160; n++) {
                nodes.put(this.algo.hash("SHARD-" + i + "-NODE-" + n), shards.get(i));
            }
        }

    }

    public T getShard(byte[] key) {
        SortedMap<Long, T> tail = nodes.tailMap(algo.hash(key));
        if (tail.isEmpty()) {
            return nodes.get(nodes.firstKey());

        }
        return tail.get(tail.firstKey());
    }

    public T getShard(String key) {
        return getShard(SafeEncoder.encode(key));

    }

}
