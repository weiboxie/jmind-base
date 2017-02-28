package jmind.base.sort;

import java.util.Arrays;

public class ChoiceSort {
    public void swap(int[] x, int a, int b) {
        int tmp;
        tmp = x[a];
        x[a] = x[b];
        x[b] = tmp;

    }

    public void sort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            int rowIndex = i;
            for (int j = data.length - 1; j > i; j--) {
                if (data[j] < data[rowIndex]) {
                    rowIndex = j;
                }
            }
            System.out.println(data[i]);
            swap(data, i, rowIndex);
        }
    }

    /**
     * 使用位图法进行排序
     *
     * @param arr
     */
    public static void bitmapSort(int[] arr) {

        // 找出数组中最值
        int max = arr[0];
        int min = max;
        for (int i : arr) {
            if (max < i) {
                max = i;
            }
            if (min > i) {
                min = i;
            }
        }
        // 得到位图数组
        int[] newArr = new int[max - min + 1];
        // 重新调整arr中的元素
        int index = 0;
        for (int i = 0; i < newArr.length; i++) {
            while (newArr[i] > 0) {
                arr[index] = i + min;
                index++;
                newArr[i]--;
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = { 2, 10, 5, 102, 8, 9, 1, 33 };
        int as = 536870912;
        System.out.println(1024 * 1024);
        System.out.println(Arrays.asList(arr));
        bitmapSort(arr);
        System.out.println(arr[0]);

    }
}