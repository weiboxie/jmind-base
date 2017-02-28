package jmind.base.collect;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 基于ArrayBlockingQueue定长队列
 * @author weibo-xie
 * 2012-4-24
 * @param <E>
 */
public class FixedArrayBlockingQueue<E> extends ArrayBlockingQueue<E> {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    public FixedArrayBlockingQueue(int capacity) {
        super(capacity);
    }

    /**
     * 添加元素。如果已满，剔除最早的
     */
    @Override
    public boolean offer(E e) {
        // 将指定的元素插入到此队列的尾部（如果立即可行且不会超过该队列的容量），在成功时返回 true，如果此队列已满，则返回 false。
        if (!super.offer(e)) {
            super.poll();
            return offer(e);
        }
        return true;
    }

    public boolean add(E e) {
        return offer(e);
    }

    public static void main(String[] args) {
        FixedArrayBlockingQueue<Integer> list = new FixedArrayBlockingQueue<Integer>(10);
        for (int i = 1; i < 50; i++) {
            list.offer(i);
        }
        System.out.println(list);
        Iterator<Integer> iterator = list.iterator();
        for (int i = 100; i < 200; i++) {
            list.offer(i);
        }
        System.out.println(iterator.next());
        String s = "sdsd\naaaaaaaaaaasddfdsd";
        System.out.println(s.replace("\n", ""));
        System.out.println(s);

    }

}
