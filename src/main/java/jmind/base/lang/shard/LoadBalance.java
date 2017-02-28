package jmind.base.lang.shard;

import java.util.List;

import jmind.base.util.RandUtil;

/**
 * 负载均衡策略
 * @author wbxie
 * 2014-1-26
 * @param <T>
 */
public abstract class LoadBalance<T> {

    public static enum Balance {
        RoundRobin, Hash,Time33
    }

    public static <T> LoadBalance<T> create(List<T> shards,Balance balance){
        if(Balance.RoundRobin==balance){
            return new RoundRobinLoadBalance<T>(shards);
        }else if(Balance.Hash==balance){
            return  new ConsistentHashLoadBalance<T>(shards);
        }else{
            return new Time33HashLoadBalance<T>(shards);
        }
    }

    private final List<T> shards;

    public LoadBalance(List<T> shards) {
        this.shards = shards;
    }

    public abstract T getShard(String key);

    public final List<T> getShards() {
        return shards;
    }

    public final T getFisrt() {
        return shards.get(0);
    }

    public boolean removeShard(T obj) {
        return shards.remove(obj);
    }

    public T getRandom() {
        return shards.get(RandUtil.nextInt(shards.size()));
    }

}
