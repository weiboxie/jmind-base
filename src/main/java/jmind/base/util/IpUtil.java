package jmind.base.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 *  SELECT INET_ATON('127.0.0.1'), INET_ATON('127.1'); ip to int
    SELECT INET_NTOA(3520061480); int to ip
 * @author weibo.xie
 *  2011-1-6
 */
public class IpUtil {

    private static final Pattern innerIpPatthen = Pattern
            .compile("(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})");//正则表达式=。 =、懒得做文字处理了、

    private static final Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

    /**
     * 取客户端的真实ip，考虑了反向代理等因素的干扰
     * 
     *     location / {
            proxy_pass  http://api.boss.letv.com;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header Http_X_Forwarded_For $http_x_forwarded_for;
        }
     */
    public static String getIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        String ip = resolveClientIPFromXFF(xff);
        if (isValidIP(ip)) {
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIP(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 是否内网ip
     * @param ip
     * @return
     */
    public static boolean isInnerIp(String ip) {
        Matcher matcher = innerIpPatthen.matcher(ip);
        return matcher.find();
    }

    /**
     * 从X-Forwarded-For头部中获取客户端的真实IP。
     * X-Forwarded-For并不是RFC定义的标准HTTP请求Header，可以参考http://en.wikipedia.org/wiki/X-Forwarded-For
     * 
     * @param xff
     *            X-Forwarded-For头部的值
     * @return 如果能够解析到client IP，则返回表示该IP的字符串，否则返回null
     */
    private static String resolveClientIPFromXFF(String xff) {
        if (xff == null || xff.length() == 0) {
            return null;
        }
        String[] ss = xff.split(",");
        // 从后往前取，防止用户自己设置 header. 自己设置header 会在前面
        // curl -H "X-Forwarded-For:192.168.0.1" http://benefit.hz.letv.com/ip.jsp
        // wget -O aa --header="X-Forwarded-For:192.168.0.1" "http://2hei.net/test.jsp"
        for (int i = ss.length - 1; i >= 0; i--) {
            String ip = ss[i].trim();
            // 是否内网ip，内网ip直接过滤，防止内部有多层nginx代理
            if (isInnerIp(ip))
                continue;
            // 是否合法
            if (isValidIP(ip))
                return ip;
        }
        return null;
    }

    // long ip to string
    public static String iplongToIp(long ipaddress) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf((ipaddress >>> 24)));
        sb.append(".");
        sb.append(String.valueOf((ipaddress & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((ipaddress & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf((ipaddress & 0x000000FF)));
        return sb.toString();
    }

    //string ip to long
    public static long ipStrToLong(String ipaddress) {
        long[] ip = new long[4];
        int position1 = ipaddress.indexOf(".");
        int position2 = ipaddress.indexOf(".", position1 + 1);
        int position3 = ipaddress.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(ipaddress.substring(0, position1));
        ip[1] = Long.parseLong(ipaddress.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(ipaddress.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(ipaddress.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    /**
     * 检查是否是一个合格的ipv4 ip
     * 
     * @param ip
     * @return
     */
    public static boolean isValidIP(String ip) {
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            return false;
        return ipPattern.matcher(ip).matches();
    }

    /**
     * 取得一个合格的ipv4 ip,外网ip
     * 
     * @return
     */
    public static String getLocalIP() {
        for (String a : getAllNoLoopbackAddresses()) {
            if (isValidIP(a)) {
                return a;
            }
        }
        return "";
    }

    /**
     * 获取本地地址
     * 内网ip
     * @return 本机ipv4地址
     */
    public static final String getLocalAddress() {
        try {
            for (Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces(); ni.hasMoreElements();) {
                NetworkInterface eth = ni.nextElement();
                for (Enumeration<InetAddress> add = eth.getInetAddresses(); add.hasMoreElements();) {
                    InetAddress i = add.nextElement();
                    if (i instanceof Inet4Address) {
                        if (i.isSiteLocalAddress()) {
                            return i.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得本地所有的ipv4列表
     * 
     * @return
     */
    public static List<String> getLocalIPs() {
        List<String> localIps = new ArrayList<String>();
        for (String a : getAllNoLoopbackAddresses()) {
            if (isValidIP(a)) {
                localIps.add(a);
            }
        }
        return localIps;
    }

    private static Collection<InetAddress> getAllHostAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Collection<InetAddress> addresses = new ArrayList<InetAddress>();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addresses.add(inetAddress);
                }
            }

            return addresses;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Collection<String> getAllNoLoopbackAddresses() {
        Collection<String> noLoopbackAddresses = new ArrayList<String>();
        Collection<InetAddress> allInetAddresses = getAllHostAddress();

        for (InetAddress address : allInetAddresses) {
            if (!address.isLoopbackAddress()) {
                noLoopbackAddresses.add(address.getHostAddress());
            }
        }

        return noLoopbackAddresses;
    }

}
