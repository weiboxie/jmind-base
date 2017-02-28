package jmind.base.match;

/**
 * http://hxraid.iteye.com/blog/621660
  * KMP，基于左前缀的滑动的经典单模式搜索
 * 
 * 预处理时间复杂度 O(m)<br>
 * 搜索最坏时间复杂度 O(n)<br>
 * 搜索平均时间复杂度 O(n)<br>
 * 
 * n代表文本长度，m代表模式串长度<br>
 * 
 * 只有当模式串长度小于8时，KMP才比基于后缀和基于子串的搜索方法有效。<br>
 * 而在这个范围，即小于8时，Shift-And算法和Shift-Or能够在所有机器上运行，速度至少是KMP的两倍，且更易于实现。<br>
 * 因为Shift-And和Shift-Or算法是基于位并行的，所以该算法取决与机器的字长。<br>
 * 
 * 两倍是如何算出的，模式串最小不能小于2位，因为1位的模式没有可比性。于是最少会快2倍。<br>
 * 因为1971年，Intel公司推出了世界上第一台用于计算器的4位微处理器4004。所以至少能提高w倍，即4倍。
 * 
 * KMP采用的并不多。虽然时间复杂度很小，通常比蛮力搜索还要慢。
 * 
 * KMP构建了一个DFA自动机。利用next数组来预先计算状态转移。
 * 
 * next数组代表当有一个不同的时候(模式串的第j位)，向右滑动的距离j-next[j]
 * @author weibo.xie
 * 2011-11-29
 */
public class KMP {

    int patternLength;
    String _pattern;

    /**
     * next特征数组
     * 
     * 辅助数组next来确定当匹配过程中出现不等时,模式P右移的位置和开始比较的位置.
     * 
     * 对于任意的模式串p=p0p1…pm-1
     * 
     * 求p0…pi-1最大相同的前缀和后缀的长度k <br>
     * next[i] = k <br>
     * 
     * next特征数组结合pattern就成为了一个自动机<br>
     */
    int[] next;

    /**
     * 预处理,构建一个自动机<br>
     * 
     * @param pattern
     */
    public void complie(String pattern) {
        _pattern = pattern;
        patternLength = pattern.length();
        next = new int[patternLength];

        int i = 0, j = -1;
        next[0] = -1;

        while (i < patternLength - 1) {
            // 如果刚开始，或者发现一个字符匹配
            if (j == -1 || _pattern.charAt(i) == _pattern.charAt(j)) {// 找不到，直到匹配到字符
                i++;
                j++;
                //	System.out.println(i + " " + j);
                // 如果该字符匹配的前后不匹配 aab
                if (_pattern.charAt(i) != _pattern.charAt(j)) {
                    next[i] = j;
                } else {
                    next[i] = next[j];
                }
            } else {
                // System.out.println("> " + j + " " + next[j]);
                j = next[j];// 根据前一个的位置，j可能会被重置为-1
            }
        }

    }

    public static void main(String[] args) {
        KMP kmp = new KMP();
        kmp.complie("中文");
        int search = kmp.search("wacaa中文");
        System.out.println("s" + search);
        System.out.println("y" + kmp.search("wsde"));
    }

    /**
     * 
     * 搜索关键字
     * 
     * 匹配过程中遇到Pi不等于Tj时,若next[i]>=0,则应将P右移i-next[i]位个字符,用P中的第next[i]个字符与Tj 进行比较;<br>
     * 若:next[i]= -1,P中的任何字符都不必再与Tj比较,而应将P右移i+1个字符,从P0和Tj+1从新开始下一轮比较
     * 
     * @param content
     *            内容
     * @return -1 代表不存在 <br>
     *         0 代表找到
     */
    public int search(String content) {

        int N = content.length();
        int i = 0, j = 0;

        while (i < N && j < patternLength) {
            if (j == -1 || _pattern.charAt(j) == content.charAt(i)) {
                i++;
                j++;
            } else {
                // 如果发现有不同，那么就向右位移j个数
                j = next[j];
            }
        }
        if (j == patternLength)
            return i - j;
        else
            return -1;
    }

}
