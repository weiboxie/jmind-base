package jmind.base.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * http://blog.csdn.net/sunp823/article/details/49886051
 * 自定义自旋锁
 *自旋锁的概念
 首先是一种锁，与互斥锁相似，基本作用是用于线程（进程）之间的同步。
 与普通锁不同的是，一个线程A在获得普通锁后，如果再有线程B试图获取锁，
 那么这个线程B将会挂起（阻塞）；试想下，如果两个线程资源竞争不是特别激烈，
 而处理器阻塞一个线程引起的线程上下文的切换的代价高于等待资源的代价的时候（锁的已保持者保持锁时间比较短），
 那么线程B可以不放弃CPU时间片，而是在“原地”忙等，直到锁的持有者释放了该锁，这就是自旋锁的原理，
 可见自旋锁是一种非阻塞锁。


 自旋锁可能引起的问题：
 1.过多占据CPU时间：如果锁的当前持有者长时间不释放该锁，那么等待者将长时间的占据cpu时间片，导致CPU资源的浪费，
 因此可以设定一个时间，当锁持有者超过这个时间不释放锁时，等待者会放弃CPU时间片阻塞；
 2.死锁问题：试想一下，有一个线程连续两次试图获得自旋锁（比如在递归程序中），
 第一次这个线程获得了该锁，当第二次试图加锁的时候，检测到锁已被占用（其实是被自己占用），
 那么这时，线程会一直等待自己释放该锁，而不能继续执行，这样就引起了死锁。
 因此递归程序使用自旋锁应该遵循以下原则：递归程序决不能在持有自旋锁时调用它自己，也决不能在递归调用时试图获得相同的自旋锁。
 * Created by xieweibo on 2017/5/3.
 */
public  class SpinLock {
    //java中原子（CAS）操作
    AtomicReference<Thread> owner = new AtomicReference<Thread>();//持有自旋锁的线程对象

    public void lock() {
        Thread cur = Thread.currentThread();
        //lock函数将owner设置为当前线程，并且预测原来的值为空。unlock函数将owner设置为null，并且预测值为当前线程。当有第二个线程调用lock操作时由于owner值不为空，
        // 导致循环一直被执行，直至第一个线程调用unlock函数将owner设置为null，第二个线程才能进入临界区。
        while (!owner.compareAndSet(null, cur)){
        }
    }
    public void unLock() {
        Thread cur = Thread.currentThread();
        owner.compareAndSet(cur, null);
    }

}