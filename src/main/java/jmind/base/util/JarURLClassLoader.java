package jmind.base.util;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * description: 使用URLClassLoader动态加载jar
 * https://www.cnblogs.com/lichmama/p/12858517.html
 * @author weibo.xie
 * @date : create in 11:18 上午 2021/8/10
 */
public class JarURLClassLoader {
    private URL jar;
    private URLClassLoader classLoader;

    public JarURLClassLoader(URL jar) {
        this.jar = jar;
        classLoader = new URLClassLoader(new URL[] { jar });
    }

    /**
     * 在指定包路径下加载子类
     *
     * @param superClass
     * @param pkgName
     * @return
     */
    public Set<Class> loadClass(Class<?> superClass, String basePackage) {
        JarFile jarFile;
        try {
            jarFile = ((JarURLConnection) jar.openConnection()).getJarFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return loadClassFromJar(superClass, basePackage, jarFile);
    }

    private Set<Class> loadClassFromJar(Class<?> superClass, String basePackage, JarFile jar) {
        Set<Class> classes = new HashSet<>();
        String pkgPath = basePackage.replace(".", "/");
        Enumeration<JarEntry> entries = jar.entries();
        Class<?> clazz;
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.charAt(0) == '/') {
                entryName = entryName.substring(1);
            }
            if (jarEntry.isDirectory() || !entryName.startsWith(pkgPath) || !entryName.endsWith(".class")) {
                continue;
            }
            String className = entryName.substring(0, entryName.length() - 6);
            clazz = loadClass(className.replace("/", "."));
            if (clazz != null && !clazz.isInterface() && superClass.isAssignableFrom(clazz)) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    private Class<?> loadClass(String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}