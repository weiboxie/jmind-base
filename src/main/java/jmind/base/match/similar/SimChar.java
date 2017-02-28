package jmind.base.match.similar;

/**
 * 相似度算法。可检查中英文<br>
 * @author wbxie
 * 2013-11-3
 */
public class SimChar {

    /**
     * dice度量
     * 
     * @param a
     * @param b
     * @return
     */
    public static double sim_dice(String a, String b) {
        a = a.replace(" ", "");
        b = b.replace(" ", "");
        char[] aChar = a.toCharArray();
        char[] bChar = b.toCharArray();
        double aLength = a.length();
        double bLength = b.length();
        double sim = 0;
        for (int i = 0; i < aLength; i++) {
            char _a = aChar[i];
            xx: for (int j = 0; j < bLength; j++) {
                if (_a == bChar[j]) {
                    sim++;
                    break xx;
                }
            }
        }
        double result = 2 * sim / (aLength + bLength);
        return result;
    }

    /**
     * jaccard度量
     * 
     * @param a
     * @param b
     * @return
     */
    public static double sim_jaccard(String a, String b) {
        char[] aChar = a.toCharArray();
        char[] bChar = b.toCharArray();
        double aLength = a.length();
        double bLength = b.length();
        double sim = 0;
        for (int i = 0; i < aLength; i++) {
            char _a = aChar[i];
            BRound: for (int j = 0; j < bLength; j++) {
                if (_a == bChar[j]) {
                    sim++;
                    break BRound;
                }
            }
        }
        double result = sim / (aLength + bLength - sim);
        return result;
    }

    /**
     * 余弦度量
     * 
     * @param a
     * @param b
     * @return
     */
    public static double sim_cosine(String a, String b) {
        char[] aChar = a.toCharArray();
        char[] bChar = b.toCharArray();
        double aLength = a.length();
        double bLength = b.length();
        double sim = 0;
        for (int i = 0; i < aLength; i++) {
            char _a = aChar[i];
            BRound: for (int j = 0; j < bLength; j++) {
                if (_a == bChar[j]) {
                    sim++;
                    break BRound;
                }
            }
        }
        double result = sim / ((double) Math.sqrt(aLength + bLength));
        return result;
    }

    /**
     * 重叠度量
     * 
     * @param a
     * @param b
     * @return
     */
    public static double sim_overlap(String a, String b) {
        char[] aChar = a.toCharArray();
        char[] bChar = b.toCharArray();
        double aLength = a.length();
        double bLength = b.length();
        double sim = 0;
        for (int i = 0; i < aLength; i++) {
            char _a = aChar[i];
            BRound: for (int j = 0; j < bLength; j++) {
                if (_a == bChar[j]) {
                    sim++;
                    break BRound;
                }
            }
        }
        aLength = aLength > bLength ? aLength : bLength;
        double result = sim / aLength;
        return result;
    }

    public static void main(String[] args) {

        String a = "今天星期四";
        String b = "今天是星期五";

        //    String a = "那次跟同学一起晚上去山上玩，天气不太好，有点冷，又是午夜12点多了，大家在一块空地上点了一堆火。火烧了一下，没柴了，就分头去拾柴火，有两个刚上去一下，就尖叫着跑了下来，问他们什么事，两个人颤抖的说“有鬼”他们说上面有个女的穿个白衣服长头发的，一个人在上面。我们都不以为回事，唏嘘两句旧没事了，他们做死的说是真的，可就是没人信，都说有人又不巧，别人住上面的。　　后来由于人太多，就把火分成3堆，过了大约半小时，那两边的人都过来了，忽然正对着那两对没人看管的火的两个人大叫，鬼鬼鬼，　　大家回头一看，都尖叫起来，那两堆火四处移动，而且完全排除风吹的可能，那两堆火不是同一方向的移动。大家想想刚刚那两人说的话，越想越不对，这么晚了，怎么会有女的独身一人 在这里，再说了这山个面也没有人家啊，大家这才感觉事情不对跎，忙扑灭了火下山去。　　走到山脚是却又看到一个女的，这次可是全部看到了，白衣服，长发，就在我门前面，我们刚犹豫一下，就不见了那人影，走前点一看，只看到一块十分烂的墓碑斜倒在那里，碑都是黄色的了，墓已经被人踏平了，大家经过是都作了个揖，然后狂奔下了山。";
        //     String b = "这种事以前我都不信的，但是后来我的小侄女身上就发生了一件事。　　我老家是在一个很古老的小镇上，老街一带基本上都是明清时的建筑物，木质结构，而且那时的建筑物通常都有一个很大的大堂，也就是今天所谓的客厅。老房子本来就光线不足，加上年代久远，就变得阴暗潮湿。我三姨家就是这样的房子。小侄女刚出世，就特别爱哭，尤其是一经过大堂就哭的更厉害，怎么也哄不住，大家开始还都以为是婴儿的正常表现，也就没管。后来慢慢的大了点，就发现她经常是望着一个地方哭，哭两声就拼命要人抱走，大人还只当她不喜欢屋里的黑暗。家里第一次察觉不对时是在她两岁多能说话的时候。　　有一天，我姨父抱着她经过大堂，她又望着大梁的角落上，怯怯地说：“爷爷，你看，你看，她的眼睛好吓人啊~~~~~“可姨父转头望去，什么都没有。可她还是使劲的指着那个方向。从此以后她每次经过那里都会指着那个地方对人说：“你看啊！你看嘛！她的眼睛好吓人啊~~~~“　 　　后来我听三姨说，他们家以前就有个女孩子不止一次看到过一个穿红衣服的女子，现在小侄女看到的也可能是同一个。大概后来是请了人去家做了法事吧，再也没听说小侄女成天的哭了，也不怕到大堂里去了。我小时候也常看见有人影从旁边一闪而过，不过不知道是不是幻觉。";

        //  String b = "134-0345-4321";
        //  String a = "134-0345-4321";

        /*
         * String a = "好强啊"; String b = "恨人啊";
         */
        /*
         * String a = "很漂亮，很长的腿啊"; String b = "伪娘啊";
         */
        System.out.println("dice : " + sim_dice(a, b));
        System.out.println("jaccard : " + sim_jaccard(a, b));
        System.out.println("cosine : " + sim_cosine(a, b));
        System.out.println("overlap : " + sim_overlap(a, b));

    }
}
