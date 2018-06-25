package jmind.base.util;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DataUtil {

    public static final String EMPTY = "";

    /**
     * 只要有1个为空，即为空
     * 2014-2-18 
     * @param str
     * @return
     */
    public static boolean isEmpty(String... str) {
        for (String s : str) {
            if (isEmpty(s))
                return true;
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    public static boolean isNotEmpty( String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    public static boolean isEmpty(Map<?, ?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     * 
     * @param str
     *            要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank(String str) {
        int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String likeParam(String s) {
        if (isEmpty(s))
            return null;
        return "%" + s.trim() + "%";
    }

    public static String LeftLikeParam(String s) {
        if (isEmpty(s))
            return null;
        return "%"+s.trim() ;
    }

    public static String RightLikeParam(String s) {
        if (isEmpty(s))
            return null;
        return s.trim() + "%";
    }

    public static String emptyToNull(String s) {
        if (s == null || s.isEmpty())
            return null;
        return s;
    }

    public static <E> Collection<E> addNotHas(Collection<E> source, Collection<E> target) {
        for (E o : target) {
            if (!source.contains(o)) {
                source.add(o);
            }
        }
        return source;
    }

    public static <T> String join(Collection<T> list, final String separator) {
        if (isEmpty(list))
            return EMPTY;
        StringBuilder sb = new StringBuilder();
        for (T e : list) {
            sb.append(separator).append(e);
        }
        sb.delete(0, separator.length());
        return sb.toString();
    }

    public static String join(final String separator, Object... list) {
        StringBuilder sb = new StringBuilder(list[0].toString());
        for (int i = 1; i < list.length; i++) {
            sb.append(separator).append(list[i]);
        }
        return sb.toString();
    }

    public static boolean matcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static String getInt(String str) {
        return str.replaceAll("\\D", "");
    }

    public static int toInt(String str) {
        if (isEmpty(str))
            return 0;
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static long toLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double toDouble(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static String formatNumber(Number num, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(num);
    }

    public static String percent(Number num) {
        return formatNumber(num, "#.##%");
    }

    /**
     * <p>Checks whether the String a valid Java number.</p>
     *
     * <p>Valid numbers include hexadecimal marked with the <code>0x</code>
     * qualifier, scientific notation and numbers marked with a type
     * qualifier (e.g. 123L).</p>
     *
     * <p><code>Null</code> and empty String will return
     * <code>false</code>.</p>
     *
     * @param str  the <code>String</code> to check
     * @return <code>true</code> if the string is a correctly formatted number
     */
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent   
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }

    /**
     * 组合字符串
     * 2013-11-27 
     * @param prefix

     * @return
     */
    public static String composeKey(String prefix, Object... o) {
        if (o.length > 0) {
            StringBuilder sb = new StringBuilder(prefix);
            for (Object obj : o) {
                sb.append(GlobalConstants.DASH).append(obj);
            }
            return sb.toString();
        }
        return prefix;

    }

    public static String getFullName(String name, String path) {
        return ":" + (isNotEmpty(path) ? name + "." + path : name);
    }

    public static String underscoreName(String name) {
        if (isEmpty(name)) {
            return EMPTY;
        }
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1).toLowerCase());
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = s.toLowerCase();
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString();
    }

    /**
     * 首字母大写
     * @param str
     * @return
     * <a href="http://my.oschina.net/u/556800" class="referer" target="_blank">@return</a> 
     */
    public static String firstLetterToUpperCase(String str) {
        Character c = Character.toUpperCase(str.charAt(0));
        return c.toString().concat(str.substring(1));
    }


    public static String firstLetterToLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
