package jmind.base.util;

/**
 * description:
 *
 * @author weibo.xie
 * @date:2021/8/13 下午2:03
 */
public enum  OS {
    MAC,
    WINDOWS,
    LINUX;

    /**
     * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,linux 等.
     */
    public static String OS_NAME =System.getProperty("os.name").toLowerCase();

    public static OS OS_SYSTEM=getOS();



    private static  OS getOS(){
        if(OS_NAME.contains("mac")){
            return MAC;
        }else if(OS_NAME.contains("linux")){
            return LINUX;
        }else if(OS_NAME.contains("windows")){
            return WINDOWS;
        }
        return WINDOWS;
    }

}
