package jmind.base.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;

/**
 * 参数签名验证
 * @author xieweibo
 *
 */
public class SignatureUtil {

    /**
     * 获取签名
     * @param method
     * @param url
     * @param params
     * @param secretKey
     * @return
     */
    public static String getSign(String method, String url, TreeMap<String, ?> params, String secretKey) {
        params.remove("sign");
        params.remove("signUrl");
        StringBuilder sb = new StringBuilder();
        if (!DataUtil.isEmpty(method)) {
            sb.append(method);
        }
        sb.append(url);
        for (Entry<String, ?> entry : params.entrySet()) {
            if (entry.getValue() != null && !DataUtil.isEmpty(entry.getValue().toString()))
                sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        String encode = null;
        try {
            encode = URLEncoder.encode(sb.toString(), GlobalConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            encode=sb.toString();
        }

        return MD5.md5(encode + secretKey);

    }

    /**
     * 以字母升序(A-Z)排列 ，&分隔，不encode
     * @param params
     * @param secretKey
     * @return
     */
    public static String getSign(TreeMap<String, ?> params, String secretKey) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            if (entry.getValue() != null && !DataUtil.isEmpty(entry.getValue().toString()))
                sb.append(GlobalConstants.DELIM).append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.deleteCharAt(0);
        return MD5.md5(sb.toString() + secretKey);

    }

    /**
     * 移动积分商城签名生成
     *  字符串排序 后加密
     * @param json
     * @param secretKey 
     * @return
     */
    public static String generateSign(String json, String secretKey) {
        char[] array = json.toCharArray();
        Arrays.sort(array);
        String sortParamJson = new String(array);
        return MD5.md5(secretKey + sortParamJson);
    }

}
