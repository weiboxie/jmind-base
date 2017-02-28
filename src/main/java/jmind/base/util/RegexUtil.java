package jmind.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    //  private final String regexpEmail = "^[a-zA-Z0-9]([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[\\.]{1}[a-zA-Z0-9.-_]+)?$";
    // /^[A-Z_a-z0-9-\.]+@([A-Z_a-z0-9-]+\.)+[a-z0-9A-Z]{2,4}$/
    private static final String regEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w]{2,4}$";
    private static final String regMobile = "^1(3|4|5|7|8)\\d{9}$";

    public static boolean isEmail(String email) {
        return matcher(regEmail, email);
    }

    public static boolean isMobile(String mobile) {
        return matcher(regMobile, mobile);
    }

    public static final boolean matcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            return true;
        }

        return false;
    }

}
