package jmind.base.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jmind.base.security.Base64;

/**
 * 对象序列化
 * @author wbxie
 * 2013-9-9
 */
public class SerializeUtil {

    /**
     * 对象序列化
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            //   GZIPOutputStream outputStream = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 对象反序列化
     * @param bytes
     * @return
     */
    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            //   GZIPInputStream inputStream = new GZIPInputStream(bais);
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                ois.close();
                bais.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 序列化对象为String字符串
     * @param o Object
     * @return String
     * @throws Exception
     */
    public static String writeObject(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
        bos.close();

        return new String(Base64.encode(bos.toByteArray()));
    }

    /**
     * 反序列化字符串为对象
     * @param object     String
     * @return
     * @throws Exception
     */
    public static Object readObject(String object) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(object));
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object o = ois.readObject();
        bis.close();
        ois.close();
        return o;
    }

}
