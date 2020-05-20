package jmind.base.util;

import java.io.UnsupportedEncodingException;


/**
 * description: 随机生成中文
 *
 * @author weibo.xie
 * @date : create in 7:24 下午 2020/5/20
 */
public class ChineseUtil {


    public static   String getChinese() {
        String str = "";
        int hightPos; //
        int lowPos;
        hightPos = (176 + RandUtil.nextInt(39));
        lowPos = (161 +RandUtil.nextInt(93));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }


    //获得汉字名字
    public  static String getChinese(int chineseNameNum){
        StringBuilder name =new StringBuilder() ;
        for(int i=0; i<chineseNameNum;i++){
            name.append(getChinese());
        }
        return name.toString();
    }


}