package jmind.base.lang;

import java.util.concurrent.atomic.AtomicInteger;

import jmind.base.util.DateUtil;

/**
 * 有效时间错误记录，过期时间秒
 * @author xieweibo
 * @date 2016年1月20日
 */
public class ExpireRecord implements Record {

    private final AtomicInteger counter = new AtomicInteger();
    private volatile int lastExcetionTime;
    private int expireSecond;
    private String message;

    public ExpireRecord(int expireSecond) {
        this.expireSecond = expireSecond;
    }

    private final boolean isExprie() {
        return (DateUtil.getCurrentSeconds() - lastExcetionTime) > expireSecond;
    }

    public void ExceptionRecord(Exception e, Object... args) {
        if (isExprie()) {
            counter.set(1);
        } else {
            counter.incrementAndGet();
        }
        lastExcetionTime = DateUtil.getCurrentSeconds();
        message = e.getMessage();
    }

    public int getExprieExcetionCount() {
        return isExprie() ? 0 : counter.get();
    }

    public String toString() {
        return "message=" + message + ",lastExcetionTime=" + lastExcetionTime + ",conuter=" + counter.get();
    }
}
