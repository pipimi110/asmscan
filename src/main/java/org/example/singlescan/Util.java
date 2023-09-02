package org.example.singlescan;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.File;

public class Util {
    public static List<URL> classPathUrls = new ArrayList<>();
    public static String sep = System.getProperty("file.separator");

    public static ClassLoader getJarAndLibClassLoader(String directoryPath) throws Exception {
        // 开始递归遍历目录
        loadClassesRecursively(directoryPath, classPathUrls, false);
        // 创建一个新的URLClassLoader，将目录URL添加到类加载路径中
        // parent=null 减少加载的类
        URLClassLoader classLoader = new URLClassLoader(classPathUrls.toArray(new URL[classPathUrls.size()]), null);
        return classLoader;

    }

    private static void loadClassesRecursively(String directoryPath, List<URL> classPathUrls, boolean start) throws Exception {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
//                if (file.isFile() && file.getName().endsWith(".class")) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    if (start) {//第一层目录通常有多个jar和jar解压的目录,不做jar添加
                        // 获取类文件的绝对路径并转换为URL
                        URL classUrl = file.toURI().toURL();
                        classPathUrls.add(classUrl);
                    }
                } else if (file.isDirectory()) {
                    if (file.getPath().contains(sep+"BOOT-INF") || !start) {//要求第二层存在BOOT-INF目录
                        if (file.getPath().endsWith(sep+"BOOT-INF"+sep+"classes")) {
                            URL classUrl = file.toURI().toURL();
                            classPathUrls.add(classUrl);
                        }else{
                        // 递归处理子目录
                            loadClassesRecursively(file.getAbsolutePath(), classPathUrls, true);
                        }
                    }
                }
            }
        }
    }


    public static ClassLoader getJarAndLibClassLoader_0(Path path) throws IOException {


        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(path))) {
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                Path fullPath = path.resolve(jarEntry.getName());
                if (!jarEntry.isDirectory()) {
                    Path dirName = fullPath.getParent();
                    if (dirName == null) {
                        throw new IllegalStateException("Parent of item is outside temp directory.");
                    }
                    addClassPathUrls(classPathUrls, fullPath);
                }
            }
        }
        URLClassLoader classLoader = new URLClassLoader(classPathUrls.toArray(new URL[classPathUrls.size()]));
        return classLoader;
    }

    public static void addClassPathUrls(List<URL> classPathUrls, Path path) throws IOException {
        //spring-boot
        if (Files.exists(path.resolve("BOOT-INF"))) {
            classPathUrls.add(path.resolve("BOOT-INF/classes").toUri().toURL());
            Files.list(path.resolve("BOOT-INF/lib")).forEach(p -> {
                try {
                    classPathUrls.add(p.toUri().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        //shadow jar
        else {
            classPathUrls.add(path.toUri().toURL());
        }
    }
}
