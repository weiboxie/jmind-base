package jmind.base.lang;

import jmind.base.util.RandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * description: Bitmap有什么用
 * 大量数据的快速排序、查找、去重
 * 主要应用于大规模数据下不需要精确过滤的场景，如检查垃圾邮件地址，爬虫URL地址去重，解决缓存穿透问题等
 *  https://www.cnblogs.com/cjsblog/p/11613708.html
 *  https://www.cnblogs.com/myseries/p/10880641.html
 *  com.google.common.hash.BloomFilter
 *  BloomFilter 只能判断肯定不存在 或者可能存在
 * @author weibo.xie
 * @date : create in 3:41 下午 2021/3/8
 */
public class BitMap {
    //保存数据的
    private byte[] bits;

    //能够存储多少数据,可存储的最大int
    private int capacity;


    public BitMap(int capacity){
        this.capacity = capacity;

        //1bit能存储8个数据，那么capacity数据需要多少个bit呢，capacity/8+1,右移3位相当于除以8
        bits = new byte[(capacity >>3 )+1];
    }

    public void add(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3;

        // num%8得到在byte[index]的位置
        int position = num & 0x07;

        //将1左移position后，那个位置自然就是1，然后和以前的数据做|，这样，那个位置就替换成1了。
        bits[arrayIndex] |= 1 << position;
    }

    public boolean contain(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3;

        // num%8得到在byte[index]的位置
        int position = num & 0x07;

        //将1左移position后，那个位置自然就是1，然后和以前的数据做&，判断是否为0即可
        return (bits[arrayIndex] & (1 << position)) !=0;
    }

    public void clear(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3;

        // num%8得到在byte[index]的位置
        int position = num & 0x07;

        //将1左移position后，那个位置自然就是1，然后对取反，再与当前值做&，即可清除当前的位置了.
        bits[arrayIndex] &= ~(1 << position);

    }

    /**
     * 输出数组中的数据
     * @return
     */

    public List<Integer> sort(){
        List<Integer> list=new ArrayList<>();
        //遍历Byte表;
        //考虑设置数组对应位的值;
        for(int i=0;i<bits.length;i++){
            byte b=bits[i];
            for(int j=0;j<8;j++){
                byte bi=(byte) (b&(0x01<<j));
                if(bi!=0){
                    list.add(i*8+j);
                }
            }
        }
        return  list;
    }

    public static void main(String[] args) {
        int a[] = { 49, 38, 65, 97, 76, 13, 27, 49, 78, 34, 12, 64, 5, 4, 62, 99, 98, 54, 56, 17, 18, 23, 34, 15, 35,
                25, 53, 51 };
        BitMap bitmap = new BitMap(100);
        for(int i=0;i<a.length;i++){

            bitmap.add(a[i]);
        }
        System.err.println(" ");
        System.err.println(bitmap.bits.length);
        List<Integer> output = bitmap.sort();
        System.err.println(output);
    }
}