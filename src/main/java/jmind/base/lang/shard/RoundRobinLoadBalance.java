package jmind.base.lang.shard;

import java.util.List;

import jmind.base.algo.atomic.AtomicPositiveInteger;

/**
 * 轮询策略
 *  轮循，按公约后的权重设置轮循比率。
    存在慢的提供者累积请求问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。
 * @author wbxie
 * 2014-1-26
 * @param <T>
 */
public class RoundRobinLoadBalance<T> extends LoadBalance<T> {

    private final AtomicPositiveInteger count;

    public RoundRobinLoadBalance(List<T> shards) {
        super(shards);
        count = new AtomicPositiveInteger();
    }

    @Override
    public T getShard(String key) {
        int index = count.getAndIncrement() % getShards().size();
        return getShards().get(index);
    }

}
