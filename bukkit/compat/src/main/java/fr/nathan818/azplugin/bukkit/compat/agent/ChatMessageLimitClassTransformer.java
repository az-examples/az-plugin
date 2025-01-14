package fr.nathan818.azplugin.bukkit.compat.agent;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ChatMessageLimitClassTransformer extends ClassVisitor {

    private final int defaultLimit;
    private final int newLimit;

    @Getter
    private int refCount;

    public ChatMessageLimitClassTransformer(int api, @Nullable ClassVisitor cv, int defaultLimit, int newLimit) {
        super(api, cv);
        this.defaultLimit = defaultLimit;
        this.newLimit = newLimit;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
            @Override
            public void visitIntInsn(int opcode, int operand) {
                if ((opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) && operand == defaultLimit) {
                    ++refCount;
                    if (cv != null) {
                        opcode = Opcodes.SIPUSH;
                        operand = newLimit;
                    }
                }
                super.visitIntInsn(opcode, operand);
            }
        };
    }
}
