package jmind.base.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jmind.base.util.AppEnvironment;

/**
 * 使用ConcurrentHashMap ，提高并发性能
 * 支持中文，不使用ascii 码,文件使用utf8 编码
 * @author xieweibo
 * @date 2016年1月19日
 */
public class SourceProperties extends ConcurrentHashMap<String, String> implements IProperties {

    private static volatile SourceProperties instance;

    public static SourceProperties getDataSource() {
        if (instance == null) {
            synchronized (SourceProperties.class) {
                if (instance == null) {
                    instance = new SourceProperties(AppEnvironment.getEnvironment() + "-datasource.properties");
                }
            }
        }
        return instance;
    }

    public SourceProperties() {

    }

    public SourceProperties(String name) {
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }
                int index = line.indexOf("=");
                if (index > 0) {
                    put(line.substring(0, index).trim(), line.substring(index + 1).trim());
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> getProperties() {
        return this;
    }

    @Override
    public String getProperty(String key) {
        return get(key);

    }

    @Override
    public String getProperty(String key, String defaultVal) {
        return getOrDefault(key,defaultVal);
    }

    public boolean hasProperty(String key) {
        return containsKey(key);
    }

}
