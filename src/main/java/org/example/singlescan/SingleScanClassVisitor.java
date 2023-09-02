package org.example.singlescan;

import org.objectweb.asm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SingleScanClassVisitor extends ClassVisitor {
    private List<String> singleTargetAnnotation = new ArrayList<>();
    private boolean isTarget = false;
    private List<String> fields = new ArrayList<>();
    private String name;

    public SingleScanClassVisitor() {
        super(Opcodes.ASM5);
        loadTarget();
    }

    public void loadTarget() {
        InputStream in = SingleScanClassVisitor.class.getClassLoader().getResourceAsStream("target.properties");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String str = null;
        try {
            while ((str = bufferedReader.readLine()) != null) {
//                System.out.println(str);
                singleTargetAnnotation.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.name = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        // 检查类的注解
        if (singleTargetAnnotation.contains(descriptor)) {
            isTarget = true;
            System.out.println("Class Annotation: " + descriptor);
            System.out.println("Class Name: " + name);
        }

        // 如果需要进一步处理注解内容，可以返回一个自定义的 AnnotationVisitor
        return super.visitAnnotation(descriptor, visible);
    }

    // 处理字段
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (isTarget) {
            // 输出字段信息
//            System.out.println("Field Name: " + name);
//            System.out.println("Field Type: " + descriptor);
//            System.out.println("Access Flags: " + access);
            fields.add(name);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (isTarget) {
            mv = new SingleScanMethodVisitor(this.name + "." + name + descriptor);
        }
        return mv;
    }

    class SingleScanMethodVisitor extends MethodVisitor {
        private String desc;

        public SingleScanMethodVisitor(String desc) {
            super(Opcodes.ASM5);
            this.desc = desc;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            // 检查字段访问指令，例如 getfield、putfield、getstatic、putstatic
            // 这里可以根据需要添加更多的逻辑来处理字段访问
            if (fields.contains(name)) { //排除Field Access: 178 java/lang/System.out
                System.out.println("[*]Method Desc: " + desc);
                System.out.println("[*]Field Access: " + opcode + " " + owner + "." + name);
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }
}
