package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChatPacketTransformers {

    private static final int NEW_CHAT_MESSAGE_LIMIT = 16384;

    public static void registerChatPacketTransformer(
        Agent agent,
        String className,
        int defaultLimit,
        int minRefCount,
        int maxRefCount
    ) {
        agent.addTransformer(className, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) -> new ChatLimitTransformer(api, cv, defaultLimit, minRefCount, maxRefCount));
            return crw.getBytes();
        });
    }

    private static class ChatLimitTransformer extends ClassVisitor {

        private final int defaultLimit;
        private final int minRefCount;
        private final int maxRefCount;

        private String className;
        private int refCount;

        public ChatLimitTransformer(
            int api,
            @Nullable ClassVisitor cv,
            int defaultLimit,
            int minRefCount,
            int maxRefCount
        ) {
            super(api, cv);
            this.defaultLimit = defaultLimit;
            this.minRefCount = minRefCount;
            this.maxRefCount = maxRefCount;
        }

        @Override
        public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces
        ) {
            className = name;
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
                        opcode = Opcodes.SIPUSH;
                        operand = NEW_CHAT_MESSAGE_LIMIT;
                    }
                    super.visitIntInsn(opcode, operand);
                }
            };
        }

        @Override
        public void visitEnd() {
            if (refCount < minRefCount || refCount > maxRefCount) {
                throw new IllegalStateException(
                    "Failed to increase chat message limit (" +
                    ("count=" + refCount) +
                    (", min=" + minRefCount) +
                    (", max=" + maxRefCount) +
                    ")"
                );
            }
            if (refCount > 0 && cv != null) {
                addInfo(
                    cv,
                    className,
                    "Increased chat message limit from {0} to {1}",
                    defaultLimit,
                    NEW_CHAT_MESSAGE_LIMIT
                );
            }
            super.visitEnd();
        }
    }
}
