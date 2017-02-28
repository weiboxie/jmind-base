package jmind.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jmind.base.security.MD5;
import jmind.base.security.Verify;

/**
 * 后台登陆逻辑
 * @author xieweibo
 *
 */
public class AdminCookieUtil {

    public static boolean setAdminCookie(final HttpServletRequest request, final HttpServletResponse response,
            String key, String name) {
        long time = System.currentTimeMillis();
        String value = DataUtil.join(GlobalConstants.DELIM, time, name,
                MD5.MD5_2(name + Verify.KEY + time).substring(4, 20));
        value = Verify.encodeRC(value);
        return CookieUtil.setSessionCookie(response, key, value, request.getServerName());
    }

    public static String getAdmin(final HttpServletRequest request, String key) {
        String cookie = CookieUtil.getCookie(request, key);
        if (!DataUtil.isEmpty(cookie)) {
            cookie = Verify.decodeRC(cookie);
            String[] code = cookie.split(GlobalConstants.DELIM);
            if (code.length == 3) {
                String time = code[0];
                String name = code[1];
                String md5 = MD5.MD5_2(name + Verify.KEY + time).substring(4, 20);
                long loginTime = DataUtil.toLong(time);
                // 判断时间,超过3天过期
                int days = DateUtil.getDayInt(System.currentTimeMillis()) - DateUtil.getDayInt(loginTime);
                if (days < 3 && md5.equals(code[2])) {
                    return name;
                }
            }
        }
        return null;
    }

}
