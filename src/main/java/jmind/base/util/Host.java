package jmind.base.util;

import java.net.*;
import java.util.Enumeration;

public abstract class Host {


    private static String ip;
    private static String hostName;
    private static InetAddress _host;

    static {
        try {
            // Init the host information.
            resolveHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void resolveHost() throws Exception {
        _host = InetAddress.getLocalHost();
        hostName = _host.getHostName();
        ip = _host.getHostAddress();
        if (_host.isLoopbackAddress()) {
            // find the first IPv4 Address that not loopback
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface in = interfaces.nextElement();
                Enumeration<InetAddress> addrs = in.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress address = addrs.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ip = address.getHostAddress();
                    }
                }
            }
        }
    }

    public static String getIp() {
        return ip;
    }

    public static String getHostName() {
        return hostName;
    }


    public static InetAddress getHost() {
        return _host;
    }


}
