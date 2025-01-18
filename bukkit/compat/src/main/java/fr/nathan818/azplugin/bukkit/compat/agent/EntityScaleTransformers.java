package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.addField;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.FLOAT_TYPE;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class EntityScaleTransformers {

    public static void registerEntityScaleTransformer(Agent agent, Consumer<? super Options.Builder> optionsConsumer) {
        Options.Builder builder = Options.builder();
        optionsConsumer.accept(builder);
        Options opts = builder.build();

        agent.addTransformer(opts.getCompatBridgeClass(), BridgeTransformer::new, opts);
        agent.addTransformer(opts.getNmsEntityClass(), EntitySuperclassTransformer::new, opts);
        agent.addTransformer(
            n -> n.startsWith(opts.getNmsEntityClass()) && !n.equals(opts.getNmsEntityClass()),
            EntitySubclassTransformer::new,
            opts
        );
    }

    private static class BridgeTransformer extends AZClassVisitor {

        private final Options opts;

        public BridgeTransformer(int api, ClassVisitor cv, Options opts) {
            super(api, cv);
            this.opts = opts;
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            if ("setBboxScale".equals(name)) {
                // public void setBboxScale(org.bukkit.entity.Entity arg0, float arg1, float arg2) {
                //   arg0.getHandle().setBboxScale(arg1, arg2);
                // }
                AZGeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                mg.loadArg(0);
                mg.invokeVirtual(
                    t(opts.getCraftEntityClass()),
                    new Method("getHandle", t(opts.getNmsEntityClass()), NO_ARGS)
                );
                mg.loadArg(1);
                mg.loadArg(2);
                mg.invokeVirtual(t(opts.getNmsEntityClass()), new Method("setBboxScale", "(FF)V"));
                mg.returnValue();
                mg.endMethod();
                addInfo(cv, getClassName(), "Defined setBboxScale method");
                return null;
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static class EntitySuperclassTransformer extends EntitySubclassTransformer {

        public EntitySuperclassTransformer(int api, ClassVisitor cv, Options opts) {
            super(api, cv, opts);
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            if ("setSize".equals(name) && "(FF)V".equals(descriptor)) {
                // Rename setSize to setSizeInternal
                return super.visitMethod(access, "setSizeInternal", descriptor, signature, exceptions);
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            addField(cv, Opcodes.ACC_PRIVATE, "bboxScaled", "Z");
            addField(cv, Opcodes.ACC_PRIVATE, "bboxScaleWidth", "F");
            addField(cv, Opcodes.ACC_PRIVATE, "bboxScaleLength", "F");
            addField(cv, Opcodes.ACC_PRIVATE, "unscaledWidth", "F");
            addField(cv, Opcodes.ACC_PRIVATE, "unscaledLength", "F");

            // public float getUnscaledLength() {
            //   if (this.bboxScaled) {
            //     return this.unscaledLength;
            //   } else {
            //     return this.length;
            //   }
            // }
            AZGeneratorAdapter mg = generateMethod(cv, Opcodes.ACC_PUBLIC, "getUnscaledLength", FLOAT_TYPE, NO_ARGS);
            mg.loadThis();
            mg.getField(t(getClassName()), "bboxScaled", BOOLEAN_TYPE);
            Label elseLabel = mg.newLabel();
            mg.ifZCmp(Opcodes.IFEQ, elseLabel);
            mg.loadThis();
            mg.getField(t(getClassName()), "unscaledLength", FLOAT_TYPE);
            mg.returnValue();
            mg.mark(elseLabel);
            mg.loadThis();
            mg.getField(t(getClassName()), "length", FLOAT_TYPE);
            mg.returnValue();
            mg.endMethod();

            // public float getUnscaledWidth() {
            //   if (this.bboxScaled) {
            //     return this.unscaledWidth;
            //   } else {
            //     return this.width;
            //   }
            // }
            mg = generateMethod(cv, Opcodes.ACC_PUBLIC, "getUnscaledWidth", FLOAT_TYPE, NO_ARGS);
            mg.loadThis();
            mg.getField(t(getClassName()), "bboxScaled", BOOLEAN_TYPE);
            elseLabel = mg.newLabel();
            mg.ifZCmp(Opcodes.IFEQ, elseLabel);
            mg.loadThis();
            mg.getField(t(getClassName()), "unscaledWidth", FLOAT_TYPE);
            mg.returnValue();
            mg.mark(elseLabel);
            mg.loadThis();
            mg.getField(t(getClassName()), "width", FLOAT_TYPE);
            mg.returnValue();
            mg.endMethod();

            // public void setSize(float arg0, float arg1) {
            //   this.unscaledWidth = arg0;
            //   this.unscaledLength = arg1;
            //   if (!bboxScaled) {
            //     this.setSizeInternal(arg0, arg1);
            //   } else {
            //     this.setSizeInternal(arg0 * this.bboxScaleWidth, arg1 * this.bboxScaleLength);
            //   }
            // }
            mg = generateMethod(cv, Opcodes.ACC_PUBLIC, "setSize", VOID_TYPE, new Type[] { FLOAT_TYPE, FLOAT_TYPE });

            mg.loadThis();
            mg.loadArg(0);
            mg.putField(t(getClassName()), "unscaledWidth", FLOAT_TYPE);

            mg.loadThis();
            mg.loadArg(1);
            mg.putField(t(getClassName()), "unscaledLength", FLOAT_TYPE);

            elseLabel = mg.newLabel();
            mg.loadThis();
            mg.getField(t(getClassName()), "bboxScaled", BOOLEAN_TYPE);
            mg.ifZCmp(Opcodes.IFNE, elseLabel);
            mg.loadThis();
            mg.loadArg(0);
            mg.loadArg(1);
            mg.invokeVirtual(t(getClassName()), new Method("setSizeInternal", "(FF)V"));
            mg.returnValue();
            mg.mark(elseLabel);
            mg.loadThis();
            mg.loadArg(0);
            mg.loadThis();
            mg.getField(t(getClassName()), "bboxScaleWidth", FLOAT_TYPE);
            mg.visitInsn(Opcodes.FMUL);
            mg.loadArg(1);
            mg.loadThis();
            mg.getField(t(getClassName()), "bboxScaleLength", FLOAT_TYPE);
            mg.visitInsn(Opcodes.FMUL);
            mg.invokeVirtual(t(getClassName()), new Method("setSizeInternal", "(FF)V"));

            mg.returnValue();
            mg.endMethod();

            // public void setBboxScale(float arg0, float arg1) {
            //   float unscaledWidth = this.getUnscaledWidth();
            //   float unscaledLength = this.getUnscaledLength();
            //   this.bboxScaled = arg0 != 1.0F || arg1 != 1.0F;
            //   this.bboxScaleWidth = arg0;
            //   this.bboxScaleLength = arg1;
            //   this.setSizeInternal(unscaledWidth * arg0, unscaledLength * arg1);
            // }
            mg = generateMethod(
                cv,
                Opcodes.ACC_PUBLIC,
                "setBboxScale",
                VOID_TYPE,
                new Type[] { FLOAT_TYPE, FLOAT_TYPE }
            );

            int unscaledWidth = mg.newLocal(FLOAT_TYPE);
            mg.loadThis();
            mg.invokeVirtual(t(getClassName()), new Method("getUnscaledWidth", "()F"));
            mg.storeLocal(unscaledWidth);

            int unscaledLength = mg.newLocal(FLOAT_TYPE);
            mg.loadThis();
            mg.invokeVirtual(t(getClassName()), new Method("getUnscaledLength", "()F"));
            mg.storeLocal(unscaledLength);

            mg.loadThis();
            Label elseLabel2 = mg.newLabel();
            mg.loadArg(0);
            mg.push(1.0F);
            mg.ifCmp(FLOAT_TYPE, Opcodes.IFNE, elseLabel2);
            mg.loadArg(1);
            mg.push(1.0F);
            mg.ifCmp(FLOAT_TYPE, Opcodes.IFNE, elseLabel2);
            mg.push(false);
            Label endLabel = mg.newLabel();
            mg.goTo(endLabel);
            mg.mark(elseLabel2);
            mg.push(true);
            mg.mark(endLabel);
            mg.putField(t(getClassName()), "bboxScaled", BOOLEAN_TYPE);

            mg.loadThis();
            mg.loadArg(0);
            mg.putField(t(getClassName()), "bboxScaleWidth", FLOAT_TYPE);

            mg.loadThis();
            mg.loadArg(1);
            mg.putField(t(getClassName()), "bboxScaleLength", FLOAT_TYPE);

            mg.loadThis();
            mg.loadLocal(unscaledWidth);
            mg.loadArg(0);
            mg.visitInsn(Opcodes.FMUL);
            mg.loadLocal(unscaledLength);
            mg.loadArg(1);
            mg.visitInsn(Opcodes.FMUL);
            mg.invokeVirtual(t(getClassName()), new Method("setSizeInternal", "(FF)V"));

            mg.returnValue();
            mg.endMethod();

            // public float getHeadHeight() {
            //   return CompatBridge.getHeadHeight(this.getBukkitEntity(), this.getUnscaledHeadHeight());
            // }
            mg = generateMethod(cv, Opcodes.ACC_PUBLIC, "getHeadHeight", FLOAT_TYPE, NO_ARGS);
            mg.loadThis();
            mg.invokeVirtual(
                t(opts.getNmsEntityClass()),
                new Method("getBukkitEntity", t(opts.getCraftEntityClass()), NO_ARGS)
            );
            mg.loadThis();
            mg.invokeVirtual(t(getClassName()), new Method("getUnscaledHeadHeight", "()F"));
            mg.invokeStatic(
                t("fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge"),
                new Method("getHeadHeight", FLOAT_TYPE, new Type[] { t("org/bukkit/entity/Entity"), FLOAT_TYPE })
            );
            mg.returnValue();
            mg.endMethod();

            addInfo(cv, getClassName(), "Added custom-scale logic");
            super.visitEnd();
        }
    }

    private static class EntitySubclassTransformer extends AZClassVisitor {

        protected final Options opts;

        public EntitySubclassTransformer(int api, ClassVisitor cv, Options opts) {
            super(api, cv);
            this.opts = opts;
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
            if ("getHeadHeight".equals(name) && "()F".equals(descriptor)) {
                // Rename getHeadHeight to getUnscaledHeadHeight
                addInfo(cv, getClassName(), "Remapped getHeadHeight to getUnscaledHeadHeight");
                return new AZGeneratorAdapter(
                    api,
                    cv,
                    access,
                    "getUnscaledHeadHeight",
                    descriptor,
                    signature,
                    exceptions
                ) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        if ("length".equals(name) && opts.getNmsEntityClass().equals(owner)) {
                            // Redirect length field access to getUnscaledLength()
                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, "getUnscaledLength", "()F", false);
                            return;
                        }
                        super.visitFieldInsn(opcode, owner, name, descriptor);
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static final class Options {

        private final @NonNull String compatBridgeClass;
        private final @NonNull String nmsEntityClass;
        private final @NonNull String craftEntityClass;
    }
}
