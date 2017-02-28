package jmind.base.sort;

import java.util.Random;

public class QuickSort {
	/**
	 * * @param data
	 *            需要排序的数组
	 * @param start
	 *            左边的位置,初始值为0
	 * @param length
	 *            右边的位置,初始值为数组长度
	 */
	public static void sort(int[] data, int start, int length) {
		int i, j;
		int middle, tmp;
		i = start;
		j = length;
		middle = data[i];
		while (true) {
			while ((++i) < length - 1 && data[i] < middle)
				;
			while ((--j) > start && data[j] > middle)
				;
			if (i >= j)
				break;
			tmp = data[j];
			data[j] = data[i];
			data[i] = tmp;
		}
		data[start] = data[j];
		data[j] = middle;
		if (start < j)
			sort(data, start, j);
		if (length > i)
			sort(data, i, length);
	}

	public static void main(String args[]) {
		int[] data = new int[10];
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			data[i] = r.nextInt(100);
			System.out.print(data[i] + " ");
		}
		QuickSort.sort(data, 0, data.length);
		System.out.println();
		for (int d : data) {
			System.out.print(d + " ");
		}
	}
}
