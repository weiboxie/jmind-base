package jmind.base.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AddrUtil {

    public static List<InetSocketAddress> getAddress(String s) {
        if (DataUtil.isEmpty(s))
            return Collections.emptyList();
        List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
        for (String str : s.split(",")) {
            String[] host = str.split(":");
            if (host.length == 2) {
                list.add(new InetSocketAddress(host[0], DataUtil.toInt(host[1])));
            }
        }
        return list;
    }

}
