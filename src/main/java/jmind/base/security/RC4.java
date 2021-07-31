package jmind.base.security;

/**
 * Created by weibo.xwb on 2018/3/2.
 */
public class RC4 {
    /**
     * 加密
     * @param data
     * @param key
     * @return
     */
    public static String encryRC4(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return HexString.toHex(asString(encryRC4Byte(data, key)));
    }

    /**
     * 解密
     * @param data
     * @param key
     * @return
     */
    public static String decryRC4(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return new String(RC4Base(HexString.hexToByte(data), key));
    }





    private static byte[] encryRC4Byte(String data, String key) {
        byte b_data[] = data.getBytes();
        return RC4Base(b_data, key);
    }



    private static String asString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length);
        for (int i = 0; i < buf.length; i++) {
            strbuf.append((char) buf[i]);
        }
        return strbuf.toString();
    }

    private static byte[] initKey(String aKey) {
        byte[] b_key = aKey.getBytes();
        byte state[] = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    private static byte[] RC4Base(byte[] input, String mKkey) {
        int x = 0;
        int y = 0;
        byte key[] = initKey(mKkey);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

    public static void main(String[] args) {
        String s="中文";
        String key="Www#22*(!" ;
        String a=  encryRC4(s,key);
        System.err.println(a);
        System.err.println(decryRC4(a,key));
    }


}
