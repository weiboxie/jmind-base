package jmind.base.sort;

/**
 * 描述:
 https://www.cnblogs.com/huangwei520/p/4868934.html * @author weibo
 * @date : 2018/10/19 上午11:01
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * SortList<Student> sortList = new SortList<Student>();
 *   sortList.Sort(students, "getAge", "asc");
 */
public class SortList<E> {
    public  void Sort(List<E> list, final String method, final String sort) {
        Collections.sort(list, new Comparator() {
            public int compare(Object a, Object b) {
                int ret = 0;
                try {
                    Method m = ((E) a).getClass().getMethod(method, null);
                    if ("desc".equals(sort)) {// 倒序
                        ret = m.invoke(((E) b), null).toString()
                                .compareTo(m.invoke(((E) a), null).toString());
                    }else {
                        // 正序
                        ret = m.invoke(((E) a), null).toString()
                                .compareTo(m.invoke(((E) b), null).toString());
                    }
                } catch (NoSuchMethodException ne) {
                    ne.printStackTrace();
                } catch (IllegalAccessException ie) {
                    ie.printStackTrace();
                } catch (InvocationTargetException it) {
                    it.printStackTrace();
                }
                return ret;
            }
        });
    }
}
