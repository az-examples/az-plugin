package fr.nathan818.azplugin.bukkit.compat.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.GeneratorAdapter;

class GetItemStackHandleTransformer extends ClassVisitor {

    public GetItemStackHandleTransformer(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ("getItemStackHandle".equals(name)) {
            GeneratorAdapter mg = new GeneratorAdapter(mv, access, name, descriptor);
            mg.loadArg(0);
            mg.getField(mg.getArgumentTypes()[0], "handle", mg.getReturnType());
            mg.returnValue();
            mg.endMethod();
            return mg;
        }
        return mv;
    }
}
