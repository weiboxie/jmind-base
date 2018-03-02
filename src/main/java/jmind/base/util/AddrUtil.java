package jmind.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AddrUtil {

    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

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

    /**
     * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,linux 等.
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取unix网卡的mac地址. 非windows的系统默认调用本方法获取. 如果有特殊系统请继续扩充新的取mac地址方法.
     *
     * @return mac地址
     */
    private static String getUnixMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // linux下的命令，一般取eth0作为本地主网卡
            process = Runtime.getRuntime().exec("ifconfig eth0");
            // 显示信息中包含有mac地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {// 找到了
                    // 取出mac地址并去除2边空格
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("unix/linux方式未获取到网卡地址");
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    /**
     * 获取widnows网卡的mac地址.
     *
     * @return mac地址
     */
    private static String getWindowsMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // windows下的命令，显示信息中包含有mac地址信息
            process = Runtime.getRuntime().exec("ipconfig /all");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[physical
                index = line.toLowerCase().indexOf("physical address");
                if (index >= 0) {// 找到了
                    index = line.indexOf(":");// 寻找":"的位置
                    if (index >= 0) {
                        // 取出mac地址并去除2边空格
                        mac = line.substring(index + 1).trim();
                    }
                    break;
                }
            }
        }
        catch (IOException e) {
            System.out.println("widnows方式未获取到网卡地址");
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    /**
     * windows 7 专用 获取MAC地址
     *
     * @return
     * @throws Exception
     */
    private static String getWindows7MACAddress() {
        StringBuffer sb = new StringBuffer();
        try {
            // 获取本地IP对象
            InetAddress ia = InetAddress.getLocalHost();
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            for (int i = 0; i < mac.length; i++) {
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
        }
        catch (Exception ex) {
            System.out.println("windows 7方式未获取到网卡地址");
        }
        return sb.toString();
    }

    /**
     * 获取MAC地址
     *
     * @param argc
     *            运行参数.
     * @throws Exception
     */
    public static String getMACAddress() {
        // windows
        String mac = getWindowsMACAddress();
        // windows7
        if (DataUtil.isEmpty(mac)) {
            mac = getWindows7MACAddress();
        }
        // unix
        if (DataUtil.isEmpty(mac)) {
            mac = getUnixMACAddress();
        }

        if (!DataUtil.isEmpty(mac)) {
            mac = mac.replace("-", "");
        }
        else {
            mac = "ABCDEFGHIJ";
        }
        return mac;
    }



    public static final String getLocalMac()  {
        StringBuilder sb = new StringBuilder();
        try {
            //获取网卡，获取地址
            byte[] mac    = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            for(int i=0; i<mac.length; i++) {
//                if(i!=0) {
//                    sb.append("-");
//                }
                //字节转换为整数
                int temp = mac[i]&0xff;
                String str = Integer.toHexString(temp);
                if(str.length()==1) {
                    sb.append("0"+str);
                }else {
                    sb.append(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getMACAddress());
        System.out.println(getLocalMac());

    }



}
