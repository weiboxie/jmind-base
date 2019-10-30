package jmind.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    //  private final String regexpEmail = "^[a-zA-Z0-9]([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[\\.]{1}[a-zA-Z0-9.-_]+)?$";
    // /^[A-Z_a-z0-9-\.]+@([A-Z_a-z0-9-]+\.)+[a-z0-9A-Z]{2,4}$/
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w]{2,4}$");
    public static final Pattern MOBILE_PATTERN = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");
    public static final Pattern LETTER_PATTERN=Pattern.compile("^[a-zA-Z]+" );
    public static final Pattern NUMBER_PATTERN =Pattern.compile("^\\d+$") ;
    public static final Pattern ALPHANUM_PATTERN  = Pattern.compile("^[0-9,a-z,A-Z]+$");
    public static  final Pattern CHINESE_PATTERN=Pattern.compile(GlobalConstants.CHINESE_REGEX);

    // 数字字母下划线
    public static final String regW="^\\w*$" ;
    public static boolean isEmail(String email) {
        return matcher(EMAIL_PATTERN, email);
    }

    public static boolean isMobile(String mobile) {
        return matcher(MOBILE_PATTERN, mobile);
    }

    public static final boolean matcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        return  matcher(pattern,value);
    }

    public static final boolean matcher(Pattern pattern, String value) {
        if(DataUtil.isEmpty(value)){
            return false;
        }

        Matcher matcher = pattern.matcher(value);
        return  matcher.matches();
    }

    /**
     * 是否全部字母
     * @param charaString
     * @return
     */
    public static boolean isEnglish(String charaString){
        return matcher(LETTER_PATTERN,charaString);
    }

    /**
     * 是否全部中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str){
        return matcher(CHINESE_PATTERN,str);

    }


    public static boolean isNumber(String str){
        return matcher(NUMBER_PATTERN,str);

    }

    /**
     * 是否字母数字
     * @param str
     * @return
     */
    public static boolean isAlphanum(String str){
        return matcher(ALPHANUM_PATTERN,str);
    }




}
