package jmind.base.util;

import java.util.concurrent.*;

/**
 * 延时执行线程池
 * @author weibo-xie
 * 2012-4-11
 */
public class DelayTaskExecutor {

    private DelayTaskExecutor() {
    }

    private static final DelayTaskExecutor rte = new DelayTaskExecutor();
    //  single thread  同时执行的task 只有一个   Executors.newScheduledThreadPool(20);
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static DelayTaskExecutor getInstance() {

        return rte;
    }

    public void runTask(Runnable task) {
        executor.execute(task);
    }

    /**
     * 延时多少分钟执行
     * @param command
     * @param delay
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay) {
       return executor.schedule(command, delay, TimeUnit.MINUTES);
    }

    /**
     * @param command 要执行的任务
     * @param delay 从现在开始延迟执行的时间
     * @param unit 延迟参数的时间单位 
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      return  executor.schedule(command, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit){
       return executor.schedule(callable,delay,unit);
    }

    /**
     * 创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；
     * 也就是将在 initialDelay 后开始执行，然后在 initialDelay+period 后执行，接着在 initialDelay + 2 * period 后执行，依此类推。
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit){
       return executor.scheduleAtFixedRate(command, initialDelay, period, unit);

    }

    /**
     *  创建并执行一个在给定初始延迟后首次启用的定期操作，随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit){
       return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }


}
