package jmind.base.match.ac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 著名的AC算法。
 * 
 * 能够以线性时间进行搜索。
 * http://wenku.baidu.com/view/f9a89d0e4a7302768e993967.html?from=rec&pos=0&weight=5&lastweight=2&count=5
 * @author weibo.xie
 * 
 */
public class AhoCorasick {

    ACTrie actrie;

    public AhoCorasick(Collection<String> keywords) {
        actrie = new ACTrie(keywords);
    }

    /**
     * 搜索黑词
      wbxie
      2013-6-19 
      @param text
      @return
     */
    public List<SearchResult> search(String text) {
        // System.out.println("search begin");
        List<SearchResult> keyWords = new ArrayList<SearchResult>();
        char words[] = text.toCharArray();
        int wordsLength = words.length;
        int state = ACTrie.START;
        for (int i = 0; i < wordsLength; i++) {
            while (actrie.g(state, words[i]) == ACTrie.FAIL) {
                state = actrie.failure(state);
            }
            state = actrie.g(state, words[i]);
            List<String> results = actrie.output(state);
            if (results.size() != 0) {
                /* System.out.println("found 末尾在: " + state); */
                for (String result : results) {
                    SearchResult searchResult = new SearchResult();
                    int start = i - result.length() + 1;
                    searchResult.setEndPosition(i + 1);
                    searchResult.setStartPosition(start);
                    searchResult.setKeyword(result);
                    //					System.out.println("output : " + i + " " + start + " "
                    //							+ result);
                    keyWords.add(searchResult);
                }

            }
        }
        return keyWords;
    }

    public Set<String> searchKeyWord(String text) {
        Set<String> keyWords = new HashSet<String>();
        char words[] = text.toCharArray();
        int wordsLength = words.length;
        int state = ACTrie.START;
        for (int i = 0; i < wordsLength; i++) {
            while (actrie.g(state, words[i]) == ACTrie.FAIL) {
                state = actrie.failure(state);
            }
            state = actrie.g(state, words[i]);
            List<String> results = actrie.output(state);
            keyWords.addAll(results);
        }
        return keyWords;
    }

    public String replace(String text, String replacement) {
        Set<String> keys = searchKeyWord(text);
        if (keys.isEmpty())
            return text;
        StringBuilder sb = new StringBuilder("");
        for (String sr : keys) {
            sb.append("|" + sr);
        }
        sb.deleteCharAt(0);
        return text.replaceAll("(" + sb.toString() + ")", replacement);
    }

    /**
     * 得到警告信息
      wbxie
      2013-6-19 
      @param text
      @param keys
      @return
     */
    public String warn(String text, Set<SearchResult> keys) {
        if (keys.isEmpty())
            return text;
        StringBuilder sb = new StringBuilder("");
        for (SearchResult sr : keys) {
            sb.append("|" + sr.getKeyword());
        }
        sb.deleteCharAt(0);
        return text.replaceAll("(" + sb.toString() + ")", "<span class='heici'>$1</span>");
    }

    List<SearchResult> searchByAutomata(String text) {
        List<SearchResult> keyWords = new ArrayList<SearchResult>();
        char words[] = text.toCharArray();
        int wordsLength = words.length;
        int state = ACTrie.START;
        for (int i = 0; i < wordsLength; i++) {
            state = actrie.δ(state, words[i]);
            List<String> results = actrie.output(state);
            if (results.size() != 0) {

                for (String result : results) {
                    SearchResult searchResult = new SearchResult();
                    int start = i - result.length() + 1;
                    searchResult.setEndPosition(i + 1);
                    searchResult.setStartPosition(start);
                    searchResult.setKeyword(result);
                    keyWords.add(searchResult);
                }

            }
        }
        return keyWords;
    }

}
