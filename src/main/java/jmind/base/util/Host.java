package jmind.base.util;

import java.net.*;
import java.util.Enumeration;

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
//        if (_host.isLoopbackAddress()) {
//            // find the first IPv4 Address that not loopback
//            Enumeration<NetworkInterface> interfaces = null;
//            try {
//                interfaces = NetworkInterface.getNetworkInterfaces();
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }
//            while (interfaces.hasMoreElements()) {
//                NetworkInterface in = interfaces.nextElement();
//                Enumeration<InetAddress> addrs = in.getInetAddresses();
//                while (addrs.hasMoreElements()) {
//                    InetAddress address = addrs.nextElement();
//                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
//                        ip = address.getHostAddress();
//                    }
//                }
//            }
//        }
    }

}
