package jmind.base.security;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

public class Native2Ascii {
    static String java_bin_path = "C:/Program Files/Java/jdk1.6.0_04/bin";

    public Native2Ascii() {

    }

    public Properties getProperties(String filename) throws IOException {
        Properties p = new Properties();
        //ClassLoader cl = this.getClass().getClassLoader();

        //        InputStream input;
        //        input = cl.getResourceAsStream(filename);
        //         p.load(input);
        FileInputStream input;
        input = new FileInputStream(filename);
        p.load(input);

        System.out.println(2 + "=" + p.get("2"));
        return p;

    }

    public String getUnicodeString(String value) {

        StringBuffer tempSb = new StringBuffer();
        try {
            Process pro = Runtime.getRuntime().exec(java_bin_path + "/native2ascii.exe ");
            OutputStream out = pro.getOutputStream();
            out.write(value.getBytes());
            out.flush();
            out.close();
            InputStreamReader child_in = new InputStreamReader(pro.getInputStream());

            int c;
            while ((c = child_in.read()) != -1) {
                tempSb.append((char) c);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return tempSb.toString();
    }

    public String unicodeTo(String value) {

        StringBuffer tempSb = new StringBuffer();
        try {
            Process pro = Runtime.getRuntime().exec(java_bin_path + "/native2ascii.exe -reverse");
            OutputStream out = pro.getOutputStream();
            out.write(value.getBytes());
            out.flush();
            out.close();
            InputStreamReader child_in = new InputStreamReader(pro.getInputStream());

            int c;
            while ((c = child_in.read()) != -1) {
                tempSb.append((char) c);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return tempSb.toString();
    }

    /** */
    /**
    * @param args
    */
    public static void main(String[] args) {
        String sourceFile = "Language_zh_CN.properties";
        String targetFile = "target.properties";

        Native2Ascii parser = new Native2Ascii();
        StringBuffer sb = new StringBuffer();
        try {
            //Convert the source file into unicode first.
            Properties p = parser.getProperties(sourceFile);
            Iterator<?> iterator = p.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                String value = p.get(key).toString();
                System.out.println(key + ":" + value);
                //  value= new String(value.getBytes("GBK"),"UTF-8");
                value = parser.getUnicodeString(value);
                //   value=parser.unicodeTo(value);
                System.out.println(key + ":" + value);
                p.setProperty(key.toString(), value);
                sb.append(key.toString() + "=" + value);
            }
            //write the target file.
            FileWriter out = new FileWriter(targetFile);
            out.write(sb.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
