package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.CONSTRUCTOR_NAME;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.addField;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.matchMethod;
import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class PacketRewriteTransformers {

    public static void register(Agent agent, Consumer<? super Options.Builder> optionsConsumer) {
        Options.Builder builder = Options.builder();
        optionsConsumer.accept(builder);
        Options opts = builder.build();

        agent.addTransformer(opts.getNmsPacketDataSerializerClass(), (loader, className, bytes) ->
            transformPacketDataSerializer(loader, className, bytes, opts)
        );
        agent.addTransformer(opts.getNmsPacketEncoderClass(), (loader, className, bytes) ->
            transformPacketHandler(loader, className, bytes, opts)
        );
        agent.addTransformer(opts.getNmsPacketDecoderClass(), (loader, className, bytes) ->
            transformPacketHandler(loader, className, bytes, opts)
        );
        agent.addTransformer(opts.getNmsEntityPlayerClass(), (loader, className, bytes) ->
            transformPlayer(loader, className, bytes, opts)
        );
    }

    private static byte[] transformPacketDataSerializer(
        ClassLoader loader,
        String className,
        byte[] bytes,
        Options opts
    ) {
        ClassRewriter crw = new ClassRewriter(loader, bytes);
        crw.rewrite(
            (api, cw) ->
                new ClassVisitor(api, cw) {
                    @Override
                    public MethodVisitor visitMethod(
                        int access,
                        String name,
                        String descriptor,
                        String signature,
                        String[] exceptions
                    ) {
                        if (CONSTRUCTOR_NAME.equals(name)) {
                            // public PacketDataSerializer(..., EntityPlayer nmsPlayer) {
                            //   super(...);
                            //   this.nmsPlayer = nmsPlayer;
                            // }
                            Type[] argumentTypes = addArgument(
                                Type.getArgumentTypes(descriptor),
                                Type.getObjectType(opts.getNmsEntityPlayerClass())
                            );
                            GeneratorAdapter mg = generateMethod(
                                cv,
                                ACC_PUBLIC,
                                name,
                                Type.getReturnType(descriptor),
                                argumentTypes
                            );
                            mg.loadThis();
                            for (int i = 0; i < argumentTypes.length - 1; ++i) {
                                mg.loadArg(i);
                            }
                            mg.invokeConstructor(Type.getObjectType(className), new Method(name, descriptor));
                            mg.loadThis();
                            mg.loadArg(argumentTypes.length - 1);
                            mg.putField(
                                Type.getObjectType(className),
                                "nmsPlayer",
                                Type.getObjectType(opts.getNmsEntityPlayerClass())
                            );
                            mg.returnValue();
                            mg.endMethod();
                            return super.visitMethod(access, name, descriptor, signature, exceptions);
                        }
                        if (
                            opts.getWriteItemStackMethod().equals(name) &&
                            matchMethodOptionalReturn(
                                descriptor,
                                Type.getObjectType(opts.getNmsPacketDataSerializerClass()),
                                Type.getObjectType(opts.getNmsItemStackClass())
                            )
                        ) {
                            // public PacketDataSerializer XXX(ItemStack itemStack) {
                            //   itemStack = CompatBridgeXXX.rewriteItemStackOut(this.nmsPlayer, itemStack);
                            //   [...]
                            // }
                            return new GeneratorAdapter(
                                api,
                                super.visitMethod(access, name, descriptor, signature, exceptions),
                                access,
                                name,
                                descriptor
                            ) {
                                @Override
                                public void visitCode() {
                                    loadThis();
                                    getField(
                                        Type.getObjectType(className),
                                        "nmsPlayer",
                                        Type.getObjectType(opts.getNmsEntityPlayerClass())
                                    );
                                    loadArg(0);
                                    invokeStatic(
                                        Type.getObjectType(opts.getCompatBridgeClass()),
                                        new Method(
                                            "rewriteItemStackOut",
                                            Type.getMethodDescriptor(
                                                Type.getObjectType(opts.getNmsItemStackClass()),
                                                Type.getObjectType(opts.getNmsEntityPlayerClass()),
                                                Type.getObjectType(opts.getNmsItemStackClass())
                                            )
                                        )
                                    );
                                    storeArg(0);
                                    super.visitCode();
                                }
                            };
                        }
                        if (
                            opts.getReadItemStackMethod().equals(name) &&
                            matchMethod(descriptor, Type.getObjectType(opts.getNmsItemStackClass()))
                        ) {
                            // Add rewriteItemStackIn AFTER the setTag call,
                            // this way it will be called before the CraftBukkit tag filtering
                            //
                            // public ItemStack XXX() {
                            //   [...]
                            //   [itemStack.setTag(...);]
                            //   itemStack = CompatBridgeXXX.rewriteItemStackIn(this.nmsPlayer, itemStack);
                            //   [...]
                            // }
                            return new GeneratorAdapter(
                                api,
                                super.visitMethod(access, name, descriptor, signature, exceptions),
                                access,
                                name,
                                descriptor
                            ) {
                                private int currentLocal;

                                @Override
                                public void visitVarInsn(int opcode, int varIndex) {
                                    if (opcode == Opcodes.ALOAD && varIndex != 0) {
                                        currentLocal = varIndex;
                                    }
                                    super.visitVarInsn(opcode, varIndex);
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
                                        opcode == Opcodes.INVOKEVIRTUAL &&
                                        "setTag".equals(name) &&
                                        opts.getNmsItemStackClass().equals(owner)
                                    ) {
                                        int local = currentLocal;
                                        loadThis();
                                        getField(
                                            Type.getObjectType(className),
                                            "nmsPlayer",
                                            Type.getObjectType(opts.getNmsEntityPlayerClass())
                                        );
                                        loadLocal(local);
                                        invokeStatic(
                                            Type.getObjectType(opts.getCompatBridgeClass()),
                                            new Method(
                                                "rewriteItemStackIn",
                                                Type.getMethodDescriptor(
                                                    Type.getObjectType(opts.getNmsItemStackClass()),
                                                    Type.getObjectType(opts.getNmsEntityPlayerClass()),
                                                    Type.getObjectType(opts.getNmsItemStackClass())
                                                )
                                            )
                                        );
                                        storeLocal(local);
                                    }
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }

                    @Override
                    public void visitEnd() {
                        addField(cv, ACC_PUBLIC, "nmsPlayer", Type.getObjectType(opts.getNmsEntityPlayerClass()));
                        addInfo(cv, className, "Added player context and rewrite calls");
                        super.visitEnd();
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }

    private static byte[] transformPacketHandler(ClassLoader loader, String className, byte[] bytes, Options opts) {
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
                        return new GeneratorAdapter(
                            api,
                            super.visitMethod(access, name, descriptor, signature, exceptions),
                            access,
                            name,
                            descriptor
                        ) {
                            @Override
                            public void visitMethodInsn(
                                int opcode,
                                String owner,
                                String name,
                                String descriptor,
                                boolean isInterface
                            ) {
                                if (
                                    opcode == Opcodes.INVOKESPECIAL &&
                                    opts.getNmsPacketDataSerializerClass().equals(owner) &&
                                    CONSTRUCTOR_NAME.equals(name)
                                ) {
                                    // Add the this.nmsPlayer argument to new PacketDataSerializer(...) calls
                                    loadThis();
                                    getField(
                                        Type.getObjectType(className),
                                        "nmsPlayer",
                                        Type.getObjectType(opts.getNmsEntityPlayerClass())
                                    );
                                    descriptor = Type.getMethodDescriptor(
                                        Type.getReturnType(descriptor),
                                        addArgument(
                                            Type.getArgumentTypes(descriptor),
                                            Type.getObjectType(opts.getNmsEntityPlayerClass())
                                        )
                                    );
                                }
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }
                        };
                    }

                    @Override
                    public void visitEnd() {
                        addField(cv, ACC_PUBLIC, "nmsPlayer", Type.getObjectType(opts.getNmsEntityPlayerClass()));
                        addInfo(cv, className, "Added player context");
                        super.visitEnd();
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }

    private static byte[] transformPlayer(ClassLoader loader, String className, byte[] bytes, Options opts) {
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
                        return new GeneratorAdapter(
                            api,
                            super.visitMethod(access, name, descriptor, signature, exceptions),
                            access,
                            name,
                            descriptor
                        ) {
                            @Override
                            public void visitMethodInsn(
                                int opcode,
                                String owner,
                                String name,
                                String descriptor,
                                boolean isInterface
                            ) {
                                if (
                                    opcode == Opcodes.INVOKESPECIAL &&
                                    opts.getNmsPacketDataSerializerClass().equals(owner) &&
                                    CONSTRUCTOR_NAME.equals(name)
                                ) {
                                    // Add "this" argument to new PacketDataSerializer(...) calls
                                    loadThis();
                                    descriptor = Type.getMethodDescriptor(
                                        Type.getReturnType(descriptor),
                                        addArgument(
                                            Type.getArgumentTypes(descriptor),
                                            Type.getObjectType(opts.getNmsEntityPlayerClass())
                                        )
                                    );
                                }
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }
                        };
                    }

                    @Override
                    public void visitEnd() {
                        addInfo(cv, className, "Added player context to PacketDataSerializer constructors");
                        super.visitEnd();
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassWriter.COMPUTE_MAXS
        );
        return crw.getBytes();
    }

    private static Type[] addArgument(Type[] types, Type type) {
        Type[] newTypes = new Type[types.length + 1];
        System.arraycopy(types, 0, newTypes, 0, types.length);
        newTypes[types.length] = type;
        return newTypes;
    }

    private static boolean matchMethodOptionalReturn(String descriptor, Type returnType, Type... argumentTypes) {
        return (
            matchMethod(descriptor, returnType, argumentTypes) || matchMethod(descriptor, Type.VOID_TYPE, argumentTypes)
        );
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static final class Options {

        private final @NonNull String compatBridgeClass;
        private final @NonNull String nmsPacketDataSerializerClass;
        private final @NonNull String writeItemStackMethod;
        private final @NonNull String readItemStackMethod;
        private final @NonNull String nmsPacketEncoderClass;
        private final @NonNull String nmsPacketDecoderClass;
        private final @NonNull String nmsEntityPlayerClass;
        private final @NonNull String nmsItemStackClass;
    }
}
