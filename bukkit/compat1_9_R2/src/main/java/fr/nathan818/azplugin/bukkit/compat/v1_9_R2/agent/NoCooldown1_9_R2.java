package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.IS_ATTACK_COOLDOWN_DISABLED;
import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.invokeCompatBridge;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CraftEntity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.Entity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityHuman1_9_R2;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

public class NoCooldown1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer(EntityHuman1_9_R2, EntityHumanTransformer::new);
    }

    private static class EntityHumanTransformer extends AZClassVisitor {

        public EntityHumanTransformer(int api, ClassVisitor cv) {
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
            if (name.equals("o") && descriptor.equals("(F)F")) {
                // public float o(float f) {
                //   if (CompatBridge.isNoCooldown(this.getBukkitEntity())) {
                //     return 1.0F;
                //   }
                //   [...]
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label elseLabel = newLabel();
                        loadThis();
                        invokeVirtual(t(Entity1_9_R2), new Method("getBukkitEntity", t(CraftEntity1_9_R2), NO_ARGS));
                        invokeCompatBridge(this, IS_ATTACK_COOLDOWN_DISABLED);
                        visitJumpInsn(Opcodes.IFEQ, elseLabel);
                        push(1.0F);
                        returnValue();
                        mark(elseLabel);
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
