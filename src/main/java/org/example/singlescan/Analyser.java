package org.example.singlescan;

import com.google.common.reflect.ClassPath;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

public class Analyser {
    public static void singlescan(ClassLoader classLoader, String pkg) throws IOException {
        for (ClassPath.ClassInfo classInfo : ClassPath.from(classLoader).getAllClasses()) {
            try {
                if (classInfo.getName().startsWith(pkg)) {
//                    System.out.println("LoadClass: " + classInfo);
                    String resourceName = classInfo.getResourceName();
                    InputStream in = classLoader.getResourceAsStream(resourceName);
                    ClassReader cr = new ClassReader(in);
                    //使用asm的ClassVisitor、MethodVisitor，利用观察模式去扫描所有的class和method并记录
                    cr.accept(new SingleScanClassVisitor(), ClassReader.EXPAND_FRAMES);
                }
            } catch (Exception e) {
                System.err.println("LoadError: " + classInfo);
            }
        }
    }

}
