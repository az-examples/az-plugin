package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
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
        agent.addTransformer(className, (api, cv) ->
            new ChatLimitTransformer(api, cv, defaultLimit, minRefCount, maxRefCount)
        );
    }

    private static class ChatLimitTransformer extends AZClassVisitor {

        private final int defaultLimit;
        private final int minRefCount;
        private final int maxRefCount;

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
            expandFrames = true;
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
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
                    getClassName(),
                    "Increased chat message limit from {0} to {1}",
                    defaultLimit,
                    NEW_CHAT_MESSAGE_LIMIT
                );
            }
            super.visitEnd();
        }
    }
}
