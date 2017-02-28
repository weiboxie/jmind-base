package jmind.base.algo.atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xieweibo on 2016/12/6.
 */
public class StatsCounterMap<K> extends ConcurrentHashMap<K, StatsCounter> {
    /**
     * 必须获得，没有new 一个
     *
     * @param k
     * @return
     */
    public StatsCounter mustGet(K k) {
        StatsCounter statsCounter = get(k);
        if (statsCounter == null) {
            statsCounter = new StatsCounter();
            StatsCounter old = this.putIfAbsent(k, statsCounter);
            if (old != null) {
                statsCounter= old;
            }
        }
        return statsCounter;
    }

    public List<StatsCounter.SCount> stat() {
        List<StatsCounter.SCount> list = new ArrayList<>();
        long sumSucc = 0, sumSuccTime = 0, sumException = 0, sumExcetionTime = 0, slowCount = 0;
        for (Entry<K, StatsCounter> entry : entrySet()) {
            StatsCounter.SCount counter = entry.getValue().snapshot();
            counter.setKey(entry.getKey());
            list.add(counter);
            sumSucc += counter.getSuccessCount();
            sumSuccTime += counter.getTotalSuccessTime();
            sumException += counter.getExceptionCount();
            sumExcetionTime += counter.getTotalExceptionTime();
            slowCount += counter.getSlowCount();
        }
        StatsCounter.SCount counter = new StatsCounter.SCount();
        counter.setKey("total");
        counter.setSuccessCount(sumSucc);
        counter.setTotalSuccessTime(sumSuccTime);
        counter.setExceptionCount(sumException);
        counter.setTotalExceptionTime(sumExcetionTime);
        counter.setSlowCount(slowCount);
        list.add(counter);
        return list;
    }


}
