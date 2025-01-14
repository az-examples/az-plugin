package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;

import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

@Getter
class EntityTrackEventClassTransformer1_8_R3 extends ClassVisitor {

    private boolean inserted;

    private int flagLocalIndex = -1;
    private boolean invokeCreateSpawnPacket;
    private boolean setFlagInserted;

    public EntityTrackEventClassTransformer1_8_R3(int api, ClassVisitor cv) {
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
        if ("updatePlayer".equals(name)) {
            return new GeneratorAdapter(api, mv, access, name, descriptor) {
                @Override
                public void visitCode() {
                    // boolean flag = false;
                    flagLocalIndex = newLocal(Type.BOOLEAN_TYPE);
                    push(false);
                    storeLocal(flagLocalIndex);
                    super.visitCode();
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
                        "net/minecraft/server/v1_8_R3/EntityTrackerEntry".equals(owner) &&
                        "c".equals(name) &&
                        "()Lnet/minecraft/server/v1_8_R3/Packet;".equals(descriptor)
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
        return mv;
    }

    private void insertCallEntityTrackBeginEvent(GeneratorAdapter mg) {
        // CompatBridge.callEntityTrackBeginEvent(this.tracker.getBukkitEntity(), entityplayer.getBukkitEntity());
        mg.loadThis();
        mg.getField(
            Type.getObjectType("net/minecraft/server/v1_8_R3/EntityTrackerEntry"),
            "tracker",
            Type.getObjectType("net/minecraft/server/v1_8_R3/Entity")
        );
        mg.invokeVirtual(
            Type.getObjectType("net/minecraft/server/v1_8_R3/Entity"),
            new Method(
                "getBukkitEntity",
                Type.getObjectType("org/bukkit/craftbukkit/v1_8_R3/entity/CraftEntity"),
                NO_ARGS
            )
        );
        mg.loadArg(0);
        mg.invokeVirtual(
            Type.getObjectType("net/minecraft/server/v1_8_R3/EntityPlayer"),
            new Method(
                "getBukkitEntity",
                Type.getObjectType("org/bukkit/craftbukkit/v1_8_R3/entity/CraftPlayer"),
                NO_ARGS
            )
        );
        mg.invokeStatic(
            Type.getObjectType("fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge"),
            new Method(
                "callEntityTrackBeginEvent",
                Type.VOID_TYPE,
                new Type[] {
                    Type.getObjectType("org/bukkit/entity/Entity"),
                    Type.getObjectType("org/bukkit/entity/Player"),
                }
            )
        );
    }
}
