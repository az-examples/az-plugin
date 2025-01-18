package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.CALL_ENTITY_TRACK_BEGIN_EVENT;
import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.invokeCompatBridge;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CraftEntity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CraftPlayer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.Entity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityPlayer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityTrackerEntry1_9_R2;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class EntityTrackEventTransformers1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer(EntityTrackerEntry1_9_R2, EntityTrackerEntryTransformer::new);
    }

    private static class EntityTrackerEntryTransformer extends AZClassVisitor {

        private boolean inserted;

        public EntityTrackerEntryTransformer(int api, ClassVisitor cv) {
            super(api, cv);
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
            if ("updatePlayer".equals(name)) {
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
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
                            EntityPlayer1_9_R2.equals(owner) &&
                            "d".equals(name) &&
                            ("(L" + Entity1_9_R2 + ";)V").equals(descriptor)
                        ) {
                            inserted = true;
                            insertCallEntityTrackBeginEvent(this);
                        }
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            if (inserted) {
                addInfo(cv, getClassName(), "Added EntityTrackBeginEvent calls");
            }
            super.visitEnd();
        }

        private void insertCallEntityTrackBeginEvent(AZGeneratorAdapter mg) {
            // CompatBridge.callEntityTrackBeginEvent(this.tracker.getBukkitEntity(), entityplayer.getBukkitEntity());
            mg.loadThis();
            mg.getField(t(EntityTrackerEntry1_9_R2), "tracker", t(Entity1_9_R2));
            mg.invokeVirtual(t(Entity1_9_R2), new Method("getBukkitEntity", t(CraftEntity1_9_R2), NO_ARGS));
            mg.loadArg(0);
            mg.invokeVirtual(t(EntityPlayer1_9_R2), new Method("getBukkitEntity", t(CraftPlayer1_9_R2), NO_ARGS));
            invokeCompatBridge(mg, CALL_ENTITY_TRACK_BEGIN_EVENT);
        }
    }
}
