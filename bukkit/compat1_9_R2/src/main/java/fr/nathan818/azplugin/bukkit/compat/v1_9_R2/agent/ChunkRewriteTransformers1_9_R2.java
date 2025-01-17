package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CompatBridge1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityPlayer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketDataSerializer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketPlayOutBlockChange1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketPlayOutMapChunk1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketPlayOutMultiBlockChange1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.RegistryBlockID1_9_R2;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.BYTE_ARRAY_TYPE;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.BufferedMethodVisitor.Visit;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class ChunkRewriteTransformers1_9_R2 {

    public static void register(Agent agent) {
        agent.addTransformer(PacketPlayOutMapChunk1_9_R2, PacketChunkTransformer::new);
        agent.addTransformer(PacketPlayOutBlockChange1_9_R2, PacketBlockChangeTransformer::new);
        agent.addTransformer(PacketPlayOutMultiBlockChange1_9_R2, PacketBlockChangeTransformer::new);
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
            if ("b".equals(name) && ("(L" + PacketDataSerializer1_9_R2 + ";)V").equals(descriptor)) {
                // public void b(PacketDataSerializer buf) {
                //   [...]
                //   [buf.d(this.c);]
                //  +CompatBridgeXXX.writeChunkData(buf, buf.nmsPlayer, this.d, this.c, this.f);
                //  -[buf.d(this.d.length);]
                //  -[buf.writeBytes(this.d);]
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
                                PacketDataSerializer1_9_R2,
                                "writeBytes",
                                "([B)Lio/netty/buffer/ByteBuf;"
                            ) &&
                            isGetField(1, PacketPlayOutMapChunk1_9_R2, "d", "[B")
                        );
                    }

                    private boolean isWriteSectionsMask() {
                        return (
                            isInvokeVirtual(
                                0,
                                PacketDataSerializer1_9_R2,
                                "d",
                                "(I)Lnet/minecraft/server/v1_9_R2/PacketDataSerializer;"
                            ) &&
                            isGetField(1, PacketPlayOutMapChunk1_9_R2, "c", "I")
                        );
                    }

                    private void redirectToCompatWriteChunkData() {
                        while (!isWriteSectionsMask()) {
                            visits().pop();
                        }
                        pop();
                        loadArg(0);
                        loadArg(0);
                        getField(t(PacketDataSerializer1_9_R2), "nmsPlayer", t(EntityPlayer1_9_R2));
                        loadThis();
                        getField(t(PacketPlayOutMapChunk1_9_R2), "d", BYTE_ARRAY_TYPE);
                        loadThis();
                        getField(t(PacketPlayOutMapChunk1_9_R2), "c", INT_TYPE);
                        loadThis();
                        getField(t(PacketPlayOutMapChunk1_9_R2), "f", BOOLEAN_TYPE);
                        invokeStatic(
                            t(CompatBridge1_9_R2),
                            new Method(
                                "writeChunkData",
                                VOID_TYPE,
                                new Type[] {
                                    t(PacketDataSerializer1_9_R2),
                                    t(EntityPlayer1_9_R2),
                                    BYTE_ARRAY_TYPE,
                                    INT_TYPE,
                                    BOOLEAN_TYPE,
                                }
                            )
                        );
                        loadThis();
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
            if ("b".equals(name) && ("(L" + PacketDataSerializer1_9_R2 + ";)V").equals(descriptor)) {
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
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
                            RegistryBlockID1_9_R2.equals(owner)
                        ) {
                            loadArg(0);
                            getField(t(PacketDataSerializer1_9_R2), "nmsPlayer", t(EntityPlayer1_9_R2));
                            invokeStatic(
                                t(CompatBridge1_9_R2),
                                new Method(
                                    "rewriteBlockState",
                                    INT_TYPE,
                                    new Type[] { INT_TYPE, t(EntityPlayer1_9_R2) }
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
