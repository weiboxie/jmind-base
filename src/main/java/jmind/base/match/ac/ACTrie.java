package jmind.base.match.ac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ACTrie {

    HashMap<Integer, List<String>> outputs = new HashMap<Integer, List<String>>();
    HashMap<Integer, List<TrieNode>> states = new HashMap<Integer, List<TrieNode>>();
    HashMap<Integer, Integer> fails = new HashMap<Integer, Integer>();
    HashMap<Integer, List<TrieNode>> autoMatas = new HashMap<Integer, List<TrieNode>>();

    Set<Character> charsSet = new HashSet<Character>();

    boolean isBuild = false;

    public static final int FAIL = -1;

    public static final int START = 0;
    int newState = 0;

    public ACTrie(Collection<String> keywords) {
        for (String word : keywords) {
            enter(word);
        }

        isBuild = true;

        buildfail();

        buildδ();

        /*
         * 注释，打印自动机的表，可用来做研究。
         * 
         * for (int key : autoMatas.keySet()) { System.out.println("state " +
         * key); List<TrieNode> nodes = autoMatas.get(key); for (TrieNode node :
         * nodes) { System.out.println("        " + node.getNextChar() + "   "+
         * node.getNextState()); } System.out.println(); }
         */

        // System.out.println("fails " + fails);
        // System.out.println("outputs " + outputs);

    }

    void enter(String keyword) {
        int state = 0;
        int j = 0;
        char[] chars = keyword.toCharArray();
        while (j < chars.length && g(state, chars[j]) != FAIL) {
            state = g(state, chars[j]);
            j++;
        }

        for (int p = j; p < chars.length; p++) {
            newState++;
            g(state, chars[p], newState);
            charsSet.add(chars[p]);
            state = newState;
        }
        output(state, keyword);
    }

    /**
     * 状态转换
     * 
     * @param state
     * @param word
     */
    int g(int state, char word) {
        if (states.containsKey(state)) {
            for (TrieNode node : states.get(state)) {
                if (node.getNextChar() == word) {
                    return node.getNextState();
                }
            }
            if (state == 0 && isBuild) {
                return 0;
            }
        }
        return FAIL;
    }

    void g(int state, char word, int newState) {
        TrieNode node = new TrieNode();
        node.setNextChar(word);
        node.setNextState(newState);
        if (states.containsKey(state)) {
            states.get(state).add(node);
        } else {
            ArrayList<TrieNode> list = new ArrayList<TrieNode>();
            list.add(node);
            states.put(state, list);
        }
    }

    List<String> output(int state) {
        if (outputs.containsKey(state)) {
            return outputs.get(state);
        }
        return new ArrayList<String>();
    }

    void output(int state, String keyword) {
        if (outputs.containsKey(state)) {
            outputs.get(state).add(keyword);
        } else {
            ArrayList<String> list = new ArrayList<String>();
            list.add(keyword);
            outputs.put(state, list);
        }
    }

    void buildfail() {

        List<Integer> queue = new ArrayList<Integer>();

        for (char a : charsSet) {
            int s = g(0, a);
            if (s != 0) {
                queue.add(s);
                failure(s, 0);
            }
        }

        while (queue.size() != 0) {
            int r = queue.remove(0);

            // 1 2 3 4 5 6 7 8 9
            // 0 0 0 1 2 0 3 0 3

            for (char a : charsSet) {
                int s = g(r, a);
                if (s != FAIL) {
                    queue.add(s);
                    int state = failure(r);
                    while (g(state, a) == FAIL) {
                        state = failure(state);
                    }
                    failure(s, g(state, a));
                    // System.out.println(failure(s) + " " + s);
                    if (outputs.containsKey(failure(s))) {
                        if (outputs.containsKey(s)) {
                            outputs.get(s).addAll(outputs.get(failure(s)));
                        } else {
                            outputs.put(s, outputs.get(failure(s)));
                        }
                    }
                }
            }

        }
    }

    void failure(int state, int fail) {
        fails.put(state, fail);
    }

    int failure(int state) {
        if (!fails.containsKey(state)) {
            return 0;
        }
        return fails.get(state);
    }

    int δ(int state, char word) {
        if (states.containsKey(state)) {
            for (TrieNode node : states.get(state)) {
                if (node.getNextChar() == word) {
                    return node.getNextState();
                }
            }
        }
        return 0;
    }

    void δ(int state, char word, int newState) {
        if (newState == 0)
            return;
        TrieNode node = new TrieNode();
        node.setNextChar(word);
        node.setNextState(newState);
        if (autoMatas.containsKey(state)) {
            autoMatas.get(state).add(node);
        } else {
            ArrayList<TrieNode> list = new ArrayList<TrieNode>();
            list.add(node);
            autoMatas.put(state, list);
        }
    }

    private void buildδ() {
        List<Integer> queue = new ArrayList<Integer>();

        for (char a : charsSet) {
            δ(0, a, g(0, a));
            if (g(0, a) != 0)
                queue.add(g(0, a));
        }

        while (queue.size() > 0) {
            int r = queue.remove(0);
            for (char a : charsSet) {
                if (g(r, a) != FAIL) {
                    int s = g(r, a);
                    queue.add(s);
                    δ(r, a, s);
                } else {
                    δ(r, a, δ(failure(r), a));
                }
            }

        }
    }
}