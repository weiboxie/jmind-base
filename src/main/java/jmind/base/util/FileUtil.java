package jmind.base.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.activation.URLDataSource;
import javax.imageio.ImageIO;

public class FileUtil {

    /**
     * 获取扩展名
     * @param filename
     * @return 123.jPg 返回jpg
     */
    public static String getExtensionName(final String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            final int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * 
     * @param file
     * @return  ".jpg"
     */
    public static String getFileType(String file) {
        try {
            return file.substring(file.lastIndexOf("."), file.length());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取文件
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] readFile(String filename) throws IOException {
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        if (filename.startsWith("http://")) {
            URL url = new URL(filename);
            URLDataSource uds = new URLDataSource(url);
            in = uds.getInputStream();
        } else {
            in = new FileInputStream(filename);
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        int len = bufferedInputStream.available();
        byte[] bytes = new byte[len];
        int r = bufferedInputStream.read(bytes);
        if (len != r) {
            bytes = null;
            throw new IOException("读取文件不正确");
        }
        bufferedInputStream.close();
        return bytes;
    }

    /**
     * 保存文件到硬盘上
     * 
     * @throws IOException
     * 
     */
    public static void save(InputStream in, String path, String fileName, boolean overwrite) throws IOException {

        FileUtil.makeDir(new File(path));

        File f = new File(path, fileName);

        if (f.exists() && !overwrite) {
            throw new IOException("the file " + path + " exists! But overwrite is FALSE!");
        }

        if (!f.exists()) {
            boolean createSuccess = f.createNewFile();
            if (!createSuccess) {
                throw new IOException("create new file " + path + " failed!");
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int reads = in.read(buffer);
                if (reads == -1) {
                    break;
                }
                out.write(buffer, 0, reads);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 保存图片文件到硬盘上
     * 
     * @throws IOException
     * 
     */
    public static void saveImage(InputStream in, String path, String fileName, String formatName, boolean overwrite)
            throws IOException {

        FileUtil.makeDir(new File(path));
        File f = new File(path, fileName);

        if (f.exists() && !overwrite) {
            throw new IOException("the file " + path + " exists! But overwrite is FALSE!");
        }

        BufferedImage bi = ImageIO.read(in);
        if (bi == null) {
            throw new IOException("从InputStream " + in + " 读取图像数据时失败！");
        }

        boolean success = ImageIO.write(bi, formatName, f);
        if (!success) {
            throw new IOException("把文件保存为 " + formatName + " 格式时失败！");
        }
    }

    /**
     * 复制文件
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @return 
     * @throws IOException
     */
    public static long copyFile(File sourceFile, File targetFile) throws IOException {
        if (sourceFile == null || targetFile == null) {
            return 0;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        long size = 0;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);

            size = fis.available();
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = fis.read(buff)) > 0) {
                fos.write(buff, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return size;
    }

    /**
     * 复制文件
     * @param sourceFile 源文件绝对路径
     * @param targetFile 目标文件绝对路径
     * @return
     * @throws IOException
     */
    public static long copyFile(String sourceFile, String targetFile) throws IOException {
        if (sourceFile == null || targetFile == null) {
            return 0;
        }
        return copyFile(new File(sourceFile), new File(targetFile));
    }

    /**
     * 把文件流写入一个目标文件
     * @param sourceOut 字节输出流
     * @param targetFile 绝对路径
     * @return
     */
    public static boolean toFile(ByteArrayOutputStream sourceOut, String targetFile) throws IOException,
            FileNotFoundException {
        if (sourceOut == null) {
            return false;
        }
        try {
            FileOutputStream fout = new FileOutputStream(new File(targetFile));
            fout.write(sourceOut.toByteArray());
            fout.close();
            return true;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    public static String readFile(File src, String charsetName) {
        if (DataUtil.isEmpty(charsetName)) {
            charsetName = GlobalConstants.UTF8;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(src), charsetName));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
        return sb.toString();
    }

    public static void writeToFile(File file, String sb, String charsetName) {
        if (DataUtil.isEmpty(charsetName)) {
            charsetName = GlobalConstants.UTF8;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charsetName));
            bw.write(sb);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception ie) {
                }
            }
        }
    }

    /**
     * 创建目录
     * @param dir 目录路径
     */
    public static void makeDir(File dir) {
        if (!dir.exists()) {
            makeDir(dir.getParentFile());
            dir.mkdir();
        }
    }

    /**
     * 删除文件
     * @param fileName 文件绝对路径
     */
    public static void removeFile(String fileName) {
        File file = new File(fileName);
        //cailing注释了。老删除不掉:file.deleteOnExit();
        if (file.exists()) {
            file.delete();
        }
    }

    public static void removeFile(File file) {
        //cailing注释了。老删除不掉:file.deleteOnExit();
        if (file.exists()) {
            file.delete();
        }
    }

    public static void ListFiles(File src) {
        File[] children = src.listFiles();
        String[] children1 = src.list();
        for (File c : children) {

            System.out.println(c);
        }
        System.out.println("----------");
        for (String s : children1) {
            System.out.println(s);
        }
    }

    // 列出所有文件和目录
    public static void visitAllDirsAndFiles(File dir) {
        System.out.println(dir);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String c : children) {
                visitAllDirsAndFiles(new File(dir, c));
            }
        }
    }

    // 只列出目录
    public static void visitAllDirs(File dir) {
        if (dir.isDirectory()) {
            System.out.println(dir);
            String[] children = dir.list();
            for (String c : children) {
                visitAllDirs(new File(dir, c));
            }
        }

    }

    // 只打印文件
    public static void visitAllFiles(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String c : children) {
                visitAllFiles(new File(dir, c));
            }
        } else {
            System.out.println(dir);
        }
    }

}
