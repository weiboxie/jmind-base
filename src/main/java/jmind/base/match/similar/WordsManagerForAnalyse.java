package jmind.base.match.similar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WordsManagerForAnalyse {
    private static WordsManagerForAnalyse instance = new WordsManagerForAnalyse();
    private WordNodeForAnalyse nodeTree;
    private int max;

    private WordsManagerForAnalyse() {
        this.max = 0;
        this.nodeTree = new WordNodeForAnalyse(null, null);
        try {
            //			File file = new File(
            //					"/data/reform/service/activityModule/activityIdentify/resource/sogou.dic");
            //			InputStreamReader read = new InputStreamReader(new FileInputStream(
            //					file), "UTF-8");
            InputStream in = getClass().getClassLoader().getResourceAsStream("sogou.dic");
            InputStreamReader read = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(read);
            String temp = null;
            temp = br.readLine();
            while (temp != null) {
                String[] str = temp.split("\t");
                insert(str[0]);
                temp = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("can not find th file");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("io error");
        }
    }

    public static WordsManagerForAnalyse getInstance() {
        return instance;
    }

    public final void insert(String word) {
        if (word == null)
            return;
        if (word.length() > this.max)
            this.max = word.length();
        this.nodeTree.insertWord(word, word);
    }

    public Set<String> cutToSet(String content) {
        if (content == null)
            return null;
        int pos = content.length();
        WordNodeForAnalyse node = null;
        Set<String> set = new HashSet<String>();
        char[] arrContent = content.toCharArray();
        while (pos > 0) {
            if (((content.charAt(pos - 1) >= 'a') && (content.charAt(pos - 1) <= 'z'))
                    || ((content.charAt(pos - 1) >= 'A') && (content.charAt(pos - 1) <= 'Z'))) {
                int posStart = pos - 1;
                while ((posStart >= 0)
                        && ((((content.charAt(posStart) >= 'a') && (content.charAt(posStart) <= 'z')) || ((content
                                .charAt(posStart) >= 'A') && (content.charAt(posStart) <= 'Z'))))) {
                    --posStart;
                }
                set.add(content.substring(++posStart, pos));
                pos = posStart;
            }

            WordNodeForAnalyse nodeTemp = this.nodeTree;

            for (int i = pos - 1; (i >= 0) && (i >= pos - this.max); --i) {
                char key = arrContent[i];
                nodeTemp = nodeTemp.get(key);
                if (nodeTemp == null) {
                    break;
                }
                if (nodeTemp.getWord() != null) {
                    node = nodeTemp;
                }
            }
            if (node != null) {
                set.add(node.getWord());
                pos -= node.getWord().length();
            } else {
                --pos;
            }
        }
        return set;
    }

    public int getMapTotalCount(HashMap<String, Integer> hashMap) {
        return hashMap.get("wordsTotal");
    }

    public HashMap<String, Integer> cutToMap(String content) {
        if (content == null)
            return null;
        int pos = content.length();
        WordNodeForAnalyse node = null;
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        char[] arrContent = content.toCharArray();
        while (pos > 0) {
            if (((arrContent[(pos - 1)] >= 'a') && (arrContent[(pos - 1)] <= 'z'))
                    || ((arrContent[(pos - 1)] >= 'A') && (arrContent[(pos - 1)] <= 'Z'))) {
                int posStart = pos - 1;
                while ((posStart >= 0)
                        && ((((arrContent[posStart] >= 'a') && (arrContent[posStart] <= 'z')) || ((arrContent[posStart] >= 'A') && (arrContent[posStart] <= 'Z'))))) {
                    --posStart;
                }
                Integer count = Integer.valueOf(0);
                String word = content.substring(++posStart, pos);
                if (hashMap.containsKey(word)) {
                    count = hashMap.get(word);
                }
                count = Integer.valueOf(count.intValue() + 1);
                hashMap.put(word, count);
                pos = posStart;
            }

            WordNodeForAnalyse nodeTemp = this.nodeTree;

            for (int i = pos - 1; (i >= 0) && (i >= pos - this.max); --i) {
                char key = arrContent[i];
                nodeTemp = nodeTemp.get(key);
                if (nodeTemp == null) {
                    break;
                }
                if (nodeTemp.getWord() != null) {
                    node = nodeTemp;
                }
            }
            if (node != null) {
                String word = node.getWord();
                Integer count = Integer.valueOf(0);
                if (hashMap.containsKey(word)) {
                    count = hashMap.get(word);
                }
                count = Integer.valueOf(count.intValue() + 1);
                hashMap.put(word, count);
                pos -= word.length();
                node = null;
            } else {
                --pos;
            }
        }
        return hashMap;
    }

    public final void remove(String word) {
        if (word == null)
            return;
        this.nodeTree.remove(word);
    }

    public final void removeAll() {
        this.nodeTree = new WordNodeForAnalyse(null, null);
    }

}
