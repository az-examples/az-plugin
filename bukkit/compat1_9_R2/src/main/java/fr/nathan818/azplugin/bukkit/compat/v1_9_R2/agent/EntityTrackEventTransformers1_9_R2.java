package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.CALL_ENTITY_TRACK_BEGIN_EVENT;
import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.invokeCompatBridge;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class EntityTrackEventTransformers1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer("net/minecraft/server/v1_9_R2/EntityTrackerEntry", (loader, className, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite(
                EntityTrackerEntryTransformer::new,
                ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
                ClassRewriter.DEFAULT_WRITER_FLAGS
            );
            return crw.getBytes();
        });
    }

    private static class EntityTrackerEntryTransformer extends ClassVisitor {

        private String className;
        private boolean inserted;

        public EntityTrackerEntryTransformer(int api, ClassVisitor cv) {
            super(api, cv);
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
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("updatePlayer".equals(name)) {
                return new GeneratorAdapter(api, mv, access, name, descriptor) {
                    @Override
                    public void visitMethodInsn(
                        int opcode,
                        String owner,
                        String name,
                        String descriptor,
                        boolean isInterface
                    ) {
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        if (
                            opcode == Opcodes.INVOKEVIRTUAL &&
                            "net/minecraft/server/v1_9_R2/EntityPlayer".equals(owner) &&
                            "d".equals(name) &&
                            "(Lnet/minecraft/server/v1_9_R2/Entity;)V".equals(descriptor)
                        ) {
                            inserted = true;
                            insertCallEntityTrackBeginEvent(this);
                        }
                    }
                };
            }
            return mv;
        }

        @Override
        public void visitEnd() {
            if (inserted) {
                addInfo(cv, className, "Added EntityTrackBeginEvent calls");
            }
            super.visitEnd();
        }

        private void insertCallEntityTrackBeginEvent(GeneratorAdapter mg) {
            // CompatBridge.callEntityTrackBeginEvent(this.tracker.getBukkitEntity(), entityplayer.getBukkitEntity());
            mg.loadThis();
            mg.getField(
                Type.getObjectType("net/minecraft/server/v1_9_R2/EntityTrackerEntry"),
                "tracker",
                Type.getObjectType("net/minecraft/server/v1_9_R2/Entity")
            );
            mg.invokeVirtual(
                Type.getObjectType("net/minecraft/server/v1_9_R2/Entity"),
                new Method(
                    "getBukkitEntity",
                    Type.getObjectType("org/bukkit/craftbukkit/v1_9_R2/entity/CraftEntity"),
                    NO_ARGS
                )
            );
            mg.loadArg(0);
            mg.invokeVirtual(
                Type.getObjectType("net/minecraft/server/v1_9_R2/EntityPlayer"),
                new Method(
                    "getBukkitEntity",
                    Type.getObjectType("org/bukkit/craftbukkit/v1_9_R2/entity/CraftPlayer"),
                    NO_ARGS
                )
            );
            invokeCompatBridge(mg, CALL_ENTITY_TRACK_BEGIN_EVENT);
        }
    }
}
