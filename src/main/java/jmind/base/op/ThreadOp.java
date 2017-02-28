package jmind.base.op;

import java.util.concurrent.CountDownLatch;

import jmind.base.algo.atomic.StatsCounter;
import jmind.base.util.MethodUtil;
import jmind.base.util.TaskExecutor;

public abstract class ThreadOp {

    protected final StatsCounter counter = new StatsCounter();

    public static void main(String[] args) {
        //ThreadOp.doThis(10, 10, ContentAuditServerServiceImpl.class, "checkContent", "地方撒发生大幅! 胡#景*涛!裸聊无罪",1);
    }

    public abstract long exe();

    public long doThis(final int threadCount, final int times, final Class<?> ownerClass, final String methodName,
            final Object... args) {
        long start = System.currentTimeMillis();
        final CountDownLatch cdlWorker = new CountDownLatch(threadCount * times);
        for (int i = 0; i < threadCount; i++) {

            Runnable task = new Runnable() {
                public void run() {
                    try {
                        for (int j = 0; j < times; j++) {
                            long startTime = System.currentTimeMillis();
                            MethodUtil.invokeMethod(ownerClass, methodName, args);
                            counter.recordSuccess(startTime, 200);
                            cdlWorker.countDown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            TaskExecutor.getInstance().runTask(task);
        }
        try {
            cdlWorker.await();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        long time = (System.currentTimeMillis() - start) / 1000;
        System.out.println("执行完成，费时：" + time + " 秒，启动" + threadCount + "个线程，每个线程执行" + times + " 次");
        return time;
    }

    /**
     * 返回总执行秒数
     * @param threadCount
     * @param times
     * @return
     */
    public long doit(final int threadCount, final int times) {
        long start = System.currentTimeMillis();
        final CountDownLatch cdlWorker = new CountDownLatch(threadCount * times);
        for (int i = 0; i < threadCount; i++) {
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        for (int j = 0; j < times; j++) {
                            long startTime = System.currentTimeMillis();
                            exe();
                            counter.recordSuccess(startTime, 200);
                            cdlWorker.countDown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            TaskExecutor.getInstance().runTask(task);
        }

        try {
            cdlWorker.await();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        long time = (System.currentTimeMillis() - start);
        System.out.println("执行完成，费时：" + time + " 毫秒，启动" + threadCount + "个线程，每个线程执行" + times + " 次");
        return time;
    }

}
