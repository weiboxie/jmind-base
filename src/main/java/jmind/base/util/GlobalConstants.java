package jmind.base.util;

import java.nio.charset.Charset;

public class GlobalConstants {
    /** Number of CPUS, to place bound on table size */
    public static final int NCPU = Runtime.getRuntime().availableProcessors();
    // 精确到秒,memcache
    public static final int MINUTE = 60;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    public static final int WEEK = 7 * DAY;
    public static final int MONTH = 28 * DAY;

    public static final String UTF8 = "UTF-8";

    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);
    //连字符
    public static final String HYPHEN = "-";
    //分隔符
    public static final String DELIM = "&";

    // 下划线
    public static final String DASH = "_";
    // #
    public static final String POUND_SIGN = "#";
    // 逗号 comma
    public static final String COMMA = ",";
    // 冒号
    public static final String COLON = ":";

    public static final String CRLF = "\r\n";
    /***
     * 匹配中文字符的正则表达式： [\u4e00-\u9fa5]
    匹配双字节字符(包括汉字在内)：[^\x00-\xff] 
     */
    // 中文匹配
    public static final String CHINESE_REGEX = "[\\u4E00-\\u9FA5]+";

}
