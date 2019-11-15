package jmind.base.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class NIOUtil {
    private static final int BSIZE = 1024;

    public static void copy(String file, String outFile) {
//        FileChannel in, out;
//        try {
//            in = new FileInputStream(file).getChannel();
//            out = new FileOutputStream(outFile).getChannel();
//            in.transferTo(0, in.size(), out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            Files.copy(Paths.get(file),new FileOutputStream(outFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param str
     * @param fileName
     * @param append 是否追加
     */
    @SuppressWarnings("resource")
    public static void writeToFile(String fileName, String str, boolean append) {
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(fileName, append).getChannel();
            ByteBuffer bb = ByteBuffer.allocate(BSIZE);
            bb.put(str.getBytes("UTF-8"));
            bb.flip();
            fc.write(bb);
        } catch (Exception e) {

        } finally {
            if (fc != null)
                try {
                    fc.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            fc = null;
        }

    }

    /**
     * https://www.iteye.com/blog/cucaracha-2041847
     * @param fileName 文件名
     * @param str
     * @param append  是否追加
     * @return
     */
    public static Path write(String fileName, String str, boolean append){
        try {
            if(append){
                return  Files.write(Paths.get(fileName),str.getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            }else{
                return  Files.write(Paths.get(fileName),str.getBytes(), StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null ;
        }
    }

    public static String readAll(String filename){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filename));
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }


    public static List<String> readAllLines(String filename){
        try {
           return Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("resource")
    public static String read(String filename) {
        String s = null;
        FileChannel fc = null;
        try {
            fc = new FileInputStream(filename).getChannel();
            ByteBuffer bb = ByteBuffer.allocate(BSIZE);
            fc.read(bb);
            bb.flip();
            byte[] b = new byte[bb.remaining()];//这里是关键，如果这个值定义过大，则会报错，小了不会报错
            bb.get(b);
            s = new String(b);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return s;
    }

    @SuppressWarnings("resource")
    public static ByteBuffer readFile(String filename) throws Exception {
        FileChannel fiChannel = new FileInputStream(filename).getChannel();
        MappedByteBuffer mBuf;
        mBuf = fiChannel.map(FileChannel.MapMode.READ_ONLY, 0, fiChannel.size());
        fiChannel.close();
        fiChannel = null;

        return mBuf;

    }

    @SuppressWarnings("resource")
    public static void saveFile(ByteBuffer src, String filename) throws Exception {
        FileChannel foChannel = new FileOutputStream(filename).getChannel();
        foChannel.write(src);
        foChannel.close();
        foChannel = null;
    }

    @SuppressWarnings("resource")
    public static void saveFile(FileChannel fiChannel, String filename) throws IOException {
        MappedByteBuffer mBuf;
        mBuf = fiChannel.map(FileChannel.MapMode.READ_ONLY, 0, fiChannel.size());

        FileChannel foChannel = new FileOutputStream(filename).getChannel();
        foChannel.write(mBuf);

        fiChannel.close();
        foChannel.close();

        fiChannel = null;
        foChannel = null;
    }

    public static void main(String[] args) throws Exception {
        writeToFile("a.txt","ssss",false);

    }
}
