package jmind.base.lang;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Page<T> extends Pager {
    /**
     * 数据
     */
    private List<T> result = Collections.emptyList();
    /**
     * 总数据数
     */
    private int count;

    /**
     * 总页数,即 最后一页
     */
    private int pagecount;

    /**
     * 前一页
     */
    private int prev;
    /**
     * 后一页
     */
    private int next;

    private String uri;
    private Map<String, String> query;

    public Page() {

    }

    /**
     * 
     * <p>Title: </p> 
     * <p>Description: </p> 
     * @param count
     * @param curpage
     * @param pagesize
     * @param result
     */
    public Page(final int curpage) {
        this.page = curpage;
    }

    private void calculate() {
        //总页数

        this.pagecount = (count + rows - 1) / rows;

        this.prev = this.page <= 1 ? 0 : this.page - 1;
        this.next = this.page >= this.pagecount ? 0 : this.page + 1;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public void setCount(int count) {
        this.count = count;
        if (count > 0)
            calculate();
    }

    public void sePage(int curpage) {
        this.page = curpage;
        calculate();
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public void setNext(int next) {
        this.next = next;
    }

    /**
     * @return result
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * @return pagecount
     */
    public int getPagecount() {
        return pagecount;
    }

    /**
     * @return prev
     */
    public int getPrev() {
        return prev;
    }

    /**
     * @return next
     */
    public int getNext() {
        return next;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "page=" + page + ",pagesize=" + rows + ",count=" + count;
    }

}
