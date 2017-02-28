package jmind.base.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class CollectionsUtil {

    public static List<String> asList(String str, String separator) {
        if (DataUtil.isEmpty(str))
            return Collections.emptyList();
        List<String> l = new ArrayList<String>();
        if (separator.length() > 1) {
            String[] split = str.split(separator);
            for (String s : split) {
                s = s.trim();
                if (!s.isEmpty())
                    l.add(s);
            }
        } else { // StringTokenizer  长度大于1 ，会对每个字符都匹配
            StringTokenizer token = new StringTokenizer(str, separator);
            while (token.hasMoreTokens()) {
                l.add(token.nextToken());
            }
        }
        return l;
    }

    public static Set<String> asSet(String str, String separator) {
        if (DataUtil.isEmpty(str))
            return Collections.emptySet();
        Set<String> l = new HashSet<String>();
        if (separator.length() > 1) {
            String[] split = str.split(separator);
            for (String s : split) {
                l.add(s.trim());
            }
            l.remove("");
        } else { // StringTokenizer  长度大于1 ，会对每个字符都匹配
            StringTokenizer token = new StringTokenizer(str, separator);
            while (token.hasMoreTokens()) {
                l.add(token.nextToken());
            }
        }
        return l;
    }

    /**
     * 只保留大于零的
     * @param str 
     * @param separator
     * @return
     */
    public static List<Integer> asIntListGtZero(String str, String separator) {
        if (DataUtil.isEmpty(str))
            return Collections.emptyList();
        List<Integer> l = new ArrayList<Integer>();
        if (str.length() > 1) {
            String[] split = str.split(separator);
            for (String s : split) {
                int val = DataUtil.toInt(s);
                if (val > 0)
                    l.add(val);
            }
        } else { // 这个性能要高，但StringTokenizer 只有单个字符匹配才是我们想要的
            StringTokenizer token = new StringTokenizer(str, separator);
            while (token.hasMoreTokens()) {
                int val = DataUtil.toInt(token.nextToken());
                if (val > 0)
                    l.add(val);
            }
        }

        return l;
    }

    public static Set<Long> asLongSet(String str, String separator) {
        if (DataUtil.isEmpty(str))
            return Collections.emptySet();
        String[] split = str.split(separator);
        Set<Long> l = new HashSet<Long>();
        for (String s : split) {
            l.add(DataUtil.toLong(s));
        }
        return l;
    }

    /**
     * 转化成字符串数组
     * @param list
     * @return
     */
    public static <T> String[] toStrArray(Collection<T> list) {
        String[] s = new String[list.size()];
        int i = 0;
        for (T l : list) {
            s[i++] = l.toString();
        }
        return s;
    }

    /**
     * list 分页
     * @param list
     * @param pageNo 从1开始,第几页
     * @param rows 每页条数
     * @return
     */
    public static <T> List<T> subList(List<T> list, int pageNo, int rows) {
        if (list == null)
            return Collections.emptyList();
        int start = (pageNo - 1) * rows;
        int end = list.size();
        if (start < end)
            return list.subList(start, Math.min(start + rows, end));
        else
            return Collections.emptyList();

    }

    /**
     * 把一个list分成 平均分成 pagecount 分，按份取
     * @param list
     * @param pageNo 当前第几页，从第一页开始
     * @param pageSize 每页条数
     * @param pageCount 总共页数,可以大于实际总页数,当大于实际页数时,会填充分
     * @return
     */
    public static <T> List<T> getPageList(List<T> list, int pageNo, int pageSize, int pageCount) {
        if (list == null) {
            return Collections.emptyList();
        }
        int size = list.size();
        if (size < pageSize * pageCount) {
            return subList(list, pageNo, pageSize);
        } else {
            int servings = size / pageCount;
            int start = (pageNo - 1) * servings + RandUtil.nextInt(servings - pageSize + 1);
            return list.subList(start, start + pageSize);
        }
    }

    /**
     * list 平均分成pageCount 分
     * @param list
     * @param pageNo
     * @param pageCount 总页数
     * @return
     */
    public static <T> List<T> getServings(List<T> list, int pageNo, int pageCount) {
        if (list == null) {
            return Collections.emptyList();
        }
        int servings = list.size() / pageCount;
        int end = pageNo * servings;
        if (end > list.size())
            return Collections.emptyList();
        return new ArrayList<T>(list.subList((pageNo - 1) * servings, pageNo * servings));
    }

}
