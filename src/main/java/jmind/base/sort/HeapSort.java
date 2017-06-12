package jmind.base.sort;

/**
 * 最大堆排序
 * http://mp.weixin.qq.com/s/LO2yX4dBjgfA0l5MQ59L2Q
 *堆排序（heapsort）是一种比较快速的排序方式，它的时间复杂度为O(nlgn)，而且堆排序具有空间原址性：即任何时候只需要有限（常数个）的空间来存储临时数据。而且堆排序还被应用在构造优先级队列中，本文将会用Java实现一个最大堆，并利用最大堆实现优先级队列。

 最大堆的性质

 1、是一棵近似的完全二叉树，除了最底层，其它是全满，且从左向右填充。
 2、树的每个节点对应数组一个元素，根节点对应数组下标0元素。
 3、对于下标i，它的父节点下标为(i + 1) / 2 - 1，左孩子节点下标为i * 2 + 1，右孩子节点下标为i * 2 + 2。
 4、最大堆中，每个结点都必须大于等于左右孩子节点。
 *
 * https://github.com/lufficc/Algorithm/blob/master/src/com/lufficc/algorithm/Sort/HeapSort/HeapSort.java
 *
 * Created by xieweibo on 2017/3/13.
 */
public class HeapSort {


    private int[] a ;

    public  HeapSort(int[] a) {
         this.a=a;
        for (int i = a.length / 2 + 1; i >= 0; i--) {
            maxHeapify(a, i);
        }
        int length = a.length;
        for (int i = a.length - 1; i >= 1; i--) {
            swap(a, i, 0);
            length--;
            maxHeapify(a, 0, length);
        }
    }



    /**
     * 维持最大堆
     *
     * @param a
     * @param i
     * @param length
     */
    private void maxHeapify(int[] a, int i, int length) {
        int l = left(i);
        int r = right(i);
        int max;
        if (l < length && a[l] > a[i]) {
            max = l;
        } else {
            max = i;
        }
        if (r < length && a[r] > a[max]) {
            max = r;
        }
        if (max != i) {
            swap(a, i, max);
            maxHeapify(a, max, length);
        }
    }

    private void maxHeapify(int[] a, int i) {
        maxHeapify(a, i, a.length);
    }

    private int left(int i) {
        return i * 2 + 1;
    }

    private int right(int i) {
        return i * 2 + 2;
    }

    private int parent(int i) {
        return (i + 1) / 2 - 1;
    }

    private void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    //max-priority-queue  返回堆的最大值
    public int maximum() {
        return a[0];
    }
    // 返回堆的最大值并删除
    public int extractMax() {
        int max = a[0];
        a[0] = a[a.length - 1];
        int[] newA = new int[a.length - 1];
        System.arraycopy(a, 0, newA, 0, newA.length);
        maxHeapify(newA, 0, newA.length);
        return max;
    }
    // 降下标为i的元素增大为key
    public void heapIncreaseKey(int i, int key) {
        if (key > a[i]) {
            a[i] = key;
            while (i > 0 && a[parent(i)] < a[i]) {
                swap(a, parent(i), i);
                i = parent(i);
            }
        } else {
            throw new IllegalArgumentException("key is too small");
        }
    }
   // 降元素key插入堆中
    public void maxHeapInsert(int key) {
        int[] newA = new int[a.length + 1];
        System.arraycopy(a, 0, newA, 0, a.length);
        newA[newA.length - 1] = Integer.MIN_VALUE;
        this.a = newA;
        heapIncreaseKey(a.length - 1, key);
    }
}