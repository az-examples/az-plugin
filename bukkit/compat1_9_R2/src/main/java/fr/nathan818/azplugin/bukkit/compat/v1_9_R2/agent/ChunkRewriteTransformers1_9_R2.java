package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.BukkitAgentCompat1_9_R2.COMPAT_BRIDGE1_9_R2;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ChunkRewriteTransformers1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer(
            "net/minecraft/server/v1_9_R2/PacketPlayOutMapChunk",
            ChunkRewriteTransformers1_9_R2::transformPacketChunk
        );
        agent.addTransformer(
            "net/minecraft/server/v1_9_R2/PacketPlayOutBlockChange",
            ChunkRewriteTransformers1_9_R2::transformPacketBlockChange
        );
        agent.addTransformer(
            "net/minecraft/server/v1_9_R2/PacketPlayOutMultiBlockChange",
            ChunkRewriteTransformers1_9_R2::transformPacketBlockChange
        );
    }

    public static byte[] transformPacketChunk(ClassLoader loader, String className, byte[] bytes) {
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
                        if (
                            "b".equals(name) &&
                            "(Lnet/minecraft/server/v1_9_R2/PacketDataSerializer;)V".equals(descriptor)
                        ) {
                            // public void b(PacketDataSerializer buf) {
                            //   [...]
                            //   [buf.d(this.c);]
                            //  +CompatBridgeXXX.writeChunkData(buf, this.c, this.f, this.d);
                            //  -[buf.d(this.d.length);]
                            //  -[buf.writeBytes(this.d);]
                            //   [...]
                            // }
                            return new GeneratorAdapter(
                                api,
                                super.visitMethod(access, name, descriptor, signature, exceptions),
                                access,
                                name,
                                descriptor
                            ) {
                                private boolean cField;
                                private boolean dField;
                                private boolean ignoreNextPop;
                                private Label jumpLabel;

                                @Override
                                public void visitInsn(int opcode) {
                                    if (ignoreNextPop && opcode == Opcodes.POP) {
                                        ignoreNextPop = false;
                                        return;
                                    }
                                    super.visitInsn(opcode);
                                }

                                @Override
                                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                    cField = ("c".equals(name) &&
                                        "net/minecraft/server/v1_9_R2/PacketPlayOutMapChunk".equals(owner) &&
                                        "I".equals(descriptor));
                                    dField = ("d".equals(name) &&
                                        "net/minecraft/server/v1_9_R2/PacketPlayOutMapChunk".equals(owner) &&
                                        "[B".equals(descriptor));
                                    super.visitFieldInsn(opcode, owner, name, descriptor);
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
                                        jumpLabel == null &&
                                        cField &&
                                        opcode == Opcodes.INVOKEVIRTUAL &&
                                        "d".equals(name) &&
                                        "net/minecraft/server/v1_9_R2/PacketDataSerializer".equals(owner)
                                    ) {
                                        pop();
                                        jumpLabel = newLabel();
                                        ignoreNextPop = true;
                                        loadArg(0);
                                        loadArg(0);
                                        getField(
                                            Type.getObjectType(owner),
                                            "nmsPlayer",
                                            Type.getObjectType("net/minecraft/server/v1_9_R2/EntityPlayer")
                                        );
                                        loadThis();
                                        getField(Type.getObjectType(className), "d", Type.getType(byte[].class));
                                        loadThis();
                                        getField(Type.getObjectType(className), "f", Type.BOOLEAN_TYPE);
                                        loadThis();
                                        getField(Type.getObjectType(className), "c", Type.INT_TYPE);
                                        invokeStatic(
                                            Type.getObjectType(COMPAT_BRIDGE1_9_R2),
                                            new Method(
                                                "writeChunkData",
                                                Type.VOID_TYPE,
                                                new Type[] {
                                                    Type.getObjectType(
                                                        "net/minecraft/server/v1_9_R2/PacketDataSerializer"
                                                    ),
                                                    Type.getObjectType("net/minecraft/server/v1_9_R2/EntityPlayer"),
                                                    Type.getType(byte[].class),
                                                    Type.BOOLEAN_TYPE,
                                                    Type.INT_TYPE,
                                                }
                                            )
                                        );
                                        goTo(jumpLabel);
                                        return;
                                    }
                                    if (
                                        jumpLabel != null &&
                                        dField &&
                                        opcode == Opcodes.INVOKEVIRTUAL &&
                                        "writeBytes".equals(name) &&
                                        "net/minecraft/server/v1_9_R2/PacketDataSerializer".equals(owner)
                                    ) {
                                        pop();
                                        ignoreNextPop = true;
                                        mark(jumpLabel);
                                        jumpLabel = null;
                                        return;
                                    }
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }

    private static byte[] transformPacketBlockChange(ClassLoader loader, String s, byte[] bytes) {
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
                        if (
                            "b".equals(name) &&
                            "(Lnet/minecraft/server/v1_9_R2/PacketDataSerializer;)V".equals(descriptor)
                        ) {
                            return new GeneratorAdapter(
                                api,
                                super.visitMethod(access, name, descriptor, signature, exceptions),
                                access,
                                name,
                                descriptor
                            ) {
                                // public void b(PacketDataSerializer buf) {
                                //   [...]
                                //  -[buf.d(Block.REGISTRY_ID.getId(...));]
                                //  +buf.d(CompactBridgeXXX.rewriteBlockState(Block.REGISTRY_ID.getId(...), this.nmsPlayer));
                                //   [...]
                                // }
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
                                        "getId".equals(name) &&
                                        "net/minecraft/server/v1_9_R2/RegistryBlockID".equals(owner)
                                    ) {
                                        loadArg(0);
                                        getField(
                                            Type.getObjectType("net/minecraft/server/v1_9_R2/PacketDataSerializer"),
                                            "nmsPlayer",
                                            Type.getObjectType("net/minecraft/server/v1_9_R2/EntityPlayer")
                                        );
                                        invokeStatic(
                                            Type.getObjectType(COMPAT_BRIDGE1_9_R2),
                                            new Method(
                                                "rewriteBlockState",
                                                Type.INT_TYPE,
                                                new Type[] {
                                                    Type.INT_TYPE,
                                                    Type.getObjectType("net/minecraft/server/v1_9_R2/EntityPlayer"),
                                                }
                                            )
                                        );
                                    }
                                }
                            };
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                },
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        return crw.getBytes();
    }
}
