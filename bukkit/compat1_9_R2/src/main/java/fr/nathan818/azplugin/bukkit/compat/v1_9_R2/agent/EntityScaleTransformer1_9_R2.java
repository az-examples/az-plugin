package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

class EntityScaleTransformer1_9_R2 {

    public static void register(Agent agent, String compatBridge) {
        // TODO: Clean & made it generic to use with 1_8_R3
        agent.addTransformer(compatBridge, EntityScaleTransformer1_9_R2::transformBridge);
        agent.addTransformer("net/minecraft/server/v1_9_R2/Entity", EntityScaleTransformer1_9_R2::transformEntity);
        agent.addTransformer(
            n ->
                n.startsWith("net/minecraft/server/v1_9_R2/Entity") && !n.equals("net/minecraft/server/v1_9_R2/Entity"),
            EntityScaleTransformer1_9_R2::remapGetHeadHeight
        );
    }

    private static byte[] transformBridge(ClassLoader loader, String className, byte[] bytes) {
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
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    if ("setBboxScale".equals(name)) {
                        GeneratorAdapter mg = new GeneratorAdapter(mv, access, name, descriptor);
                        // arg0.getHandle().setBboxScale(arg1, arg2);
                        mg.loadArg(0);
                        mg.invokeVirtual(
                            Type.getObjectType("org/bukkit/craftbukkit/v1_9_R2/entity/CraftEntity"),
                            new Method("getHandle", "()Lnet/minecraft/server/v1_9_R2/Entity;")
                        );
                        mg.loadArg(1);
                        mg.loadArg(2);
                        mg.invokeVirtual(
                            Type.getObjectType("net/minecraft/server/v1_9_R2/Entity"),
                            new Method("setBboxScale", "(FF)V")
                        );
                        mg.returnValue();
                        mg.endMethod();
                        return mg;
                    }
                    return mv;
                }
            }
        );
        return crw.getBytes();
    }

    private static byte[] transformEntity(ClassLoader loader, String className, byte[] bytes) {
        ClassRewriter crw = new ClassRewriter(loader, remapGetHeadHeight(loader, className, bytes));
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

                        GeneratorAdapter mg = ASMUtil.generateMethod(
                            cv,
                            Opcodes.ACC_PUBLIC,
                            "setSize",
                            Type.VOID_TYPE,
                            new Type[] { Type.FLOAT_TYPE, Type.FLOAT_TYPE }
                        );
                        // this.unscaledWidth = arg0;
                        mg.loadThis();
                        mg.loadArg(0);
                        mg.putField(Type.getObjectType(className), "unscaledWidth", Type.FLOAT_TYPE);
                        // this.unscaledLength = arg1;
                        mg.loadThis();
                        mg.loadArg(1);
                        mg.putField(Type.getObjectType(className), "unscaledLength", Type.FLOAT_TYPE);
                        // if (!bboxScaled)
                        //   this.setSizeScaled(arg0, arg1);
                        // else
                        //   this.setSizeScaled(arg0 * this.bboxScaleWidth, arg1 * this.bboxScaleLength);
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

                        mg = ASMUtil.generateMethod(
                            cv,
                            Opcodes.ACC_PUBLIC,
                            "setBboxScale",
                            Type.VOID_TYPE,
                            new Type[] { Type.FLOAT_TYPE, Type.FLOAT_TYPE }
                        );
                        // this.bboxScaled = arg0 != 1.0F || arg1 != 1.0F;
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
                        // this.bboxScaleWidth = arg0;
                        mg.loadThis();
                        mg.loadArg(0);
                        mg.putField(Type.getObjectType(className), "bboxScaleWidth", Type.FLOAT_TYPE);
                        // this.bboxScaleLength = arg1;
                        mg.loadThis();
                        mg.loadArg(1);
                        mg.putField(Type.getObjectType(className), "bboxScaleLength", Type.FLOAT_TYPE);
                        // this.setSizeScaled(this.unscaledWidth * arg0, this.unscaledLength * arg1);
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

                        mg = ASMUtil.generateMethod(cv, Opcodes.ACC_PUBLIC, "getHeadHeight", Type.FLOAT_TYPE, NO_ARGS);
                        // return CompatBridge.getHeadHeight(this.getBukkitEntity(), this.getHeadHeightUnscaled());
                        mg.loadThis();
                        mg.invokeVirtual(
                            Type.getObjectType("net/minecraft/server/v1_9_R2/Entity"),
                            new Method("getBukkitEntity", "()Lorg/bukkit/craftbukkit/v1_9_R2/entity/CraftEntity;")
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
                        super.visitEnd();
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }

    private static byte[] remapGetHeadHeight(ClassLoader loader, String className, byte[] bytes) {
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
                            return new MethodVisitor(
                                api,
                                super.visitMethod(access, "getHeadHeightUnscaled", descriptor, signature, exceptions)
                            ) {
                                @Override
                                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                    if ("length".equals(name) && "net/minecraft/server/v1_9_R2/Entity".equals(owner)) {
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
}
