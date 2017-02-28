package jmind.base.util;

public class AppEnvironment {

    private static final String APP_ENV = "APP_ENV";

    public static final String TEST = "test";
    public static final String PRODUCTION = "production";

    public static void setEnvironment(String env) {
        System.setProperty(APP_ENV, env);
    }

    /**
     * 获取当前环境
     * @return
     */
    public static final String getEnvironment() {
        // 优先 -DLETV_ENV=test
        // export LETV_ENV=production
        String environment = getProperty(APP_ENV,TEST);
        return environment.toLowerCase();
    }

    /**
     *  是否测试环境
     * @return
     */
    public static boolean isTest() {
        return getEnvironment().startsWith(TEST);
    }

    public static boolean isProduction() {
        return getEnvironment().startsWith(PRODUCTION);
    }

    public static final String getProperty(String name){
        String environment = System.getProperty(name);
        if (DataUtil.isEmpty(environment))
            environment = System.getenv(name);
        return environment;
    }

    public static final String getProperty(String name,String defaultVal){
        String environment=getProperty(name);
        if(DataUtil.isEmpty(environment))
            return defaultVal;
        return environment;
    }


}
