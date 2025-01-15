package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class EntityScaleTransformers {

    public static void registerEntityScaleTransformer(Agent agent, Consumer<? super Options.Builder> optionsConsumer) {
        Options.Builder builder = Options.builder();
        optionsConsumer.accept(builder);
        Options opts = builder.build();

        agent.addTransformer(opts.getCompatBridgeClass(), (loader, className, bytes) ->
            transformBridge(loader, className, bytes, opts)
        );
        agent.addTransformer(opts.getNmsEntityClass(), (loader, className, bytes) ->
            transformEntityBase(loader, className, bytes, opts)
        );
        agent.addTransformer(
            n -> n.startsWith(opts.getNmsEntityClass()) && !n.equals(opts.getNmsEntityClass()),
            (loader, className, bytes) -> transformEntitySubclass(loader, className, bytes, opts)
        );
    }

    private static byte[] transformBridge(ClassLoader loader, String className, byte[] bytes, Options opts) {
        ClassRewriter crw = new ClassRewriter(loader, bytes);
        crw.rewrite((api, cv) ->
            new ClassVisitor(api, cv) {
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
                        GeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                        mg.loadArg(0);
                        mg.invokeVirtual(
                            Type.getObjectType(opts.getCraftEntityClass()),
                            new Method("getHandle", Type.getObjectType(opts.getNmsEntityClass()), NO_ARGS)
                        );
                        mg.loadArg(1);
                        mg.loadArg(2);
                        mg.invokeVirtual(
                            Type.getObjectType(opts.getNmsEntityClass()),
                            new Method("setBboxScale", "(FF)V")
                        );
                        mg.returnValue();
                        mg.endMethod();
                        addInfo(cv, className, "Defined setBboxScale method");
                        return mg;
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        );
        return crw.getBytes();
    }

    private static byte[] transformEntityBase(ClassLoader loader, String className, byte[] bytes, Options opts) {
        ClassRewriter crw = new ClassRewriter(loader, transformEntitySubclass(loader, className, bytes, opts));
        crw.rewrite(
            (api, cv) ->
                new ClassVisitor(api, cv) {
                    @Override
                    public MethodVisitor visitMethod(
                        int access,
                        String name,
                        String descriptor,
                        String signature,
                        String[] exceptions
                    ) {
                        if ("setSize".equals(name) && "(FF)V".equals(descriptor)) {
                            // Rename setSize to setSizeScaled
                            return super.visitMethod(access, "setSizeScaled", descriptor, signature, exceptions);
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }

                    @Override
                    public void visitEnd() {
                        visitField(Opcodes.ACC_PUBLIC, "bboxScaled", "Z", null, null).visitEnd();
                        visitField(Opcodes.ACC_PUBLIC, "bboxScaleWidth", "F", null, null).visitEnd();
                        visitField(Opcodes.ACC_PUBLIC, "bboxScaleLength", "F", null, null).visitEnd();
                        visitField(Opcodes.ACC_PUBLIC, "unscaledWidth", "F", null, 0.6F).visitEnd();
                        visitField(Opcodes.ACC_PUBLIC, "unscaledLength", "F", null, 1.8F).visitEnd();

                        // public void setSize(float arg0, float arg1) {
                        //   this.unscaledWidth = arg0;
                        //   this.unscaledLength = arg1;
                        //   if (!bboxScaled) {
                        //     this.setSizeScaled(arg0, arg1);
                        //   } else {
                        //     this.setSizeScaled(arg0 * this.bboxScaleWidth, arg1 * this.bboxScaleLength);
                        //   }
                        // }
                        GeneratorAdapter mg = generateMethod(
                            cv,
                            Opcodes.ACC_PUBLIC,
                            "setSize",
                            Type.VOID_TYPE,
                            new Type[] { Type.FLOAT_TYPE, Type.FLOAT_TYPE }
                        );

                        mg.loadThis();
                        mg.loadArg(0);
                        mg.putField(Type.getObjectType(className), "unscaledWidth", Type.FLOAT_TYPE);

                        mg.loadThis();
                        mg.loadArg(1);
                        mg.putField(Type.getObjectType(className), "unscaledLength", Type.FLOAT_TYPE);

                        Label elseLabel = mg.newLabel();
                        mg.loadThis();
                        mg.getField(Type.getObjectType(className), "bboxScaled", Type.BOOLEAN_TYPE);
                        mg.ifZCmp(Opcodes.IFNE, elseLabel);
                        mg.loadThis();
                        mg.loadArg(0);
                        mg.loadArg(1);
                        mg.invokeVirtual(Type.getObjectType(className), new Method("setSizeScaled", "(FF)V"));
                        mg.returnValue();
                        mg.mark(elseLabel);
                        mg.loadThis();
                        mg.loadArg(0);
                        mg.loadThis();
                        mg.getField(Type.getObjectType(className), "bboxScaleWidth", Type.FLOAT_TYPE);
                        mg.visitInsn(Opcodes.FMUL);
                        mg.loadArg(1);
                        mg.loadThis();
                        mg.getField(Type.getObjectType(className), "bboxScaleLength", Type.FLOAT_TYPE);
                        mg.visitInsn(Opcodes.FMUL);
                        mg.invokeVirtual(Type.getObjectType(className), new Method("setSizeScaled", "(FF)V"));

                        mg.returnValue();
                        mg.endMethod();

                        // public void setBboxScale(float arg0, float arg1) {
                        //   this.bboxScaled = arg0 != 1.0F || arg1 != 1.0F;
                        //   this.bboxScaleWidth = arg0;
                        //   this.bboxScaleLength = arg1;
                        //   this.setSizeScaled(this.unscaledWidth * arg0, this.unscaledLength * arg1);
                        // }
                        mg = generateMethod(
                            cv,
                            Opcodes.ACC_PUBLIC,
                            "setBboxScale",
                            Type.VOID_TYPE,
                            new Type[] { Type.FLOAT_TYPE, Type.FLOAT_TYPE }
                        );

                        mg.loadThis();
                        Label elseLabel2 = mg.newLabel();
                        mg.loadArg(0);
                        mg.push(1.0F);
                        mg.ifCmp(Type.FLOAT_TYPE, Opcodes.IFNE, elseLabel2);
                        mg.loadArg(1);
                        mg.push(1.0F);
                        mg.ifCmp(Type.FLOAT_TYPE, Opcodes.IFNE, elseLabel2);
                        mg.push(false);
                        Label endLabel = mg.newLabel();
                        mg.goTo(endLabel);
                        mg.mark(elseLabel2);
                        mg.push(true);
                        mg.mark(endLabel);
                        mg.putField(Type.getObjectType(className), "bboxScaled", Type.BOOLEAN_TYPE);

                        mg.loadThis();
                        mg.loadArg(0);
                        mg.putField(Type.getObjectType(className), "bboxScaleWidth", Type.FLOAT_TYPE);

                        mg.loadThis();
                        mg.loadArg(1);
                        mg.putField(Type.getObjectType(className), "bboxScaleLength", Type.FLOAT_TYPE);

                        mg.loadThis();
                        mg.loadThis();
                        mg.getField(Type.getObjectType(className), "unscaledWidth", Type.FLOAT_TYPE);
                        mg.loadArg(0);
                        mg.visitInsn(Opcodes.FMUL);
                        mg.loadThis();
                        mg.getField(Type.getObjectType(className), "unscaledLength", Type.FLOAT_TYPE);
                        mg.loadArg(1);
                        mg.visitInsn(Opcodes.FMUL);
                        mg.invokeVirtual(Type.getObjectType(className), new Method("setSizeScaled", "(FF)V"));

                        mg.returnValue();
                        mg.endMethod();

                        // public float getHeadHeight() {
                        //   return CompatBridge.getHeadHeight(this.getBukkitEntity(), this.getHeadHeightUnscaled());
                        // }
                        mg = generateMethod(cv, Opcodes.ACC_PUBLIC, "getHeadHeight", Type.FLOAT_TYPE, NO_ARGS);
                        mg.loadThis();
                        mg.invokeVirtual(
                            Type.getObjectType(opts.getNmsEntityClass()),
                            new Method("getBukkitEntity", Type.getObjectType(opts.getCraftEntityClass()), NO_ARGS)
                        );
                        mg.loadThis();
                        mg.invokeVirtual(Type.getObjectType(className), new Method("getHeadHeightUnscaled", "()F"));
                        mg.invokeStatic(
                            Type.getObjectType("fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge"),
                            new Method(
                                "getHeadHeight",
                                Type.FLOAT_TYPE,
                                new Type[] { Type.getObjectType("org/bukkit/entity/Entity"), Type.FLOAT_TYPE }
                            )
                        );
                        mg.returnValue();
                        mg.endMethod();

                        addInfo(cv, className, "Added custom-scale logic");
                        super.visitEnd();
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }

    private static byte[] transformEntitySubclass(ClassLoader loader, String className, byte[] bytes, Options opts) {
        ClassRewriter crw = new ClassRewriter(loader, bytes);
        crw.rewrite(
            (api, cv) ->
                new ClassVisitor(api, cv) {
                    @Override
                    public MethodVisitor visitMethod(
                        int access,
                        String name,
                        String descriptor,
                        String signature,
                        String[] exceptions
                    ) {
                        if ("getHeadHeight".equals(name) && "()F".equals(descriptor)) {
                            // Rename getHeadHeight to getHeadHeightUnscaled
                            addInfo(cv, className, "Remapped getHeadHeight to getHeadHeightUnscaled");
                            return new MethodVisitor(
                                api,
                                super.visitMethod(access, "getHeadHeightUnscaled", descriptor, signature, exceptions)
                            ) {
                                @Override
                                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                    if ("length".equals(name) && opts.getNmsEntityClass().equals(owner)) {
                                        // Redirect length field access to unscaledLength
                                        name = "unscaledLength";
                                    }
                                    super.visitFieldInsn(opcode, owner, name, descriptor);
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS,
            0
        );
        return crw.getBytes();
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static final class Options {

        private final @NonNull String compatBridgeClass;
        private final @NonNull String nmsEntityClass;
        private final @NonNull String craftEntityClass;
    }
}
