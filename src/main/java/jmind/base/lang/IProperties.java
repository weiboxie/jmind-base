package jmind.base.lang;

import java.util.Map;

public interface IProperties {

    public String getProperty(String key);

    public boolean hasProperty(String key);

    public String getProperty(String key, String defaultVal);

    public Map<String, String> getProperties();

}
