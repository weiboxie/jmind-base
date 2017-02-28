package jmind.base.op;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import jmind.base.util.FileUtil;

/**
 * 批量ANSI编码文本文件转换成UTF-8
 * http://zhengzhuangjie.iteye.com/blog/1441699
 * http://zhatin.blog.sohu.com/35350647.html
 * http://zhidao.baidu.com/question/115825614.html
 * @author weibo-xie
 * 2012-11-29
 
  脚本执行：
 @echo off&color a
title TXT文本ANSI转UTF-8 -by 314ccp-
set/p.=请将目录拖放此处：
cls & cd /d  "%.%"
for /r %%a in (*.java)do (
"C:\Program Files\Java\jdk1.6.0_25\bin\native2ascii" -encoding UTF-8 -reverse %%a>unicode.tmp
copy unicode.tmp "%%a">nul
del unicode.tmp /q
)
echo 全部转换完成,可用WORD选Unicode验证了!
echo.
echo 按任意键打开目录！
pause>nul
start "" "%cd%"
 */
public class ConvertFileOp {
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        File orgi = new File("Users/wbxie/work/doc/wendan/MySql/mysql.txt");
        System.out.println(orgi.getAbsolutePath());
        System.out.println(orgi.getParent());
        System.out.println(orgi.getPath());
        System.out.println(orgi.getParentFile());
        System.out.println(orgi.getCanonicalPath());
        System.out.println(orgi);
        convert(orgi, "GBK", "UTF-8");
    }

    /**
     * 
     * @param file  文件目录
     * @param formCharsetName  原文件编码
     * @param toCharsetName   新编码
     */
    public static void convert(File file, String formCharsetName, String toCharsetName) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                convert(f, formCharsetName, toCharsetName);
            }
        } else if (file.isFile()) {

            String txt = FileUtil.readFile(file, formCharsetName);
            FileUtil.writeToFile(file, txt, toCharsetName);
        }

    }

    public static void convert(File src, File dest) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(src), "GBK"));
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            writer.write(line + "\n");
        }
        writer.flush();
        writer.close();
        br.close();
    }

    public static String convert(String text) throws UnsupportedEncodingException {
        return new String(text.getBytes("GBK"));
    }

}
