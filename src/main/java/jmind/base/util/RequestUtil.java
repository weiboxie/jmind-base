package jmind.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * el表达式获取域名  ${pageContext.request.serverName}  ${header["Host"]}
 * @author xieweibo
 * @date 2015年12月10日
 */
public abstract class RequestUtil {

    public static String getParam(final ServletRequest request, String name, String defaultValue) {
        String v = request.getParameter(name);
        return DataUtil.isEmpty(v) ? defaultValue : v;
    }

    public static int getParamInt(final ServletRequest request, String name, int defaultValue) {
        String v = request.getParameter(name);
        if (DataUtil.isEmpty(v)) {
            return defaultValue;
        }
        return DataUtil.toInt(v);
    }

    public static long getParamLong(final ServletRequest request, String name, int defaultValue) {
        String v = request.getParameter(name);
        if (DataUtil.isEmpty(v)) {
            return defaultValue;
        }
        return DataUtil.toLong(v);
    }

    /**
     * 参数为空验证，只要有一个为空，即为空
     * @param request
     * @param names
     * @return
     */
    public static boolean isEmpty(final ServletRequest request, String... names) {
        for (String name : names) {
            if (DataUtil.isEmpty(request.getParameter(name))) {
                return true;
            }
        }
        return false;
    }

    public static TreeMap<String, String> getParameterMap(String param) {
        TreeMap<String, String> m = new TreeMap<String, String>();
        if (DataUtil.isEmpty(param))
            return m;
        int i=param.indexOf("?");
        if(i>0){
            param=param.substring(i+1);
        }
        String[] split = param.split("&");
        for (String s : split) {
            String[] ss = s.split("=");
            if (ss.length == 2) {
                m.put(ss[0], ss[1]);
            }
        }
        return m;
    }



    /**
     * 获取参数 ，按字典排序
     * @param request
     * @return
     */
    public static TreeMap<String, String> getParameterMap(final ServletRequest request) {
        TreeMap<String, String> m = new TreeMap<String, String>();
        // Arrays.toString(a)
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            m.put(key, request.getParameter(key));
        }
        return m;
    }

    public static  Map<String,String>  getHeaderMap(final HttpServletRequest request){
        TreeMap<String, String> m = new TreeMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name=headerNames.nextElement();
            m.put(name,request.getHeader(name));
        }
        return m;
    }


    public static String toQueryString(Map<String, ?> params) {
        if (DataUtil.isEmpty(params))
            return DataUtil.EMPTY;
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.substring(1);
    }

    public static Map<String, String> getReuestInfo(final HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uri", request.getRequestURI());
        String ip = request.getParameter("_ip");
        if (DataUtil.isEmpty(ip))
            ip = IpUtil.getIp(request);
        map.put("ip", ip);

        String ua = request.getHeader("User-Agent");
        if (ua != null)
            map.put("ua", ua);
        String r = request.getHeader("Referer");
        if (r != null)
            map.put("r", r);
        String uuid = CookieUtil.getUUIDCookie(request);
        if (uuid != null)
            map.put("uuid", uuid);
        String ajax = request.getHeader("X-Requested-With");
        if (ajax != null)
            map.put("ajax", "1");
        return map;
    }

    /**
     * 获取完整url
     * @param uri
     * @param params 参数会encode
     * @return
     */
    public static  String getURL(String uri,Map<String, ?> params){
        if (DataUtil.isEmpty(params))
            return uri;
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> entry : params.entrySet()) {
            try {
                 if(entry.getValue()!=null)
                sb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(),GlobalConstants.UTF8));
            } catch (UnsupportedEncodingException e) {
            }
        }
        if(uri.contains("?")){
            return uri+sb.toString();
        }
        return uri+"?"+sb.substring(1);
    }

    public static String getURL(final HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (query != null)
            url.append("?").append(query);
        return url.toString();

    }

    public static void setCsvHeader(String name, HttpServletRequest request, HttpServletResponse response) {
        setCsvHeader(name, request, response, GlobalConstants.UTF8);
    }

    /**
     * windows 设置gbk 直接以excel 格式打开不是乱码
     * @param name
     * @param request
     * @param response
     * @param encode
     */
    public static void setCsvHeader(String name, HttpServletRequest request, HttpServletResponse response, String encode) {
        try {
            request.setCharacterEncoding(encode);
            response.setCharacterEncoding(encode);
            response.setContentType("text/csv;charset=" + encode);
            if (request.getHeader("User-Agent").toUpperCase().indexOf("FIREFOX") > 0) {
                name = new String(name.getBytes(encode), "ISO8859-1");// firefox浏览器         
            } else {
                name = URLEncoder.encode(name, encode);
            }
            response.setHeader("content-disposition", "attachment;filename=" + name + ".csv");
        } catch (Exception e) {
        }
    }

    public static void setTextHead(String name, HttpServletRequest request, HttpServletResponse response, String encode) {
        try {
            request.setCharacterEncoding(encode);
            response.setCharacterEncoding(encode);
            response.setContentType("text;charset=" + encode);
            if (request.getHeader("User-Agent").toUpperCase().indexOf("FIREFOX") > 0) {
                name = new String(name.getBytes(encode), "ISO8859-1");
            } else {
                name = URLEncoder.encode(name, encode);
            }

            response.setHeader("Content-disposition", "attachment;filename=" + name + ".txt");
        } catch (Exception e) {
        }
    }

    public static void setExcelHeader(String name, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding(GlobalConstants.UTF8);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(GlobalConstants.UTF8);
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                name = URLEncoder.encode(name, GlobalConstants.UTF8);// IE浏览器
            } else {
                name = new String(name.getBytes(GlobalConstants.UTF8), "ISO8859-1"); // firefox浏览器 及其他问题
            }
            response.setHeader("content-disposition", "attachment;filename=" + name);
        } catch (Exception e) {
        }
    }

}
