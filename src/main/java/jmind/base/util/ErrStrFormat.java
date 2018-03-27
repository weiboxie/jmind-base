package jmind.base.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by weibo.xwb on 2017/9/21.
 */
public class ErrStrFormat {
    public static String getErrStr(Throwable e) {
        StringBuffer errStr = new StringBuffer();
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errStr.append(sw.toString().replace("\r\n",""));
            pw.close();
            sw.close();
        } catch (Exception e2) {
            errStr.append("bad getErrorInfoFromException");
        }
        return errStr.toString();
    }

    public static void main(String[] args){
        int i =9;
        try{
            int k = i/0;
        }catch(Exception e){
            System.out.print("::"+getErrStr(e));
        }

    }

}
