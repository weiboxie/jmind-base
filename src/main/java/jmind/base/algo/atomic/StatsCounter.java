package jmind.base.algo.atomic;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class StatsCounter {

    private final LongAdder slowCount = new LongAdder();
    private final LongAdder successCount =  new LongAdder();
    private final LongAdder exceptionCount =  new LongAdder();
    private final LongAdder totaSuccessTime =  new LongAdder();
    private final LongAdder totalExceptionTime =  new LongAdder();
    private volatile long min = Integer.MAX_VALUE;
    private volatile long max;

    public void recordSuccess(long startTime, int slowTime) {
        successCount.increment();
        long t = System.currentTimeMillis() - startTime;
        totaSuccessTime.add(t);
        if (min > t && t > 0) {
            min = t;
        }
        if (max < t) {
            max = t;
        }
        if (t > slowTime) {
            slowCount.increment();
        }
    }

    public void recordException(long startTime) {
        exceptionCount.increment();
        totalExceptionTime.add(System.currentTimeMillis() - startTime);
    }

    public long getExceptionCount() {
        return exceptionCount.sum();
    }

    /**
     * 统计快照
     * @return
     */
    public SCount snapshot() {
        SCount counter = new SCount();
        counter.successCount = this.successCount.sum();
        counter.exceptionCount = this.exceptionCount.sum();
        counter.totalSuccessTime = this.totaSuccessTime.sum();
        counter.totalExceptionTime = this.totalExceptionTime.sum();
        counter.min = this.min;
        counter.max = this.max;
        counter.slowCount = this.slowCount.sum();
        return counter;
    }

    public String toString() {
        Map<String, Number> map = new LinkedHashMap<String, Number>();
        long successCount = this.successCount.sum();
        long exceptionCount = this.exceptionCount.sum();
        long totaSuccessTime = this.totaSuccessTime.sum();
        long totalExceptionTime = this.totalExceptionTime.sum();
        map.put("successCount", successCount);
        map.put("successAvg", (double) totaSuccessTime / successCount);
        map.put("exceptionCount", exceptionCount);
        map.put("exceptionAvg", (double) totalExceptionTime / exceptionCount);
        map.put("sum", successCount + exceptionCount);
        double sumAvg = (double) (totaSuccessTime + totalExceptionTime) / (successCount + exceptionCount);
        map.put("sumAvg", sumAvg);
        map.put("min", min);
        map.put("max", max);
        map.put("slow", slowCount.sum());
        return map.toString();
    }

    public static class SCount implements Serializable {

        private Object key;
        private long successCount; // 成功次数
        private long exceptionCount; //失败次数
        private long totalSuccessTime; //总成功时间
        private long totalExceptionTime; // 总失败时间
        private long min;
        private long max;
        private long slowCount;

        public Object getKey() {
            return key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public long getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(long successCount) {
            this.successCount = successCount;
        }

        public long getExceptionCount() {
            return exceptionCount;
        }

        public void setExceptionCount(long exceptionCount) {
            this.exceptionCount = exceptionCount;
        }

        public long getTotalSuccessTime() {
            return totalSuccessTime;
        }

        public void setTotalSuccessTime(long totalSuccessTime) {
            this.totalSuccessTime = totalSuccessTime;
        }

        public long getTotalExceptionTime() {
            return totalExceptionTime;
        }

        public void setTotalExceptionTime(long totalExceptionTime) {
            this.totalExceptionTime = totalExceptionTime;
        }

        public double getSuccessAvg() {
            return (double) totalSuccessTime / successCount;
        }

        public double getExceptionAvg() {
            return (double) totalExceptionTime / exceptionCount;
        }

        public long getSum() {
            return successCount + exceptionCount;
        }

        public double getSumAvg() {
            return (double) (totalSuccessTime + totalExceptionTime) / (successCount + exceptionCount);
        }

        public long getMin() {
            return min;
        }

        public void setMin(long min) {
            this.min = min;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }

        public long getSlowCount() {
            return slowCount;
        }

        public void setSlowCount(long slowCount) {
            this.slowCount = slowCount;
        }

        public String toString() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("key",key);
            map.put("successCount", successCount);
            map.put("successAvg", (double) totalSuccessTime / successCount);
            map.put("exceptionCount", exceptionCount);
            map.put("exceptionAvg", (double) totalExceptionTime / exceptionCount);
            map.put("sum", successCount + exceptionCount);
            double sumAvg = (double) (totalSuccessTime + totalExceptionTime) / (successCount + exceptionCount);
            map.put("sumAvg", sumAvg);
            map.put("min", min);
            map.put("max", max);
            map.put("slowCount", slowCount);
            return map.toString();
        }

    }

}
