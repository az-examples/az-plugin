package fr.nathan818.azplugin.bungee.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import fr.nathan818.azplugin.common.utils.asm.ClassTransformer;
import java.util.logging.Level;
import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class PacketTransformers {

    private static final int NEW_CHAT_MESSAGE_LIMIT = 16384;

    public static void register(Agent agent) {
        ClassTransformer chatTransformer = (className, bytes) -> {
            ClassRewriter crw = new ClassRewriter(bytes);
            ChatPacketTransformer tr = crw.rewrite(ChatPacketTransformer::new);
            if (tr.getRefCount() > 0) {
                log(Level.INFO, "Successfully increased {0} message limit to {1}", className, NEW_CHAT_MESSAGE_LIMIT);
            }
            return crw.getBytes();
        };
        agent.addTransformer("net/md_5/bungee/protocol/packet/Chat", chatTransformer);
        agent.addTransformer("net/md_5/bungee/protocol/packet/ClientChat", chatTransformer);
    }

    @Getter
    private static class ChatPacketTransformer extends ClassVisitor {

        private int refCount = 0;

        public ChatPacketTransformer(int api, ClassVisitor mv) {
            super(api, mv);
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            if ("read".equals(name) || "write".equals(name)) {
                return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                    @Override
                    public void visitIntInsn(int opcode, int operand) {
                        if (
                            (opcode == Opcodes.BIPUSH && operand == 100) || (opcode == Opcodes.SIPUSH && operand == 256)
                        ) {
                            ++refCount;
                            opcode = Opcodes.SIPUSH;
                            operand = NEW_CHAT_MESSAGE_LIMIT;
                        }
                        super.visitIntInsn(opcode, operand);
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
