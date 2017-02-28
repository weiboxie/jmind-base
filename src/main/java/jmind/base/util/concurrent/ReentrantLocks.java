package jmind.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 构造并发可重入的互斥锁
 * @author xieweibo
 * @date 2015年6月3日
 */
public class ReentrantLocks {
    private ReentrantLocks() {

    }

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();

    private static final ReentrantLocks instance = new ReentrantLocks();

    public static final ReentrantLocks getInstance() {
        return instance;
    }

    public ReentrantLock getLock(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock == null) {
            lock = new ReentrantLock();
            ReentrantLock lock2 = lockMap.putIfAbsent(key, lock);
            if (lock2 != null) {
                lock = lock2;
            }
        }
        return lock;
    }

}
