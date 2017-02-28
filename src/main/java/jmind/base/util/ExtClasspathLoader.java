package jmind.base.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 动态加载配置文件或jar
 * @author wbxie
 * 2013-8-6
 */
public final class ExtClasspathLoader {
    /** URLClassLoader的addURL方法 */
    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * 加载jar包
     * 2013-8-6 
     * @param filepath
     */
    public static void loadJars(String filepath) {
        File file = new File(filepath);
        loopFiles(file);
    }

    /**
     * 加载目录到classpath,资源配置文件
     * 2013-8-6 
     * @param filepath
     */
    public static void loadResourceDir(String directory) {
        File file = new File(directory);
        loopDirs(file);
    }

    /**
     * 初始化addUrl 方法.
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**    
     * 循环遍历目录，找出所有的资源路径。
     * @param file 当前遍历文件
     */
    private static void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }

    /**    
     * 循环遍历目录，找出所有的jar包。
     * @param file 当前遍历文件
     */
    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }

    /**
     * 通过filepath加载文件到classpath。
     * @param filePath 文件路径
     * @return URL
     * @throws Exception 异常
     */
    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[] { file.toURI().toURL() });
        } catch (Exception e) {
        }
    }

}
