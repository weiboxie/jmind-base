package jmind.base.lang.shard;

import jmind.base.algo.HashAlgorithms;

import java.util.List;

/**
 * time33 hash 算法
 * Created by xieweibo on 2016/11/10.
 */
public class Time33HashLoadBalance<T> extends LoadBalance<T> {


    public Time33HashLoadBalance(List<T> shards) {
        super(shards);
    }

    @Override
    public T getShard(String key) {
        int index = Math.abs(HashAlgorithms.time33(key)) % getShards().size();
        return getShards().get(index);
    }
}
