package jmind.base.security;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;

/**
 * code String
 * @author wbxie
 * 2013-6-18
 */
public class Verify {
    private static final String DELIM = "l";
    public static final String KEY = "(@ewfjdg.92xAJ!$0^)";

    /**
     * 字符串转化成对应的codePoint 
     * 2013-8-9 
     * @param source
     * @return int
     */
    public static String charCode(String source) {
        if (DataUtil.isEmpty(source))
            return DataUtil.EMPTY;
        StringBuilder sb = new StringBuilder();
        int len = source.length();
        for (int i = 0; i < len - 1; i++) {
            sb.append(source.codePointAt(i)).append(DELIM);
        }
        sb.append(source.codePointAt(len - 1));
        return sb.toString();
    }

    /**
     * char code 转化成字符串
     * 2013-8-9 
     * @param code
     * @return
     */
    public static String fromCharCode(String code) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer str = new StringTokenizer(code, DELIM);
        while (str.hasMoreTokens()) {
            int a = DataUtil.toInt(str.nextToken());
            sb.append(Character.toChars(a));
        }
        return sb.toString();
    }

    /**
     * 字符串加密
     * 2013-8-9 
     * @param source
     * @return
     */
    public static String encodeRC(String source) {
        String code = source;
        try {
            final byte[] result = new RC4Crypt(code, KEY).result;
            code = new String(Base64.encode(result), GlobalConstants.UTF8);
            code = URLEncoder.encode(code, GlobalConstants.UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 字符串解密
     * 2013-8-9 
     * @param code
     * @return
     */
    public static String decodeRC(String code) {
        String source = "";
        try {
            source = URLDecoder.decode(code, GlobalConstants.UTF8);
            source = new RC4Crypt(Base64.decode(source), KEY).recoverToString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    public static void main(String[] args) {
        String s = "xieweibo";
        s = Verify.encodeRC(s);
        System.out.println(s);
    }

}
