package jmind.base.util;

import java.io.UnsupportedEncodingException;

public class SafeEncoder {
    public static byte[][] encodeMany(final String... strs) {
        byte[][] many = new byte[strs.length][];
        for (int i = 0; i < strs.length; i++) {
            many[i] = encode(strs[i]);
        }
        return many;
    }

    public static byte[] encode(final String str) {
        try {
            return str.getBytes(GlobalConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str.getBytes();

    }

    public static String encode(final byte[] data) {

        try {
            return new String(data, GlobalConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String(data);

    }

}
