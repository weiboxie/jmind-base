package jmind.base.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {

    // 1. String --> InputStream
    public static InputStream String2InputStream(String str) {
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
        return stream;
    }

    //inputStream转换成String（此方法效率高些）
    public static String stream2String(InputStream in) {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    //inputStream转换成String
    public static String inputStream2Str(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * .InputStream --> String  此方法可以根据流的编码读取，解决编码问题
     * @param is
     * @return String
     * @throws IOException
     */
    public static String readerString(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is, getEncoding(is)));
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * 文件bytes 转成逗号分隔字符串。有中文注意乱码问题。可以使用 readerString
     * @param bytes
     * @param separator
     * @return
     */
    public static String byteSplit(byte[] bytes, String separator) {
        String s = new String(bytes);
        if (separator == null)
            return s;
        return s.replace("\r", "").replace("\n", separator).replace(" ", "");
    }

    public static byte[] getBytes(File f) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileInputStream in = new FileInputStream(f);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            return out.toByteArray();
        } catch (Exception e) {
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 根据流获取编码
     * @param in
     * @return
     * @throws IOException
     */
    public static String getEncoding(InputStream in) throws IOException {
        //   BufferedInputStream bin = new BufferedInputStream(is);
        int p = (in.read() << 8) + in.read();
        String code = "GBK";
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数  
        switch (p) {
        case 0xefbb:
            code = "UTF-8";
            break;
        case 0xfffe:
            code = "Unicode";
            break;
        case 0xfeff:
            code = "UTF-16BE";
            break;
        case 0x5c75:
            code = "ANSI|ASCII";
            break;
        default:
            code = "GBK";
        }
        //  一定要reset ，否则读取的时候位置就错了
        in.reset();
        return code;
    }

    /**
     * 根据流获取编码 和上面方法结果一样
     * @param in
     * @return
     * @throws IOException
     */
    public String getEncode(InputStream in) throws IOException {
        byte[] head = new byte[3];
        in.read(head);
        String code = "gb2312";
        if (head[0] == -1 && head[1] == -2)
            code = "UTF-16";
        if (head[0] == -2 && head[1] == -1)
            code = "Unicode";
        if (head[0] == -17 && head[1] == -69 && head[2] == -65)
            code = "UTF-8";
        in.reset();
        return code;
    }

    /**
     * 
     * @param path
     * @param text
     * @param append true :追加 false :重写
     */
    public static void writerText(String path, Object text, boolean append) {
        String texts = text.toString();
        try {
            FileWriter fw = new FileWriter(path, append);
            BufferedWriter out = new BufferedWriter(fw);
            out.write(texts);
            out.write("\n");
            out.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writerText(String path, Object text) {
        writerText(path, text, true);
    }

    public static String readerFile(String path) {
        StringBuilder sb = new StringBuilder();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp).append("\n");

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

    public static List<String> reader2List(InputStream in) {
        List<String> sb = new ArrayList<String>();
        Reader fr = null;
        BufferedReader br = null;
        try {
            fr = new InputStreamReader(in);
            br = new BufferedReader(fr);
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                sb.add(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return sb;
    }

    public static void execult(String url) {
        try {
            String cmd = "wget --timeout=25 " + url + " -O  /root/a.txt";
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sh(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
