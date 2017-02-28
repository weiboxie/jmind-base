package jmind.base.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Host {

    // export _webMaster=1 (master)
    // export _webMaster=0 (slave)
    public static boolean isMaster = "1".equals(System.getenv("_webMaster"));

    private static InetAddress _host;

    static {
        try {
            _host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMaster(String masterIp) {
        return masterIp.equalsIgnoreCase(getHostIp());
    }

    public static InetAddress getHost() {
        return _host;
    }

    public static String getHostName() {
        try {
            return _host.getHostName();
        } catch (Exception e) {
        }
        return null;
    }

    public static String getHostIp() {
        return _host.getHostAddress();
    }

}
