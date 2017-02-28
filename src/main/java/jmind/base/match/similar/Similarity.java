package jmind.base.match.similar;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import jmind.base.cache.ConcurrentLRUHashMap;

public class Similarity {
    private Map<String, Queue<String>> contentMap = new ConcurrentLRUHashMap<String, Queue<String>>(3000);

    private final static Similarity instance = new Similarity();
    private final SimWithChineseDivideALG simChinese = new SimWithChineseDivideALG();

    public static Similarity getInstance() {
        return instance;
    }

    public double checkSimilar(String index, String content, boolean chinese) {
        double result = 0d;
        //        if (chinese) {
        //            content = CnToSpell.keepChinese(content);
        //        }
        // 如果有这样的索引
        if (contentMap.containsKey(index)) {

            Queue<String> queue = contentMap.get(index);
            for (String mapContentcontent : queue) {
                double newresult = chinese ? simChinese.similar(content, mapContentcontent) : SimChar.sim_dice(content,
                        mapContentcontent);

                if (newresult == 1) {// 如果结果为绝对相似，直接返回
                    return 1;
                } else {
                    if (newresult > result) {
                        result = newresult;
                    }
                }
            }

            if (queue.size() > 4) {
                queue.poll();
            }
            if (result < 0.85d) {
                queue.offer(content);
            }

        } else {
            // ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
            ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(5);
            queue.offer(content);
            contentMap.put(index, queue);

        }
        return result;
    }

    public static void main(String[] args) {
        String index = "www";
        String content = "sd是点发送到第三方第三方水电费";
        for (int i = 0; i < 10; i++) {
            Similarity.getInstance().checkSimilar(index, content, true);
        }

    }

}
