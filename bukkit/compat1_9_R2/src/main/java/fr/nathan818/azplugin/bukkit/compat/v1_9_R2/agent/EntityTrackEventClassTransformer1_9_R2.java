package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;

import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

@Getter
class EntityTrackEventClassTransformer1_9_R2 extends ClassVisitor {

    private boolean inserted;

    public EntityTrackEventClassTransformer1_9_R2(int api, ClassVisitor cv) {
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
