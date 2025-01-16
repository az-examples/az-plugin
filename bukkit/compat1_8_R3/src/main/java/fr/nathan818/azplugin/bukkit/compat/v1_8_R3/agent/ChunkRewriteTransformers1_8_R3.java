package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.BukkitAgentCompat1_8_R3.COMPAT_BRIDGE1_8_R3;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ChunkRewriteTransformers1_8_R3 {

    public static void register(Agent agent) {
        agent.addTransformer("net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk", (loader, className, bytes) ->
            transformPacketChunk(loader, className, bytes, false)
        );
        agent.addTransformer("net/minecraft/server/v1_8_R3/PacketPlayOutMapChunkBulk", (loader, className, bytes) ->
            transformPacketChunk(loader, className, bytes, true)
        );
        agent.addTransformer(
            "net/minecraft/server/v1_8_R3/PacketPlayOutBlockChange",
            ChunkRewriteTransformers1_8_R3::transformPacketBlockChange
        );
        agent.addTransformer(
            "net/minecraft/server/v1_8_R3/PacketPlayOutMultiBlockChange",
            ChunkRewriteTransformers1_8_R3::transformPacketBlockChange
        );
    }

    public static byte[] transformPacketChunk(ClassLoader loader, String className, byte[] bytes, boolean multi) {
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
                            "(Lnet/minecraft/server/v1_8_R3/PacketDataSerializer;)V".equals(descriptor)
                        ) {
                            // public void b(PacketDataSerializer buf) {
                            //   [...]
                            //  -[buf.a|writeBytes(this.c[...].a)]
                            //  +CompatBridgeXXX.writeChunkData(buf, buf.nmsPlayer, this.c[...].a, this.c[...].b, this.d, true|false);
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
                                private boolean caField;
                                private int lastIloadIndex = -1;
                                private boolean ignoreNextPop;
                                private boolean inserted;

                                @Override
                                public void visitInsn(int opcode) {
                                    if (ignoreNextPop && opcode == Opcodes.POP) {
                                        ignoreNextPop = false;
                                        return;
                                    }
                                    super.visitInsn(opcode);
                                }

                                @Override
                                public void visitVarInsn(int opcode, int varIndex) {
                                    if (opcode == Opcodes.ILOAD) {
                                        lastIloadIndex = varIndex;
                                    }
                                    super.visitIntInsn(opcode, varIndex);
                                }

                                @Override
                                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                                    caField = (cField &&
                                        "a".equals(name) &&
                                        "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap".equals(owner) &&
                                        "[B".equals(descriptor));
                                    cField = ("c".equals(name) &&
                                        className.equals(owner) &&
                                        descriptor.contains(
                                            "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap"
                                        ));
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
                                    if (
                                        !inserted &&
                                        caField &&
                                        opcode == Opcodes.INVOKEVIRTUAL &&
                                        (multi ? "writeBytes" : "a").equals(name) &&
                                        "net/minecraft/server/v1_8_R3/PacketDataSerializer".equals(owner)
                                    ) {
                                        inserted = true;
                                        ignoreNextPop = true;
                                        loadArg(0);
                                        getField(
                                            Type.getObjectType(owner),
                                            "nmsPlayer",
                                            Type.getObjectType("net/minecraft/server/v1_8_R3/EntityPlayer")
                                        );
                                        swap();
                                        loadThis();
                                        if (multi) {
                                            getField(
                                                Type.getObjectType(className),
                                                "c",
                                                ASMUtil.arrayType(
                                                    Type.getObjectType(
                                                        "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap"
                                                    )
                                                )
                                            );
                                            loadLocal(lastIloadIndex);
                                            arrayLoad(
                                                Type.getObjectType(
                                                    "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap"
                                                )
                                            );
                                        } else {
                                            getField(
                                                Type.getObjectType(className),
                                                "c",
                                                Type.getObjectType(
                                                    "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap"
                                                )
                                            );
                                        }
                                        getField(
                                            Type.getObjectType(
                                                "net/minecraft/server/v1_8_R3/PacketPlayOutMapChunk$ChunkMap"
                                            ),
                                            "b",
                                            Type.INT_TYPE
                                        );
                                        loadThis();
                                        getField(Type.getObjectType(className), "d", Type.BOOLEAN_TYPE);
                                        push("a".equals(name));
                                        invokeStatic(
                                            Type.getObjectType(COMPAT_BRIDGE1_8_R3),
                                            new Method(
                                                "writeChunkData",
                                                Type.VOID_TYPE,
                                                new Type[] {
                                                    Type.getObjectType(
                                                        "net/minecraft/server/v1_8_R3/PacketDataSerializer"
                                                    ),
                                                    Type.getObjectType("net/minecraft/server/v1_8_R3/EntityPlayer"),
                                                    Type.getType(byte[].class),
                                                    Type.INT_TYPE,
                                                    Type.BOOLEAN_TYPE,
                                                    Type.BOOLEAN_TYPE,
                                                }
                                            )
                                        );
                                        return;
                                    }
                                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
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
                            "(Lnet/minecraft/server/v1_8_R3/PacketDataSerializer;)V".equals(descriptor)
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
                                //  -[buf.b(Block.d.b(...));]
                                //  +buf.b(CompactBridgeXXX.rewriteBlockState(Block.d.b(...), this.nmsPlayer));
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
                                        "b".equals(name) &&
                                        "net/minecraft/server/v1_8_R3/RegistryID".equals(owner)
                                    ) {
                                        loadArg(0);
                                        getField(
                                            Type.getObjectType("net/minecraft/server/v1_8_R3/PacketDataSerializer"),
                                            "nmsPlayer",
                                            Type.getObjectType("net/minecraft/server/v1_8_R3/EntityPlayer")
                                        );
                                        invokeStatic(
                                            Type.getObjectType(COMPAT_BRIDGE1_8_R3),
                                            new Method(
                                                "rewriteBlockState",
                                                Type.INT_TYPE,
                                                new Type[] {
                                                    Type.INT_TYPE,
                                                    Type.getObjectType("net/minecraft/server/v1_8_R3/EntityPlayer"),
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
