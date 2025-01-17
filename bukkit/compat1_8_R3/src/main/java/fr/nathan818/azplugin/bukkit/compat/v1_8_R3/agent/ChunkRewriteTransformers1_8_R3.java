package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.ChunkMap1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CompatBridge1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EntityPlayer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketDataSerializer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketPlayOutBlockChange1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketPlayOutMapChunk1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketPlayOutMapChunkBulk1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketPlayOutMultiBlockChange1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.RegistryID1_8_R3;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.BYTE_ARRAY_TYPE;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.arrayType;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.VarInsn;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.Visit;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ChunkRewriteTransformers1_8_R3 {

    public static void register(Agent agent) {
        agent.addTransformer(PacketPlayOutMapChunk1_8_R3, PacketChunkTransformer::new);
        agent.addTransformer(PacketPlayOutMapChunkBulk1_8_R3, PacketChunkBulkTransformer::new);
        agent.addTransformer(PacketPlayOutBlockChange1_8_R3, PacketBlockChangeTransformer::new);
        agent.addTransformer(PacketPlayOutMultiBlockChange1_8_R3, PacketBlockChangeTransformer::new);
    }

    private static class PacketChunkTransformer extends AZClassVisitor {

        public PacketChunkTransformer(int api, ClassVisitor cv) {
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
            if ("b".equals(name) && ("(L" + PacketDataSerializer1_8_R3 + ";)V").equals(descriptor)) {
                // public void b(PacketDataSerializer buf) {
                //   [...]
                //  -[buf.a(this.c.a);]
                //  +CompatBridgeXXX.writeChunkData(buf, buf.nmsPlayer, this.c.a, this.c.b, this.d, true);
                //   [...]
                // }
                return new BufferedGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    private boolean inserted;

                    @Override
                    public void visit(Visit visit) {
                        if (!inserted && isWriteChunkData()) {
                            inserted = true;
                            redirectToCompatWriteChunkData();
                        }
                    }

                    private boolean isWriteChunkData() {
                        return (
                            isInvokeVirtual(0, PacketDataSerializer1_8_R3, "a", "([B)V") &&
                            isGetField(1, ChunkMap1_8_R3, "a", "[B")
                        );
                    }

                    private void redirectToCompatWriteChunkData() {
                        // FIXME: addInfo
                        visits().pop();
                        loadArg(0);
                        swap();
                        loadArg(0);
                        getField(t(PacketDataSerializer1_8_R3), "nmsPlayer", t(EntityPlayer1_8_R3));
                        swap();
                        loadThis();
                        getField(t(PacketPlayOutMapChunk1_8_R3), "c", t(ChunkMap1_8_R3));
                        getField(t(ChunkMap1_8_R3), "b", INT_TYPE);
                        loadThis();
                        getField(t(PacketPlayOutMapChunk1_8_R3), "d", BOOLEAN_TYPE);
                        push(true);
                        invokeStatic(
                            t(CompatBridge1_8_R3),
                            new Method(
                                "writeChunkData",
                                VOID_TYPE,
                                new Type[] {
                                    t(PacketDataSerializer1_8_R3),
                                    t(EntityPlayer1_8_R3),
                                    BYTE_ARRAY_TYPE,
                                    INT_TYPE,
                                    BOOLEAN_TYPE,
                                    BOOLEAN_TYPE,
                                }
                            )
                        );
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static class PacketChunkBulkTransformer extends AZClassVisitor {

        public PacketChunkBulkTransformer(int api, ClassVisitor cv) {
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
            if ("b".equals(name) && ("(L" + PacketDataSerializer1_8_R3 + ";)V").equals(descriptor)) {
                // public void b(PacketDataSerializer buf) {
                //   [...]
                //  -[buf.writeBytes(this.c[i].a);]
                //  +CompatBridgeXXX.writeChunkData(buf, buf.nmsPlayer, this.c[i].a, this.c[i].b, this.d, true);
                //   [...]
                // }
                return new BufferedGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    private boolean inserted;

                    @Override
                    public void visit(Visit visit) {
                        if (!inserted && isWriteChunkData()) {
                            inserted = true;
                            redirectToCompatWriteChunkData();
                        }
                    }

                    private boolean isWriteChunkData() {
                        return (
                            isInvokeVirtual(
                                0,
                                PacketDataSerializer1_8_R3,
                                "writeBytes",
                                "([B)Lio/netty/buffer/ByteBuf;"
                            ) &&
                            isGetField(1, ChunkMap1_8_R3, "a", "[B")
                        );
                    }

                    private void redirectToCompatWriteChunkData() {
                        // FIXME: addInfo
                        int arrayIndex = visits(VarInsn.class, v -> v.getOpcode() == Opcodes.ILOAD)
                            .findFirst()
                            .get()
                            .getVarIndex();
                        visits().pop();
                        loadArg(0);
                        swap();
                        loadArg(0);
                        getField(t(PacketDataSerializer1_8_R3), "nmsPlayer", t(EntityPlayer1_8_R3));
                        swap();
                        loadThis();
                        getField(t(PacketPlayOutMapChunkBulk1_8_R3), "c", arrayType(t(ChunkMap1_8_R3)));
                        loadLocal(arrayIndex);
                        arrayLoad(t(ChunkMap1_8_R3));
                        getField(t(ChunkMap1_8_R3), "b", INT_TYPE);
                        loadThis();
                        getField(t(PacketPlayOutMapChunkBulk1_8_R3), "d", BOOLEAN_TYPE);
                        push(false);
                        invokeStatic(
                            t(CompatBridge1_8_R3),
                            new Method(
                                "writeChunkData",
                                VOID_TYPE,
                                new Type[] {
                                    t(PacketDataSerializer1_8_R3),
                                    t(EntityPlayer1_8_R3),
                                    t(byte[].class),
                                    INT_TYPE,
                                    BOOLEAN_TYPE,
                                    BOOLEAN_TYPE,
                                }
                            )
                        );
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static class PacketBlockChangeTransformer extends AZClassVisitor {

        public PacketBlockChangeTransformer(int api, ClassVisitor cv) {
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
            if ("b".equals(name) && ("(L" + PacketDataSerializer1_8_R3 + ";)V").equals(descriptor)) {
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
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
                        if (opcode == Opcodes.INVOKEVIRTUAL && "b".equals(name) && RegistryID1_8_R3.equals(owner)) {
                            // FIXME: addInfo
                            loadArg(0);
                            getField(t(PacketDataSerializer1_8_R3), "nmsPlayer", t(EntityPlayer1_8_R3));
                            invokeStatic(
                                t(CompatBridge1_8_R3),
                                new Method(
                                    "rewriteBlockState",
                                    INT_TYPE,
                                    new Type[] { INT_TYPE, t(EntityPlayer1_8_R3) }
                                )
                            );
                        }
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
