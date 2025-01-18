package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.CALL_ENTITY_TRACK_BEGIN_EVENT;
import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.invokeCompatBridge;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CraftEntity1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CraftPlayer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.Entity1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EntityPlayer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EntityTrackerEntry1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.Packet1_8_R3;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class EntityTrackEventTransformers1_8_R3 {

    public static void register(Agent agent) {
        agent.addTransformer(EntityTrackerEntry1_8_R3, EntityTrackerEntryTransformer::new);
    }

    private static class EntityTrackerEntryTransformer extends AZClassVisitor {

        private boolean inserted;

        private int flagLocalIndex = -1;
        private boolean invokeCreateSpawnPacket;
        private boolean setFlagInserted;

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
                    public void visitCode() {
                        super.visitCode();
                        // boolean flag = false;
                        flagLocalIndex = newLocal(BOOLEAN_TYPE);
                        push(false);
                        storeLocal(flagLocalIndex);
                    }

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
                            opcode == Opcodes.INVOKESPECIAL &&
                            EntityTrackerEntry1_8_R3.equals(owner) &&
                            "c".equals(name) &&
                            ("()L" + Packet1_8_R3 + ";").equals(descriptor)
                        ) {
                            invokeCreateSpawnPacket = true;
                        }
                    }

                    @Override
                    public void visitVarInsn(int opcode, int varIndex) {
                        super.visitVarInsn(opcode, varIndex);
                        if (opcode == Opcodes.ASTORE && invokeCreateSpawnPacket) {
                            // flag = true;
                            invokeCreateSpawnPacket = false;
                            setFlagInserted = true;
                            push(true);
                            storeLocal(flagLocalIndex);
                        }
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == Opcodes.RETURN && setFlagInserted) {
                            // if (flag) callEntityTrackBeginEvent();
                            inserted = true;
                            Label label = new Label();
                            loadLocal(flagLocalIndex);
                            visitJumpInsn(Opcodes.IFEQ, label);
                            insertCallEntityTrackBeginEvent(this);
                            visitLabel(label);
                        }
                        super.visitInsn(opcode);
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
            mg.getField(t(EntityTrackerEntry1_8_R3), "tracker", t(Entity1_8_R3));
            mg.invokeVirtual(t(Entity1_8_R3), new Method("getBukkitEntity", t(CraftEntity1_8_R3), NO_ARGS));
            mg.loadArg(0);
            mg.invokeVirtual(t(EntityPlayer1_8_R3), new Method("getBukkitEntity", t(CraftPlayer1_8_R3), NO_ARGS));
            invokeCompatBridge(mg, CALL_ENTITY_TRACK_BEGIN_EVENT);
        }
    }
}
