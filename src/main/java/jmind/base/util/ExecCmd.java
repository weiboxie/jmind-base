package jmind.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

/**
 * description:
 *
 * @author weibo.xie
 * @date : create in 11:25 上午 2020/8/27
 */
public class ExecCmd {

    public static void main(String[] args) {
        // String[] cmd="find  /Users/weibo/workspace/mashangfangxin/mashangfangxin  -type f  -name \"*.js\" |xargs grep \"Service\"";
        String cmd = "grep -r \"Service\" /Users/weibo/workspace/mashangfangxin/mashangfangxin/* ";
        exec(cmd);
    }

    public static List<String> exec(String cmd) {
        String[] cmdA = {"/bin/sh", "-c", cmd};
        List<String> sb = new LinkedList<>();
        try (LineNumberReader br = new LineNumberReader(new InputStreamReader(
              Runtime.getRuntime().exec(cmdA).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb;
}

    public static void wgetUrl(String url) {
        String cmd = "wget --timeout=25 " + url + " -O  /root/a.txt";
        sh(cmd);
    }

    public static void sh(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}