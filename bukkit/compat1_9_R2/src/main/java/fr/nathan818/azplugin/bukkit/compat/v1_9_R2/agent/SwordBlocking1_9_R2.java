package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.IS_SWORD_BLOCKING_ENABLED;
import static fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentCompat.invokeCompatBridge;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CompatBridge1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.DamageSource1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityHuman1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityLiving1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EnumAnimation1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EnumHand1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EnumInteractionResult1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.InteractionResultWrapper1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.Item1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.ItemStack1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.ItemSword1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.World1_9_R2;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.CONSTRUCTOR_NAME;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.doVisit;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.Insn;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.JumpInsn;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.LocalVariable;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.VarInsn;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.Visit;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class SwordBlocking1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer(ItemSword1_9_R2, ItemSwordTransformer::new);
        agent.addTransformer(EntityLiving1_9_R2, EntityLivingTransformer::new);
    }

    private static class ItemSwordTransformer extends AZClassVisitor {

        public ItemSwordTransformer(int api, ClassVisitor cv) {
            super(api, cv);
            collectMethods = true;
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            if (name.equals(getUseDuration.getName()) && descriptor.equals(getUseDuration.getDescriptor())) {
                // public int e(ItemStack itemStack) {
                //   if (CompatBridge.isSwordBlockEnabled()) {
                //     return 72000;
                //   }
                //   [...]
                //   return super.e(...);
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label elseLabel = newLabel();
                        invokeCompatBridge(this, IS_SWORD_BLOCKING_ENABLED);
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        push(72000);
                        returnValue();
                        mark(elseLabel);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        loadThis();
                        loadArgs();
                        invokeConstructor(t(getSuperName()), getUseDuration);
                        returnValue();
                        super.visitMaxs(maxStack, maxLocals);
                    }
                };
            }
            if (name.equals(getUseAction.getName()) && descriptor.equals(getUseAction.getDescriptor())) {
                // public EnumAnimation f(ItemStack itemStack) {
                //   if (CompatBridge.isSwordBlockEnabled()) {
                //     return EnumAnimation.BLOCK;
                //   }
                //   [...]
                //   return super.f(...);
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label elseLabel = newLabel();
                        invokeCompatBridge(this, IS_SWORD_BLOCKING_ENABLED);
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        getStatic(t(EnumAnimation1_9_R2), "BLOCK", t(EnumAnimation1_9_R2));
                        returnValue();
                        mark(elseLabel);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        loadThis();
                        loadArgs();
                        invokeConstructor(t(getSuperName()), getUseAction);
                        returnValue();
                        super.visitMaxs(maxStack, maxLocals);
                    }
                };
            }
            if (name.equals(onRightClick.getName()) && descriptor.equals(onRightClick.getDescriptor())) {
                // public InteractionResultWrapper<ItemStack> a(ItemStack itemstack, World world, EntityHuman player, EnumHand hand) {
                //   if (CompatBridge.isSwordBlockEnabled()) {
                //     player.a(hand);
                //     return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
                //   }
                //   [...]
                //   return super.a(...);
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label elseLabel = newLabel();
                        invokeCompatBridge(this, IS_SWORD_BLOCKING_ENABLED);
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        loadArg(2);
                        loadArg(3);
                        invokeVirtual(
                            t(EntityHuman1_9_R2),
                            new Method("c", VOID_TYPE, new Type[] { t(EnumHand1_9_R2) })
                        );
                        newInstance(t(InteractionResultWrapper1_9_R2));
                        dup();
                        getStatic(t(EnumInteractionResult1_9_R2), "SUCCESS", t(EnumInteractionResult1_9_R2));
                        loadArg(0);
                        invokeConstructor(
                            t(InteractionResultWrapper1_9_R2),
                            new Method(
                                CONSTRUCTOR_NAME,
                                VOID_TYPE,
                                new Type[] { t(EnumInteractionResult1_9_R2), t(Object.class) }
                            )
                        );
                        returnValue();
                        mark(elseLabel);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        loadThis();
                        loadArgs();
                        invokeConstructor(t(getSuperName()), onRightClick);
                        returnValue();
                        super.visitMaxs(maxStack, maxLocals);
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            if (!getMethods().contains(getUseDuration)) {
                doVisit(visitMethod(ACC_PUBLIC, getUseDuration.getName(), getUseDuration.getDescriptor(), null, null));
            }
            if (!getMethods().contains(getUseAction)) {
                doVisit(visitMethod(ACC_PUBLIC, getUseAction.getName(), getUseAction.getDescriptor(), null, null));
            }
            if (!getMethods().contains(onRightClick)) {
                doVisit(visitMethod(ACC_PUBLIC, onRightClick.getName(), onRightClick.getDescriptor(), null, null));
            }
            super.visitEnd();
        }
    }

    private static class EntityLivingTransformer extends AZClassVisitor {

        public EntityLivingTransformer(int api, ClassVisitor cv) {
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
            if (name.equals("d") && descriptor.equals("(L" + DamageSource1_9_R2 + ";)Z")) {
                // "isShieldBlocking" method
                // public boolean d(DamageSource damageSource) {
                //   if (this.bn != null && this.bn.getItem() instanceof ItemSword) return false;
                //   [...]
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label elseLabel = newLabel();
                        loadThis();
                        getField(t(getClassName()), "bn", t(ItemStack1_9_R2));
                        ifNull(elseLabel);
                        loadThis();
                        getField(t(getClassName()), "bn", t(ItemStack1_9_R2));
                        invokeVirtual(t(ItemStack1_9_R2), new Method("getItem", t(Item1_9_R2), new Type[0]));
                        instanceOf(t(ItemSword1_9_R2));
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        push(false);
                        returnValue();
                        mark(elseLabel);
                    }
                };
            }
            if (name.equals("isBlocking") && descriptor.equals("()Z")) {
                // public boolean isBlocking() {
                //   [...]
                //   [Item item = this.bn.getItem();]
                //   if (item instanceof ItemSword && item.f(this.bn) == EnumAnimation.BLOCK) {
                //     return true;
                //   }
                //   [...]
                // }
                return new BufferedGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visit(Visit visit) {
                        if (isAStore(0) && isInvokeVirtual(1, ItemStack1_9_R2, "getItem", "()L" + Item1_9_R2 + ";")) {
                            insertSwordBlocking();
                        }
                    }

                    private void insertSwordBlocking() {
                        int itemIndex = visits(VarInsn.class, varInsn -> varInsn.getOpcode() == Opcodes.ASTORE)
                            .findFirst()
                            .get()
                            .getVarIndex();
                        Label elseLabel = newLabel();
                        loadLocal(itemIndex);
                        instanceOf(t(ItemSword1_9_R2));
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        loadLocal(itemIndex);
                        loadThis();
                        getField(t(getClassName()), "bn", t(ItemStack1_9_R2));
                        invokeVirtual(
                            t(Item1_9_R2),
                            new Method("f", t(EnumAnimation1_9_R2), new Type[] { t(ItemStack1_9_R2) })
                        );
                        getStatic(t(EnumAnimation1_9_R2), "BLOCK", t(EnumAnimation1_9_R2));
                        ifCmp(t(EnumAnimation1_9_R2), Opcodes.IFNE, elseLabel);
                        push(true);
                        returnValue();
                        mark(elseLabel);
                    }
                };
            }
            if (name.equals("damageEntity0") && descriptor.equals("(L" + DamageSource1_9_R2 + ";F)Z")) {
                // protected boolean damageEntity0(DamageSource source, float f) {
                //   [...]
                //   [Function<Double, Double> blocking = ...]
                //   boolean isSwordBlocking = this.isSwordBlocking(source);
                //   blocking = CompatBridgeXXX.wrapDamageBlocking(blocking, isSwordBlocking);
                //   [...]
                //   if ([event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) < 0.0] && !isSwordBlocking)
                //   [...]
                // }
                return new BufferedGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    private int pass = 1;
                    private boolean wrapInserted;
                    private int blockingVar = -1;
                    private int isSwordBlockingVar = -1;

                    @Override
                    public void visit(Visit visit) {
                        if (pass == 2 && !wrapInserted && isAStore(0, blockingVar)) {
                            wrapInserted = true;
                            loadLocal(blockingVar);
                            loadThis();
                            loadArg(0);
                            invokeVirtual(t(getClassName()), isSwordBlocking);
                            isSwordBlockingVar = newLocal(BOOLEAN_TYPE);
                            storeLocal(isSwordBlockingVar);
                            loadLocal(isSwordBlockingVar);
                            invokeStatic(
                                t(CompatBridge1_9_R2),
                                new Method(
                                    "wrapDamageBlocking",
                                    t("com/google/common/base/Function"),
                                    new Type[] { t("com/google/common/base/Function"), BOOLEAN_TYPE }
                                )
                            );
                            storeLocal(blockingVar);
                            return;
                        }
                        if (
                            pass == 2 &&
                            is(0, JumpInsn.class, j -> j.getOpcode() == Opcodes.IFGE) &&
                            is(1, Insn.class, j -> j.getOpcode() == Opcodes.DCMPG) &&
                            is(2, Insn.class, j -> j.getOpcode() == Opcodes.DCONST_0) &&
                            isInvokeVirtual(
                                3,
                                "org/bukkit/event/entity/EntityDamageEvent",
                                "getDamage",
                                "(Lorg/bukkit/event/entity/EntityDamageEvent$DamageModifier;)D"
                            ) &&
                            isGetStatic(
                                4,
                                "org/bukkit/event/entity/EntityDamageEvent$DamageModifier",
                                "BLOCKING",
                                "Lorg/bukkit/event/entity/EntityDamageEvent$DamageModifier;"
                            )
                        ) {
                            Label elseLabel = ((JumpInsn) visits().getFirst()).getLabel();
                            loadLocal(isSwordBlockingVar);
                            ifZCmp(Opcodes.IFNE, elseLabel);
                            return;
                        }
                    }

                    @Override
                    public void visitEnd() {
                        if (pass == 1) {
                            pass = 2;
                            blockingVar = visits(LocalVariable.class, v -> v.getName().equals("blocking"))
                                .findFirst()
                                .get()
                                .getIndex();
                            replay();
                        }
                        super.visitEnd();
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            // public boolean isSwordBlocking(DamageSource source) {
            //   return !source.ignoresArmor() && this.isBlocking() && this.bn.getItem() instanceof ItemSword;
            // }
            AZGeneratorAdapter mg = generateMethod(cv, ACC_PUBLIC, isSwordBlocking);
            Label elseLabel = mg.newLabel();
            mg.visitCode();
            mg.loadArg(0);
            mg.invokeVirtual(t(DamageSource1_9_R2), new Method("ignoresArmor", BOOLEAN_TYPE, NO_ARGS));
            mg.ifZCmp(Opcodes.IFNE, elseLabel);
            mg.loadThis();
            mg.invokeVirtual(t(getClassName()), new Method("isBlocking", BOOLEAN_TYPE, NO_ARGS));
            mg.ifZCmp(Opcodes.IFEQ, elseLabel);
            mg.loadThis();
            mg.getField(t(getClassName()), "bn", t(ItemStack1_9_R2));
            mg.invokeVirtual(t(ItemStack1_9_R2), new Method("getItem", t(Item1_9_R2), NO_ARGS));
            mg.instanceOf(t(ItemSword1_9_R2));
            mg.ifZCmp(Opcodes.IFEQ, elseLabel);
            mg.push(true);
            mg.returnValue();
            mg.mark(elseLabel);
            mg.push(false);
            mg.returnValue();
            mg.endMethod();

            super.visitEnd();
        }
    }

    private static final Method getUseDuration = new Method("e", INT_TYPE, new Type[] { t(ItemStack1_9_R2) });
    private static final Method getUseAction = new Method(
        "f",
        t(EnumAnimation1_9_R2),
        new Type[] { t(ItemStack1_9_R2) }
    );
    private static final Method onRightClick = new Method(
        "a",
        t(InteractionResultWrapper1_9_R2),
        new Type[] { t(ItemStack1_9_R2), t(World1_9_R2), t(EntityHuman1_9_R2), t(EnumHand1_9_R2) }
    );
    private static final Method isSwordBlocking = new Method(
        "isSwordBlocking",
        BOOLEAN_TYPE,
        new Type[] { t(DamageSource1_9_R2) }
    );
}
