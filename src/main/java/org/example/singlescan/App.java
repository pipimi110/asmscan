package org.example.singlescan;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URL;
import java.net.URLClassLoader;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    //    private static ClassLoader initJarData(String[] args, boolean boot, int argIndex, AtomicBoolean haveNewJar, List<Path> pathList)
    private static void loadtest(ClassLoader classLoader, String class1) {
        try {
            classLoader.loadClass(class1);
        } catch (ClassNotFoundException e) {
            logger.info("Could not load class");
        }
    }

    private static ClassLoader initJarData(String directory)
            throws Exception {
        ClassLoader classLoader = null;
        //todo:改成scan
//            //程序参数的最后一部分，即最后一个具有前缀--的参数（例：--resume）后
//            if (args.length == argIndex + 1 && args[argIndex].toLowerCase().endsWith(".war")) {
//                //加载war文件
//                Path path = Paths.get(args[argIndex]);
//                LOGGER.info("Using WAR classpath: " + path);
//                //实现为URLClassLoader，加载war包下的WEB-INF/lib和WEB-INF/classes
//                classLoader = Util.getWarClassLoader(path);
//            } else if (args.length == argIndex + 1 && args[argIndex].toLowerCase().endsWith(".jar")
//                && boot) {
//                Path path = Paths.get(args[argIndex]);
//                LOGGER.info("Using JAR classpath: " + path);

        boolean test = false;
        if (test) {
//            URL jarFileUrl = new URL("file:/" + jar);
            URL jarFileUrl = new URL("file:/E:/java_my_proj/springDemoPy/target/classes/");
            classLoader = new URLClassLoader(new URL[]{jarFileUrl}, null);
            //加载不到springJar里的class
            //parent=null 减少加载的类
        } else {
            //实现为URLClassLoader，递归加载jar包下的BOOT-INF/lib和BOOT-INF/classes
            classLoader = Util.getJarAndLibClassLoader(directory);
            //加入class也加载不到
            //正确加入不是应该加入class目录吗,没什么要递归
        }
//            } else {
//                //加载jar文件，java命令后部，可配置多个
//                Set<String> scanJarHistory = new HashSet<>();
//                Path filePath = Paths.get(ConfigHelper.historyRecordFile);
//                if (ConfigHelper.history && Files.exists(filePath)) {
//                    try (InputStream inputStream = Files
//                        .newInputStream(filePath);
//                        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
//                        while (scanner.hasNext()) {
//                            String jar = scanner.nextLine();
//                            if (jar.length() > 0) {
//                                scanJarHistory.add(jar.trim());
//                            }
//                        }
//                    }
//                }
//                AtomicInteger jarCount = new AtomicInteger(0);
//                for (int i = 0; i < args.length - argIndex; i++) {
//                    String pathStr = args[argIndex + i];
//                    if (!pathStr.endsWith(".jar")) {
//                        //todo 主要用于大批量的挖掘链
//                        //非.jar结尾，即目录，需要遍历目录找出所有jar文件
//                        File file = Paths.get(pathStr).toFile();
//                        if (file == null || !file.exists())
//                            continue;
//                        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
//                            @Override
//                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
//                                if (!file.getFileName().toString().endsWith(".jar"))
//                                    return FileVisitResult.CONTINUE;
//                                File readFile = file.toFile();
//                                Path path = Paths.get(readFile.getAbsolutePath());
//                                if (Files.exists(path)) {
//                                    if (ConfigHelper.history) {
//                                        if (!scanJarHistory.contains(path.getFileName().toString())) {
//                                            if (jarCount.incrementAndGet() <= ConfigHelper.maxJarCount) {
//                                                pathList.add(path);
//                                            }
//                                        }
//                                    } else {
//                                        if (jarCount.incrementAndGet() <= ConfigHelper.maxJarCount) {
//                                            pathList.add(path);
//                                        }
//                                    }
//                                }
//                                return FileVisitResult.CONTINUE;
//                            }
//                        });
//
//                        continue;
//                    }
//                    Path path = Paths.get(pathStr).toAbsolutePath();
//                    if (!Files.exists(path)) {
//                        throw new IllegalArgumentException("Invalid jar path: " + path);
//                    }
//                    pathList.add(path);
//                }
//                LOGGER.info("Using classpath: " + Arrays.toString(pathList.toArray()));
//                //实现为URLClassLoader，加载所有指定的jar
//                classLoader = Util.getJarClassLoader(pathList.toArray(new Path[0]));
//            }

//        haveNewJar.set(pathList.size() != 0);
        return classLoader;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java -jar xxx.jar dir org.example.demo");
        } else {
            String dir = args[0];
            ClassLoader classLoader = initJarData(dir);
            System.out.println(classLoader);
            String pkg = args[1];
            Analyser.singlescan(classLoader, pkg);
//        test(args);
        }
    }

    public static void test(String[] args) throws Exception {
//        String jar = "E:\\java_my_proj\\mvn_quickstart_mygadget\\target\\mvn_quickstart-1.0-SNAPSHOT";
//        String class1 = "org.example.Sink";
//        String jar = "E:\\java_my_proj\\springDemoPy\\target\\demo-0.0.1-SNAPSHOT.jar";
//        String jar = "E:\\java_my_proj\\springDemoPy\\target\\demo-0.0.1-SNAPSHOT\\BOOT-INF";
        String dir = "E:\\java_my_proj\\springDemoPy\\target\\demo-0.0.1-SNAPSHOT";
        String class1 = "com.example.demo.controller.DemoController";
//        Path path = Paths.get(jar);

        ClassLoader classLoader = initJarData(dir);
        System.out.println(classLoader);
        String pkg = "com.example";
        //        loadtest(classLoader, class1);
        Analyser.singlescan(classLoader, pkg);
    }
}
