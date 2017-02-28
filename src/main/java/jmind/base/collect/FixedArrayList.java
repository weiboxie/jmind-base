package jmind.base.collect;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 固定长度的arraylist
 * @author weibo-xie
 * 2011-12-26
 * @param <E>
 */
public class FixedArrayList<E> extends ArrayList<E> {

    private int capacity;
    private boolean add = true;

    public FixedArrayList(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 
     * @param capacity
     * @param add 当到达指定长度时，是否添加到list ，是添加进去，删除第一个
     */
    public FixedArrayList(int capacity, boolean add) {
        this.capacity = capacity;
        this.add = add;

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(E obj) {

        if (this.size() < capacity) {
            super.add(obj);
            return true;
        }
        if (add) {
            super.remove(0);
            super.add(obj);
        }
        return false;
    }

    public static void main(String[] args) {
        FixedArrayList<Integer> list = new FixedArrayList<Integer>(10);
        for (int i = 1; i < 50; i++) {
            list.add(i);
        }
        System.out.println(list);
        Object[] array = list.toArray();
        for (int i = 100; i < 200; i++) {
            list.add(i);
        }
        System.out.println(Arrays.asList(array));
    }
}
