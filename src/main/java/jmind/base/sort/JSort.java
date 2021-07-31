package jmind.base.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java程序员必知的8大排序 http://developer.51cto.com/art/201206/345156.htm
 * 
 * @author Administrator
 * 
 */
public class JSort {
    public static void main(String[] args) {
        int a[] = { 49, 38, 65, 97, 76, 13, 27, 49, 78, 34, 12, 64, 5, 4, 62, 99, 98, 54, 56, 17, 18, 23, 34, 15, 35, 25, 53, 51 };
        System.err.println(a.length);
        shellSort(a);
        System.err.println(Arrays.toString(a));

        bitMapSort(a);


    }

    // 插入排序：直接插入排序
    /**
     * 基本思想：在要排序的一组数中，假设前面(n-1)[n>=2] 个数已经是排

    好顺序的，现在要把第n个数插到前面的有序数中，使得这n个数

    也是排好顺序的。如此反复循环，直到全部排好顺序。
     * @param a
     */
    public static void insertSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int j = i - 1;
            int temp = a[i];
            for (; j >= 0 && temp < a[j]; j--) {
                System.out.println("j=" + j);
                a[j + 1] = a[j];
            }

            a[j + 1] = temp;
        }
    }

    // 插入排序：希尔排序
    public static void shellSort(int[] a) {
        double d1 = a.length;
        int temp = 0;
        while (true) {
            d1 = Math.ceil(d1 / 2);
            int d = (int) d1;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < a.length; i += d) {
                    int j = i - d;
                    temp = a[i];
                    for (; j >= 0 && temp < a[j]; j -= d) {
                        a[j + d] = a[j];
                    }
                    a[j + d] = temp;
                }
            }
            if (d == 1)
                break;
        }

    }

    // 选择排序：简单选择排序
    /**
     *  基本思想：在要排序的一组数中，选出最小的一个数与第一个位置的数交换；

    然后在剩下的数当中再找最小的与第二个位置的数交换，如此循环到倒数第二个数和最后一个数比较为止。
     * @param a
     */
    public static void selectSort(int[] a) {

        int position = 0;
        for (int i = 0; i < a.length; i++) {

            int j = i + 1;
            position = i;
            int temp = a[i];
            for (; j < a.length; j++) {
                if (a[j] < temp) {
                    temp = a[j];
                    position = j;
                }
            }
            a[position] = a[i];
            a[i] = temp;
        }
        for (int i = 0; i < a.length; i++)
            System.out.println(a[i]);
    }

    // 选择排序：堆排序
    public void heapSort(int[] a) {
        System.out.println("开始排序");
        int arrayLength = a.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            // 建堆

            buildMaxHeap(a, arrayLength - 1 - i);
            // 交换堆顶和最后一个元素
            swap(a, 0, arrayLength - 1 - i);

        }
    }

    private void swap(int[] data, int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    // 对data数组从0到lastIndex建大顶堆
    private void buildMaxHeap(int[] data, int lastIndex) {

        // 从lastIndex处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            // k保存正在判断的节点
            int k = i;
            // 如果当前k节点的子节点存在
            while (k * 2 + 1 <= lastIndex) {
                // k节点的左子节点的索引
                int biggerIndex = 2 * k + 1;
                // 如果biggerIndex小于lastIndex，即biggerIndex+1代表的k节点的右子节点存在
                if (biggerIndex < lastIndex) {
                    // 若果右子节点的值较大
                    if (data[biggerIndex] < data[biggerIndex + 1]) {
                        // biggerIndex总是记录较大子节点的索引
                        biggerIndex++;
                    }
                }
                // 如果k节点的值小于其较大的子节点的值
                if (data[k] < data[biggerIndex]) {
                    // 交换他们
                    swap(data, k, biggerIndex);
                    // 将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                    k = biggerIndex;
                } else {
                    break;
                }
            }
        }
    }

    // 交换排序：冒泡排序
    /**
     *  基本思想：在要排序的一组数中，对当前还未排好序的范围内的全部数，
     * 自上而下对相邻的两个数依次进行比较和调整，让较大的数往下沉，
     * 较小的往上冒。即：每当两相邻的数比较后发现它们的排序与排序要求相反时，就将它们互换。
     * @param a
     */
    public static void bubbleSort(int[] a) {
        int temp = 0;
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }

    }

    // 交换排序：快速排序
    private int getMiddle(int[] list, int low, int high) {
        int tmp = list[low]; // 数组的第一个作为中轴
        while (low < high) {
            while (low < high && list[high] >= tmp) {

                high--;
            }
            list[low] = list[high]; // 比中轴小的记录移到低端
            while (low < high && list[low] <= tmp) {
                low++;
            }
            list[high] = list[low]; // 比中轴大的记录移到高端
        }
        list[low] = tmp; // 中轴记录到尾
        return low; // 返回中轴的位置
    }

    private void _quickSort(int[] list, int low, int high) {
        if (low < high) {
            int middle = getMiddle(list, low, high); // 将list数组进行一分为二
            _quickSort(list, low, middle - 1); // 对低字表进行递归排序
            _quickSort(list, middle + 1, high); // 对高字表进行递归排序
        }
    }

    public void quick(int[] a2) {
        if (a2.length > 0) { // 查看数组是否为空
            _quickSort(a2, 0, a2.length - 1);
        }
    }

    //  快速排序
    /**
     * 基本思想：选择一个基准元素,通常选择第一个元素或者最后一个元素,通过一趟扫描，
     * 将待排序列分成两部分,一部分比基准元素小,一部分大于等于基准元素,
     * 此时基准元素在其排好序后的正确位置,然后再用同样的方法递归地排序划分的两部分。
     * @param data
     * @param start
     * @param length
     */
    public void sortQuick(int[] data, int start, int length) {
        int i = start, j = length, middle = data[i], tmp;
        while (true) {
            while ((++i) < length - 1 && data[i] < middle)
                ;
            while ((--j) > start && data[j] > middle)
                ;
            if (i >= j)
                break;
            tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
        data[start] = data[j];
        data[j] = middle;
        if (i < length)
            sortQuick(data, i, length);
        if (start < j)
            sortQuick(data, start, j);

    }

    // 归并排序
    public void mergingSort(int[] data, int left, int right) {
        if (left < right) {
            // 找出中间索引
            int center = (left + right) / 2;
            // 对左边数组进行递归
            mergingSort(data, left, center);
            // 对右边数组进行递归
            mergingSort(data, center + 1, right);
            // 合并
            merge(data, left, center, right);

        }
    }

    public void merge(int[] data, int left, int center, int right) {

        int[] tmpArr = new int[data.length];
        int mid = center + 1;
        // third记录中间数组的索引
        int third = left;
        int tmp = left;
        while (left <= center && mid <= right) {

            // 从两个数组中取出最小的放入中间数组
            if (data[left] <= data[mid]) {
                tmpArr[third++] = data[left++];
            } else {
                tmpArr[third++] = data[mid++];
            }
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) {
            tmpArr[third++] = data[mid++];
        }
        while (left <= center) {
            tmpArr[third++] = data[left++];
        }
        // 将中间数组中的内容复制回原数组
        while (tmp <= right) {
            data[tmp] = tmpArr[tmp++];
        }

    }

    // 基数排序
    public void radixSort(int[] array) {

        // 首先确定排序的趟数;
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }

        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> queue1 = new ArrayList<Integer>();
            queue.add(queue1);
        }

        // 进行time次分配和收集;
        for (int i = 0; i < time; i++) {

            // 分配数组元素;
            for (int j = 0; j < array.length; j++) {
                // 得到数字的第time+1位数;
                int x = array[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
                ArrayList<Integer> queue2 = queue.get(x);
                queue2.add(array[j]);
                queue.set(x, queue2);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Integer> queue3 = queue.get(k);
                    array[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }
        }

    }



    /**
     * 利用BitMap进行排序;
     * @param array
     */
    public static void bitMapSort(int[] array){

        //开辟16个Byte;
        byte[] bs=new byte[16];
        for(int i=0;i<array.length;i++){
            // num/8得到byte[]的index
            int arrayIndex = array[i] >> 3;

            // num%8得到在byte[index]的位置
            int position = array[i] & 0x07;

            //将1左移position后，那个位置自然就是1，然后和以前的数据做|，这样，那个位置就替换成1了。
            bs[arrayIndex] |= 1 << position;
        }
        int count=0;
        int num=0;
        //遍历Byte表;
        //考虑设置数组对应位的值;
        for(int i=0;i<bs.length;i++){
            byte b=bs[i];
            for(int j=0;j<8;j++){
                byte bi=(byte) (b&(0x01<<j));
                if(bi!=0){
                    num=i*8+j;
                    array[count]=num;
                    count++;
                }
            }
        }
        System.err.println(array.length);
        System.err.println(Arrays.toString(array));
    }



}
