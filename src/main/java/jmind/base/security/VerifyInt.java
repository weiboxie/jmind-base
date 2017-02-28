package jmind.base.security;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;

import jmind.base.util.DateUtil;
import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;

/**
 * 
 * 
 * @author xieweibo 2010-5-5 下午02:29:49
 */
public class VerifyInt {

    private static char[] cKey = { 'X', 'W', 'B' };

    private static final char[] ss = { 'o', 'l', 'z', '5', 'f', 'q' };
    private static final char[] ii = { '0', '1', '2', 'g', '7', '9' };

    public static final String toAlphabet(int id) {
        String s = String.valueOf(id);
        for (int i = 0; i < ss.length; i++) {
            s = s.replace(ii[i], ss[i]);
        }
        return s;
    }

    public static final int fromAlphabet(String s) {
        for (int i = 0; i < ss.length; i++) {
            s = s.replace(ss[i], ii[i]);
        }
        return DataUtil.toInt(s);
    }

    /**
     * 根据uid 加密
     * 1天有效
     * @param code
     * @return
     */
    public static String encodeWithDay(int id) {
        String code = String.valueOf(id + 119);
        code = code.replaceAll("2", "M").replaceAll("0", "O").replaceAll("9", "P");
        int t = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int k = t % 2 + 2;
        code = code.substring(0, k) + "l" + t + "l" + code.substring(k);
        code = code.replaceAll("1", "Z").replaceAll("6", "B");
        // System.out.println("c=" + code);
        try {
            byte a[] = code.getBytes(GlobalConstants.UTF8);
            for (int i = 0; i < a.length; i++) {
                a[i] = (byte) (a[i] ^ cKey[i % cKey.length]);
            }
            code = new String(Base64.encode(a));
            code = URLEncoder.encode(code, GlobalConstants.UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 根据code 解密出uid
     * 1天有效
     * @param code
     * @return
     */
    public static int decoderWithDay(String code) {
        if (DataUtil.isEmpty(code))
            return 0;
        try {
            code = URLDecoder.decode(code, GlobalConstants.UTF8);
            byte a[] = Base64.decode(code);
            for (int i = 0; i < a.length; i++) {
                a[i] = (byte) (a[i] ^ cKey[i % cKey.length]);
            }
            code = new String(a);
            code = code.replaceAll("Z", "1").replaceAll("B", "6");
            String[] decoder = code.split("l");
            if (decoder.length == 3) {
                String id = (decoder[0] + decoder[2]).replaceAll("M", "2").replaceAll("O", "0").replaceAll("P", "9");
                int t = Integer.parseInt(decoder[1]);
                int now = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                if (t == now || (t + 1) == now) {
                    int uid = Integer.parseInt(id) - 119;
                    return uid;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVerifyCode(int id, long time) {
        return MD5.md5_16(id + Verify.KEY + time);
    }

    public static boolean checkVerifyCode(int id, long time, String code) {
        long now = System.currentTimeMillis();
        long m = now - time;
        if (m > 0 && m < DateUtil.DAY) {
            return getVerifyCode(id, time).equals(code);
        }
        return false;
    }

}