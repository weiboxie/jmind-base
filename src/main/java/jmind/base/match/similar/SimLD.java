package jmind.base.match.similar;

/**
 *  * LD又称 编辑距离。
 * 
 * 该算法计算从a编辑到b的复杂度
 * @author wbxie
 * 2013-11-3
 */
public class SimLD {

    private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }

    public static double sim(String str1, String str2) {

        int n = str1.length();
        int m = str2.length();

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        // 建立一个矩阵
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; ++i) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; ++j) {
            d[0][j] = j;
        }
        char[] array1 = str1.toCharArray();
        char[] array2 = str2.toCharArray();
        for (int i = 1; i <= n; ++i) {
            char ch1 = array1[(i - 1)];

            for (int j = 1; j <= m; ++j) {
                char ch2 = array2[(j - 1)];
                int temp;
                if (ch1 == ch2)
                    temp = 0;
                else {
                    temp = 1;
                }

                d[i][j] = min(d[(i - 1)][j] + 1, d[i][(j - 1)] + 1, d[(i - 1)][(j - 1)] + temp);
            }
        }

        int ld = d[n][m];
        return 1.0D - ld / Math.max(str1.length(), str2.length());
    }

    public static void main(String[] args) {
        String b = "我们134-0345sdsfdsafdsafsdf撒旦撒撒旦撒旦按时21";
        String a = "我ya134-0345-4321";

        System.out.println(sim(b, b));

    }

}
