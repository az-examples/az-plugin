package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.addField;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;
import static org.objectweb.asm.Type.INT_TYPE;

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

public class PlayerWindowIdTransformers {

    public static void registerWindowIdTransformers(Agent agent, Consumer<? super Options.Builder> optionsConsumer) {
        Options.Builder builder = Options.builder();
        optionsConsumer.accept(builder);
        Options opts = builder.build();

        agent.addTransformer(opts.getCompatBridgeClass(), BridgeTransformer::new, opts);
        agent.addTransformer(opts.getNmsEntityPlayerClass(), EntityPlayerTransformer::new, opts);
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
            if ("setNextWindowId".equals(name)) {
                // public void setNextWindowId(EntityPlayer handle, int windowId) {
                //   handle.azWindowIdOverride = windowId;
                // }
                AZGeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                mg.loadArg(0);
                mg.loadArg(1);
                mg.putField(t(opts.getNmsEntityPlayerClass()), "azWindowIdOverride", INT_TYPE);
                mg.returnValue();
                mg.endMethod();
                addInfo(cv, getClassName(), "Defined setNextWindowId method");
                return null;
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    private static class EntityPlayerTransformer extends AZClassVisitor {

        private final Options opts;

        public EntityPlayerTransformer(int api, ClassVisitor cv, Options opts) {
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
            if ("nextContainerCounter".equals(name)) {
                // public int nextContainerCounter() {
                //   if (this.azWindowIdOverride != 0) {
                //     this.containerCounter = this.azWindowIdOverride;
                //     this.azWindowIdOverride = 0;
                //     return this.containerCounter;
                //   }
                //   [...]
                // }
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        Label elseLabel = newLabel();
                        loadThis();
                        getField(t(opts.getNmsEntityPlayerClass()), "azWindowIdOverride", INT_TYPE);
                        ifZCmp(Opcodes.IFEQ, elseLabel);
                        loadThis();
                        loadThis();
                        getField(t(opts.getNmsEntityPlayerClass()), "azWindowIdOverride", INT_TYPE);
                        putField(t(opts.getNmsEntityPlayerClass()), "containerCounter", INT_TYPE);
                        loadThis();
                        push(0);
                        putField(t(opts.getNmsEntityPlayerClass()), "azWindowIdOverride", INT_TYPE);
                        loadThis();
                        getField(t(opts.getNmsEntityPlayerClass()), "containerCounter", INT_TYPE);
                        returnValue();
                        visitLabel(elseLabel);
                        addInfo(cv, getClassName(), "Overridden nextContainerCounter method");
                        super.visitCode();
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            addField(cv, Opcodes.ACC_PUBLIC, "azWindowIdOverride", INT_TYPE);
            super.visitEnd();
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter
    public static final class Options {

        private final @NonNull String compatBridgeClass;
        private final @NonNull String nmsEntityPlayerClass;
    }
}
