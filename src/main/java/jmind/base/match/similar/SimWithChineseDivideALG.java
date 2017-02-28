package jmind.base.match.similar;

import java.util.Map;
import java.util.Set;

/**
 * 基于中文分词的相似度判别。
 * @author wbxie
 * 2013-11-3
 */
public class SimWithChineseDivideALG {

    private WordsManagerForAnalyse wmfa = WordsManagerForAnalyse.getInstance();

    /**
     * 中文相似度检查
     * 2013-11-3 
     * @param a
     * @param b
     * @return
     */
    public double similar(String a, String b) {
        // 向量检查
        VectorSimResult vectorSimResult = getSimCount(a, b);
        return sim_jaccard(vectorSimResult);

    }

    public VectorSimResult getSimCount(String a, String b) {
        Map<String, Integer> aMap = wmfa.cutToMap(a);
        Map<String, Integer> bMap = wmfa.cutToMap(b);

        int aLength = aMap.size();
        int bLength = bMap.size();
        int sim = 0;
        Set<String> aStringSet = aMap.keySet();
        Set<String> bStringSet = bMap.keySet();

        for (String _b : bStringSet) {
            aStringSet.remove(_b);
        }

        sim = aLength - aStringSet.size();

        VectorSimResult result = new VectorSimResult();
        result.setaLength(aLength);
        result.setbLength(bLength);
        result.setSimCount(sim);
        return result;
    }

    public double sim_dice(VectorSimResult simCount) {
        double aLength = simCount.getaLength();
        double bLength = simCount.getbLength();
        double sim = simCount.getSimCount();
        double result = 2 * (sim / (aLength + bLength));
        return result;
    }

    public double sim_jaccard(VectorSimResult simCount) {
        double aLength = simCount.getaLength();
        double bLength = simCount.getbLength();
        double sim = simCount.getSimCount();
        double result = (sim / (aLength + bLength - sim));
        return result;
    }

    public double sim_cosine(VectorSimResult simCount) {
        double aLength = simCount.getaLength();
        double bLength = simCount.getbLength();
        double sim = simCount.getSimCount();

        double result = (sim / Math.sqrt(aLength + bLength));
        return result;
    }

    public double sim_overlap(VectorSimResult simCount) {
        double aLength = simCount.getaLength();
        double bLength = simCount.getbLength();
        double sim = simCount.getSimCount();

        double count = aLength > bLength ? aLength : bLength;
        double result = sim / count;
        return result;
    }

    public static void main(String[] args) {
        //  String a = "改挖地下室。以后跳楼就往上跳，让他们比比谁是跳蚤二号";
        //    String b = "以后绿豆蛙也升值了";

        //    String a = "那次跟同学一起晚上去山上玩，天气不太好，有点冷，又是午夜12点多了，大家在一块空地上点了一堆火。火烧了一下，没柴了，就分头去拾柴火，有两个刚上去一下，就尖叫着跑了下来，问他们什么事，两个人颤抖的说“有鬼”他们说上面有个女的穿个白衣服长头发的，一个人在上面。我们都不以为回事，唏嘘两句旧没事了，他们做死的说是真的，可就是没人信，都说有人又不巧，别人住上面的。　　后来由于人太多，就把火分成3堆，过了大约半小时，那两边的人都过来了，忽然正对着那两对没人看管的火的两个人大叫，鬼鬼鬼，　　大家回头一看，都尖叫起来，那两堆火四处移动，而且完全排除风吹的可能，那两堆火不是同一方向的移动。大家想想刚刚那两人说的话，越想越不对，这么晚了，怎么会有女的独身一人 在这里，再说了这山个面也没有人家啊，大家这才感觉事情不对跎，忙扑灭了火下山去。　　走到山脚是却又看到一个女的，这次可是全部看到了，白衣服，长发，就在我门前面，我们刚犹豫一下，就不见了那人影，走前点一看，只看到一块十分烂的墓碑斜倒在那里，碑都是黄色的了，墓已经被人踏平了，大家经过是都作了个揖，然后狂奔下了山。";
        //     String b = "这种事以前我都不信的，但是后来我的小侄女身上就发生了一件事。　　我老家是在一个很古老的小镇上，老街一带基本上都是明清时的建筑物，木质结构，而且那时的建筑物通常都有一个很大的大堂，也就是今天所谓的客厅。老房子本来就光线不足，加上年代久远，就变得阴暗潮湿。我三姨家就是这样的房子。小侄女刚出世，就特别爱哭，尤其是一经过大堂就哭的更厉害，怎么也哄不住，大家开始还都以为是婴儿的正常表现，也就没管。后来慢慢的大了点，就发现她经常是望着一个地方哭，哭两声就拼命要人抱走，大人还只当她不喜欢屋里的黑暗。家里第一次察觉不对时是在她两岁多能说话的时候。　　有一天，我姨父抱着她经过大堂，她又望着大梁的角落上，怯怯地说：“爷爷，你看，你看，她的眼睛好吓人啊~~~~~“可姨父转头望去，什么都没有。可她还是使劲的指着那个方向。从此以后她每次经过那里都会指着那个地方对人说：“你看啊！你看嘛！她的眼睛好吓人啊~~~~“　 　　后来我听三姨说，他们家以前就有个女孩子不止一次看到过一个穿红衣服的女子，现在小侄女看到的也可能是同一个。大概后来是请了人去家做了法事吧，再也没听说小侄女成天的哭了，也不怕到大堂里去了。我小时候也常看见有人影从旁边一闪而过，不过不知道是不是幻觉。";

        String b = "dsfdsfdsfsdf自动释放134-0345-4321第三方的是随碟附送的事发生的鼎折覆餗";
        String a = "134-0345-4321";

        SimWithChineseDivideALG simChinese = new SimWithChineseDivideALG();
        System.out.println(simChinese.similar(a, b));
    }

}
