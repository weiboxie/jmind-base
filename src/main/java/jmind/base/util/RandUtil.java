package jmind.base.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import jmind.base.lang.Pair;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class RandUtil {
    private static final String[] word = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "Q", "W", "E", "R", "T",
            "Y", "U", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M"};

    public static final Random getRandom() {
        return _random;
    }

    private final static Random _random =  ThreadLocalRandom.current();

    public static int nextInt(int maxValue) {
        return _random.nextInt(maxValue);
    }

    public static int nextInt(int min, int max) {
        int i = _random.nextInt(max);
        if (i < min)
            return i + min;
        return i;
    }

    public static boolean randomBoolean() {
        return _random.nextBoolean();
    }

    /**
     * 从一个list 里面随机取一个
     *
     * @param list
     * @return
     */
    public static <T> T randGet(List<T> list) {
        if (list != null && list.size() > 0)
            return list.get(_random.nextInt(list.size()));
        else
            return null;
    }

    /**
     * 从一堆list 随机取一个
     *
     * @param lists
     * @return
     */
    public static <T> T randGet(List<T>... lists) {
        if (lists != null && lists.length > 0) {
            int size = 0;
            for (List<T> list : lists) {
                if (list != null)
                    size += list.size();
            }
            if (size == 0)
                return null;

            int id = _random.nextInt(size);
            for (List<T> list : lists) {
                if (list != null) {
                    if (id < list.size())
                        return list.get(id);
                    else
                        id -= list.size();
                }
            }
            return null; // impossible
        } else
            return null;
    }

    /**
     * list 分页
     *
     * @param list
     * @param pageNo 从0 开始
     * @param limit
     * @return
     */
    public static <T> List<T> subList(List<T> list, int pageNo, int limit) {
        int start = pageNo * limit;
        int end = list.size();
        if (start < end)
            return list.subList(start, Math.min(start + limit, end));
        else
            return Collections.emptyList();

    }

    /**
     * 从list中随机选出n个
     *
     * @param list   原列表
     * @param limit  随机取出数量
     * @param stable 结果列表中的数据是否保持在原list中顺序，true为保持原顺序
     * @return 保证非空
     */
    public static <T> List<T> randSubList(List<T> list, int limit, boolean stable) {
        if (DataUtil.isEmpty(list) || limit <= 0)
            return Collections.emptyList();

        final int size = list.size();
        limit = Math.min(size, limit);
        if (limit == size)
            return new ArrayList<T>(list);

        final boolean selectLessThanHalf = size >= limit * 2;
        final int selectSize = selectLessThanHalf ? limit : size - limit;
        final Set<Integer> selectedIds = new HashSet<Integer>(selectSize);
        final List<T> selected = new ArrayList<T>(limit);

        if (!selectLessThanHalf) {

            /*
             * 选出超过一半的T 1. 随机选择不返回的T的id，2.从原列表中列出未选择的T作为结果，3.如果无stable要求则乱序
             */
            while (selectedIds.size() < selectSize)
                selectedIds.add(_random.nextInt(size));

            for (int i = 0; i < size; i++) {
                if (!selectedIds.contains(i))
                    selected.add(list.get(i));
            }

            if (!stable)
                Collections.shuffle(selected);

        } else if (selectLessThanHalf && stable) {

            /*
             * 仅选出少量T(少于一半) && 有stable要求 1. 随机选择需要返回的T的id，2.排序，3.从原列表按id列出结果
             */
            while (selectedIds.size() < selectSize)
                selectedIds.add(_random.nextInt(size));

            List<Integer> selectedId = new ArrayList<Integer>(selectedIds);
            Collections.sort(selectedId);

            for (int id : selectedId)
                selected.add(list.get(id));

        } else {

            /*
             * 仅选出少量T(少于一半) && 无stable要求 一个一个随机选，选够了返回就行了
             */
            while (selected.size() < selectSize) {
                int id = _random.nextInt(size);
                if (!selectedIds.contains(id)) {
                    selectedIds.add(id);
                    selected.add(list.get(id));
                }
            }

        }
        return selected;
    }

    public static <T> List<T> randSubList(T[] list, int limit, boolean stable) {
        return randSubList(Arrays.asList(list), limit, stable);
    }

    /**
     * 随机合并列表，总是stable
     */
    public static <T> List<T> randJoin(int limit, List<T>... lists) {
        int choices = 0;
        if (lists == null || (choices = lists.length) == 0)
            return Collections.emptyList();

        final List<T> result = new ArrayList<T>(limit);
        // 随机选取.初始化
        int total = 0;
        final int[] sizes = new int[choices];
        for (int i = 0; i < choices; i++)
            total += (sizes[i] = (lists[i] == null ? 0 : lists[i].size()));

        int left = limit <= 0 ? total : Math.min(limit, total);
        while (left > 0) {
            int rand = nextInt(total);
            int choice = 0;
            while (rand >= sizes[choice]) {
                rand -= sizes[choice];
                choice++;
            }

            result.add(lists[choice].get(lists[choice].size() - sizes[choice]));
            sizes[choice]--;
            total--;
            left--;
        }
        return result;
    }

    /**
     *
     * @param length 随机数位数
     * @param abc 是否包含字母
     * @param max set 条数
     * @return
     */
    public static Set<String> randomCode(int length, boolean abc, int max) {
  //   return    Stream.generate(()->randomCode(length, abc)).distinct().limit(max).collect(toSet());
        Set<String> set = new HashSet<>();
        do {
            set.add(randomCode(length, abc));
        } while (set.size() < max);
        return set;
    }




    /**
     * @param length 随机字符长度
     * @param abc    是否可以有字母
     * @return
     */
    public static String randomCode(int length, boolean abc) {
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (abc && randomBoolean()) {
                int j = _random.nextInt(26);
                int k = randomBoolean() ? (j + 65) : (j + 97); // 大小写
                char c = (char) k;
                sRand.append(c);
            } else {
                sRand.append(nextInt(10));
            }

        }
        return sRand.toString();
    }

    /**
     * 不包含字母 I 和O  的随机数。都是大写格式
     *
     * @param length
     * @return
     */
    public static String randomCode(int length) {
        StringBuilder sRand = new StringBuilder();
        int word_len = word.length;
        for (int i = 0; i < length; i++) {
            sRand.append(word[nextInt(word_len)]);
        }
        return sRand.toString();
    }

    /**
     * 随机加减法验证码
     *
     * @return
     */
    public static final Pair<String, Integer> mathVCode() {
        int a = _random.nextInt(50) + 1;
        int b = _random.nextInt(30) + 1;
        String first = null;
        int second = a - b;
        if (second > 0 && _random.nextBoolean()) { // -
            first = a + "-" + b;

        } else { // +
            first = a + "+" + b;
            second = a + b;

        }
        return Pair.of(first + "=?", second);

    }

    public static final Color getRandColor(int fc, int bc) {//给定范围获得随机颜色
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + nextInt(bc - fc);
        int g = fc + nextInt(bc - fc);
        int b = fc + nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
