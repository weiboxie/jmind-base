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
            errStr.append(DataUtil.removeLF(sw.toString()));
            pw.close();
            sw.close();
        } catch (Exception e2) {
            errStr.append("bad getErrorInfoFromException");
        }
        return errStr.toString();
    }

    /**
     * 获取当前堆栈信息
     * @return
     */
    public static String getCurrentStackTrace() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StringBuffer sbf = new StringBuffer();
        if (st != null) {
            for (StackTraceElement e : st) {
                if (sbf.length() > 0) {
                    sbf.append(" <- ");
                    sbf.append(System.getProperty("line.separator"));
                }
                sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                        , e.getClassName()
                        , e.getMethodName()
                        , e.getLineNumber()));
            }
        }
        return sbf.toString();
    }

    public static void main(String[] args) {
        int i = 9;
        try {
            int k = i / 0;
        } catch (Exception e) {
            System.out.print("::" + getErrStr(e));
        }

    }

}
