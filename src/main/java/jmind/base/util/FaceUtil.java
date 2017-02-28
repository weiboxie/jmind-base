package jmind.base.util;

import java.util.Map;
import java.util.Set;

import jmind.base.lang.SourceProperties;

/**
 * 表情工具类
 * @author weibo-xie
 * 2012-10-31
 */
public class FaceUtil {
    private static final int maxIconForPage = 32;

    private static final SourceProperties face = new SourceProperties("face.properties");
    private static final SourceProperties vipFace = new SourceProperties("vipface.properties");

    private static final String START_TAG = "[link]";
    private static final String END_TAG = "[/link]";

    public static String replaceLink(String content) {
        int linkStart = content.indexOf(START_TAG);
        int linkEnd = 0;
        String link = null;
        while (linkStart != -1) {
            linkEnd = content.indexOf(END_TAG, linkStart + 1);
            if (linkEnd <= linkStart)
                break;
            link = content.substring(linkStart + START_TAG.length(), linkEnd);
            boolean hasFace = false;
            //            for (int i = 0; i < ARR_FACE_SRC.length; ++i){
            //                if (link.indexOf(ARR_FACE_SRC[i]) != -1) {
            //                    hasFace = true;
            //                    break;
            //                }
            //            }
            if (!hasFace) {
                link = "<a href=\"" + link + "\" target='_blank'>" + link + "</a>";
                content = content.substring(0, linkStart) + link + content.substring(linkEnd + END_TAG.length());
            }
            linkStart = content.indexOf(START_TAG, linkStart + 1);
        }
        return content;
    }

    public static String replaceFace(String content) {
        Map<?, ?> p = face.getProperties();
        for (Object k : p.keySet()) {
            String key = k.toString();
            if (content.indexOf(key) > -1) {
                content = content.replace(key, "<img src='" + face.getProperty(key) + "' />");
            }
        }
        p = vipFace.getProperties();
        for (Object k : p.keySet()) {
            String key = k.toString();
            if (content.indexOf(key) > -1) {
                content = content.replace(key, "<img src='" + vipFace.getProperty(key) + "' />");
            }
        }

        return content;
    }

    public static String getFaceHtml() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<div  id='faceBar' class='boxlayer2' style='top:28px;left:-10px;z-index:999999;'><b>&nbsp;</b>");
        sb.append("<h4 ><em class='on' id='face_pt' >粉丝表情</em><em id='face_vip' >高级粉丝表情</em></h4>");
        sb.append("<div class='mask' id='face_mask' style='display:none' ></div>");
        sb.append(" <div class='txttip' id='face_txttip' style='display:none' ></div>");

        Set<?> keys = face.getProperties().keySet();
        int ptPageCount = keys.size() % maxIconForPage == 0 ? keys.size() / maxIconForPage : keys.size()
                / maxIconForPage + 1;
        int pt_count = 0;
        sb.append("<ul class='imglist' id='imgListBar_pt_1'>");
        for (Object k : keys) {
            String key = k.toString();
            if (pt_count != 0 && pt_count % maxIconForPage == 0) {
                sb.append("</ul>");
                sb.append("<ul class='imglist' id='imgListBar_pt_").append(pt_count / maxIconForPage + 1)
                        .append("' style='display:none' >");
            }
            sb.append("<li><img src='").append(face.getProperty(key)).append("' title='").append(key)
                    .append("' /></li>");
            pt_count++;
        }
        sb.append("</ul>");

        sb.append("<ul class='imglist' id='imgListBar_vip_1' style='display:none'>");
        Set<?> Vipkeys = vipFace.getProperties().keySet();
        int VipPageCount = keys.size() % maxIconForPage == 0 ? keys.size() / maxIconForPage : keys.size()
                / maxIconForPage + 1;
        int vip_count = 0;
        for (Object k : Vipkeys) {
            String key = k.toString();
            if (vip_count != 0 && vip_count % maxIconForPage == 0) {
                sb.append("</ul>");
                sb.append("<ul class='imglist' id='imgListBar_vip_").append(vip_count / maxIconForPage + 1)
                        .append("' style='display:none' >");
            }
            sb.append("<li><img src='").append(vipFace.getProperty(key)).append("' title='").append(key)
                    .append("' /></li>");
            vip_count++;
        }
        sb.append("</ul>");
        sb.append("<div class='fspag' id='pt_face_page'><em>");
        if (ptPageCount > 1) {
            for (int i = 0; i < ptPageCount; i++) {
                sb.append("<a href='javascript:void(0);'");
                if (i == 0) {
                    sb.append("class='on'");
                }
                sb.append(">").append(i + 1 + "</a>");
            }
        }
        sb.append("</em><span><a href='http://cms.17k.com/news/1203.html' target='_blank' >什么是粉丝？</a></span></div>");

        sb.append("<div class='fspag' id='vip_face_page' style='display:none'><em>");
        if (VipPageCount > 1) {
            for (int i = 0; i < VipPageCount; i++) {
                sb.append("<a href='javascript:void(0);'");
                if (i == 0) {
                    sb.append("class='on'");
                }
                sb.append(">").append(i + 1 + "</a>");
            }
        }
        sb.append("</em><span><a href='http://cms.17k.com/news/1203.html' target='_blank'>什么是粉丝？</a></span></div>");

        sb.append("</div>");
        sb.append("<script type='text/javascript'>");
        sb.append("var FaceShow=function(who){");
        sb.append("$('#face_pt').removeClass('on');");
        sb.append("$('#face_vip').removeClass('on');");
        sb.append("$('#face_'+who).addClass('on');");
        sb.append("$('#faceBar ul').css('display','none');");
        sb.append("$('#imgListBar_'+who+'_1').css('display','block');");
        sb.append("$('.fspag').css('display','none');");
        sb.append("$('#'+who+'_face_page').css('display','block');}; ");

        sb.append("$('#pt_face_page a').bind('click',function(){");
        sb.append("$('#faceBar ul').css('display','none');");
        sb.append("$('#imgListBar_pt'+'_'+$(this).html()).css('display','block');");
        sb.append("$('#pt_face_page a').removeClass('on');");
        sb.append("$(this).addClass('on');");
        sb.append("});");

        sb.append("$('#vip_face_page a').bind('click',function(){");
        sb.append("$('#faceBar ul').css('display','none');");
        sb.append("$('#imgListBar_vip'+'_'+$(this).html()).css('display','block');");
        sb.append("$('#vip_face_page a').removeClass('on');");
        sb.append("$(this).addClass('on');");
        sb.append("});");

        sb.append("$('#face_vip').bind('click',function(){");
        sb.append("FaceShow('vip');");
        sb.append("});");

        sb.append("$('#face_pt').bind('click',function(){");
        sb.append("FaceShow('pt');");
        sb.append("})");

        sb.append("</script>");

        return sb.toString();
    }

}
