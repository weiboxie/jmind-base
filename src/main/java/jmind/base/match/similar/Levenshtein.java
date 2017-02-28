package jmind.base.match.similar;

import jmind.base.util.MathUtil;

/**
 * 
 * @className:MyLevenshtein.java
 * @classDescription:Levenshtein Distance 算法实现
 * 可以使用的地方：DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测
 * @author wbxie
 * @see http://rosettacode.org/wiki/Levenshtein_distance#JavaScript
 * 2014年7月29日
 */
public class Levenshtein {

    public static void main(String[] args) {
        //要比较的两个字符串
        String str1 = "he is wave";
        String str2 = "wave is he";
        sim_jaccard(str1, str2);
        int i = distance(str1, str2);
        System.out.println(i);

    }

    /**
     * 　　DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测
     * 
     * @createTime 2012-1-12
     */
    public static double sim_jaccard(String str1, String str2) {
        char[] b1 = str1.toCharArray();
        char[] b2 = str2.toCharArray();
        //计算两个字符串的长度。
        int len1 = b1.length;
        int len2 = b2.length;
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (b1[i - 1] == b2[j - 1]) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = MathUtil.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }
        }

        //取数组右下角的值，同样不同位置代表不同字符串的比较
        System.out.println("差异步骤：" + dif[len1][len2]);
        //计算相似度
        double similarity = 1 - (double) dif[len1][len2] / Math.max(str1.length(), str2.length());
        System.out.println("相似度：" + similarity);
        return similarity;
    }

    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw
                        : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

}
