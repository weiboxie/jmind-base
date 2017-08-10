package jmind.base.algo.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * http://agapple.iteye.com/blog/1183972
    需要的是一个类似于信号量的PV控制
    具有的读写锁的，读线程可以不互相影响，写线程拥有最高的抢占权，可以不理会读线程是否在操作
    支持线程中断 (worker线程需要允许cancel)

因此本文的互斥信号(BooleanMutex)就应运而生，它是信号量(Semaphore)的一个变种，加入了读锁的特性。比如在状态为1时可以一直得到响应，对应的P操作不会消费对应的资源
实现一个互斥实现，基于Cocurrent中的AbstractQueuedSynchronizer实现了自己的sync
应用场景：系统初始化/授权控制，没有权限阻塞等待。有权限时所用线程都可以快速通过

 false： 代表需要被阻塞挂机，等待mutex变为true被唤醒
 true： 唤醒被阻塞在flase状态下的thread

 * @author xieweibo
 * @date 2015年12月6日
 */
public class BooleanMutex {

    private Sync sync;

    public BooleanMutex() {
        sync = new Sync();
        set(false);
    }

    public BooleanMutex(Boolean mutex) {
        sync = new Sync();
        set(mutex);
    }

    /**
     * 阻塞等待Boolean为true
     * 
     * @throws InterruptedException
     */
    public void get() throws InterruptedException {
        sync.innerGet();
    }

    /**
     * 阻塞等待Boolean为true,允许设置超时时间
     * @param timeout
     * @param unit
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        sync.innerGet(unit.toNanos(timeout));
    }

    /**
     * 重新设置对应的Boolean mutex
     * 
     * @param mutex
     */
    public void set(Boolean mutex) {
        if (mutex) {
            sync.innerSetTrue();
        } else {
            sync.innerSetFalse();
        }
    }

    public boolean state() {
        return sync.innerState();
    }

    /**
     * Synchronization control for BooleanMutex. Uses AQS sync state to
     * represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -7828117401763700385L;

        /** State value representing that TRUE */
        private static final int TRUE = 1;
        /** State value representing that FALSE */
        private static final int FALSE = 2;

        private boolean isTrue(int state) {
            return (state & TRUE) != 0;
        }

        /**
         * 实现AQS的接口，获取共享锁的判断
         */
        protected int tryAcquireShared(int state) {
            // 如果为true，直接允许获取锁对象
            // 如果为false，进入阻塞队列，等待被唤醒
            return isTrue(getState()) ? 1 : -1;
        }

        /**
         * 实现AQS的接口，释放共享锁的判断
         */
        protected boolean tryReleaseShared(int ignore) {
            //始终返回true，代表可以release
            return true;
        }

        boolean innerState() {
            return isTrue(getState());
        }

        void innerGet() throws InterruptedException {
            acquireSharedInterruptibly(0);
        }

        void innerGet(long nanosTimeout) throws InterruptedException, TimeoutException {
            if (!tryAcquireSharedNanos(0, nanosTimeout))
                throw new TimeoutException();
        }

        void innerSetTrue() {
            for (;;) {
                int s = getState();
                if (s == TRUE) {
                    return; //直接退出
                }
                if (compareAndSetState(s, TRUE)) {// cas更新状态，避免并发更新true操作
                    releaseShared(0);//释放一下锁对象，唤醒一下阻塞的Thread
                }
            }
        }

        void innerSetFalse() {
            for (;;) {
                int s = getState();
                if (s == FALSE) {
                    return; //直接退出
                }
                if (compareAndSetState(s, FALSE)) {//cas更新状态，避免并发更新false操作
                    setState(FALSE);
                }
            }
        }

    }
}
