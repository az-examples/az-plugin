package fr.nathan818.azplugin.common.utils.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.GeneratorAdapter;

public class AZGeneratorAdapter extends GeneratorAdapter {

    public AZGeneratorAdapter(int api, MethodVisitor mv, int access, String name, String descriptor) {
        super(api, mv, access, name, descriptor);
    }

    public AZGeneratorAdapter(
        int api,
        ClassVisitor cv,
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        super(api, cv.visitMethod(access, name, descriptor, signature, exceptions), access, name, descriptor);
    }
}
