package jmind.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具
 * @author weiboxie
 *
 */
public class TaskExecutor {

    private TaskExecutor() {
    }

    private static final TaskExecutor rte = new TaskExecutor();
    //Executors.newFixedThreadPool(nThreads)

    // private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final ExecutorService executor = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    //  单线程  
    private final ExecutorService simpleExe = Executors.newSingleThreadExecutor();

    public static TaskExecutor getInstance() {
        return rte;
    }

    public void runSimpleTask(Runnable task) {

        simpleExe.execute(task);
    }

    public void runTask(Runnable task) {
        executor.execute(task);
    }

    public void runTask(final Class<?> ownerClass, final String methodName, final Object... args) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    MethodUtil.invokeMethod(ownerClass, methodName, args);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        executor.execute(task);

    }

    /**
     * 异步执行一个任务
     *
     * @param task 任务
     * @return 任务结果
     */
    public <V> Future<V> submit(Callable<V> task) {
        return executor.submit(task);
    }

    /**
     * 异步执行，超时会停止任务
     * @param <T>
     * @param task
     * @param timeout 超时时间为秒
     * @return
     */
    public <T> T submit(Callable<T> task, int timeout) {

        Future<T> future = executor.submit(task);
        T t;
        try {
            t = future.get(timeout, TimeUnit.SECONDS);
            return t;

        } catch (Exception e) {
            e.printStackTrace();
            future.cancel(true);
            task = null;
            t = null;
        }
        return null;
    }

    /**
     * 批量执行任务
     * 超时间为秒
     * @param callables
     * @param timeout
     * @return
     */
    public <V> List<V> submit(List<Callable<V>> callables, long timeout) {

        List<Future<V>> futures = new ArrayList<Future<V>>();
        List<V> result = new ArrayList<V>();

        try {
            futures = executor.invokeAll(callables, timeout, TimeUnit.SECONDS);

            for (Future<V> future : futures) {
                try {
                    V v = future.get();
                    if (v != null)
                        result.add(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 批量执行任务，返回结果
     * @param callables
     * @return
     */
    public <V> List<V> submit(List<Callable<V>> callables) {

        List<Future<V>> futures = new ArrayList<Future<V>>();
        List<V> result = new ArrayList<V>();

        try {
            futures = executor.invokeAll(callables);

            for (Future<V> future : futures) {
                try {
                    V v = future.get();
                    if (v != null)
                        result.add(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        if (null != executor) {
            executor.shutdown();
        }
    }

}
