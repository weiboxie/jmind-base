package jmind.base.lang;

import java.io.Serializable;

public class Pager implements Serializable {

    // 当前页
    int page = 1;

    // 每页显示记录数
    int rows = 60;

    // 排序字段
    private String sort;

    // asc/desc
    private String order;

    /**
     * 获取当前页的起始位置
     *
     */
    public int getStart() {
        return (page - 1) * rows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}
