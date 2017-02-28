package jmind.base.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   对@somebody 字符串操作
 * @author weibo-xie
 * 2012-10-30
 */
public class AtMeUtil {
    private static final String userRegEx = "(@)([^@:：/\\.。,#\\$\\s]+?)([@:：/\\.。,#\\$\\s])";

    /**
     * 提取用户名信息
     * 根据传进来的内容信息，获取符合规则的用户名并放入list返回
     * 规则：匹配以@开头以：: 。#$及空白符 结尾之间的字符匹配出来
     * @param content
     * @return
     */
    public static Set<String> getAtNames(String content) {
        Pattern p = Pattern.compile(userRegEx);
        Matcher m = p.matcher(content + " ");
        final Set<String> userList = new HashSet<String>();
        try {
            while (m.find()) {
                userList.add(m.group(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 为符合规则的用户名添加链接地址
     * @param content
     * @return
     */
    public static String addLink(String content, String prefix) {
        try {
            //String c="<a href=\"http://www.mop.com?uname=$2\">$1$2</a>$3" ;
            // 根据昵称的连接
            //content = content.replace("@", " @");
            content = content.replaceAll(userRegEx, "<a target='_blank'   username='$2' href='" + prefix + "/$2"
                    + "'>$1$2</a>$3");
            //content = content.replace(" ", "");
        } catch (Exception e) {
            e.printStackTrace();
            return DataUtil.EMPTY;
        }

        return content;
    }

    public static void main(String args[]) {
        String s = "@luanxibac//@汇美丽化妆品商城 发布汇美丽-美丽婚礼";

        String userList = addLink(s, "/n");
        System.out.println(userList);

        //        Set<String> set = getAtNames(s);
        //        System.out.println(set);
        //        System.out.println(set.size());

    }

}
