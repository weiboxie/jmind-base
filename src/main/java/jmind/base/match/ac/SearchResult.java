package jmind.base.match.ac;

import java.io.Serializable;

/**
 * 
 * @author weibo.xie
 * 2011-11-29
 */

public class SearchResult implements Serializable {

    private int startPosition;
    private int endPosition;
    private String keyword;

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return startPosition + "|" + endPosition + "|" + keyword + "   ";
    }
}
