package fr.nathan818.azplugin.common.utils.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.common.utils.JvmMagic;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.net.URL;
import java.util.function.Predicate;
import java.util.logging.Level;
import lombok.Getter;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class LoadPluginsHook {

    private static Agent agent;

    public static void register(Agent agent, Predicate<String> classNameFilter) {
        LoadPluginsHook.agent = agent;
        agent.addTransformer(classNameFilter, (loader, className, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            HookClassTransformer tr = crw.rewrite((api, cv) -> new HookClassTransformer(api, cv, "loadPlugins", "()V"));
            if (tr.isHooked()) {
                log(Level.INFO, "Successfully hooked into {0}.loadPlugins()", className);
            }
            return crw.getBytes();
        });
    }

    @SneakyThrows
    public static void onLoadPlugins() {
        try {
            // Ensure that all transformations are applied
            initClass(PluginSupport.class);
            for (String className : agent.getClassesToPreload()) {
                initClass(className.replace('/', '.'));
            }

            // Remove the plugin from the system class loader
            // (it must be loaded by the plugin class loader)
            URL argentJar = Agent.class.getProtectionDomain().getCodeSource().getLocation();
            ClassLoader classLoader = Agent.class.getClassLoader();
            do {
                boolean removed = JvmMagic.removeJarFromClassLoader(classLoader, argentJar);
                if (removed) {
                    log(
                        Level.INFO,
                        "Successfully removed agent jar ({0}) from class loader: {1}",
                        argentJar,
                        classLoader
                    );
                }
            } while ((classLoader = classLoader.getParent()) != null);
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex);
        }
    }

    private static void initClass(Class<?> clazz) {
        initClass(clazz.getName());
    }

    private static void initClass(String className) {
        try {
            Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {}
    }

    private static class HookClassTransformer extends ClassVisitor {

        private final String targetMethod;
        private final String targetDescriptor;
        private @Getter boolean hooked;

        public HookClassTransformer(int api, ClassVisitor cv, String targetMethod, String targetDescriptor) {
            super(api, cv);
            this.targetMethod = targetMethod;
            this.targetDescriptor = targetDescriptor;
        }

        @Override
        public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
        ) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (targetMethod.equals(name) && targetDescriptor.equals(descriptor)) {
                return new MethodVisitor(api, mv) {
                    @Override
                    public void visitCode() {
                        hooked = true;
                        super.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            Type.getInternalName(LoadPluginsHook.class),
                            "onLoadPlugins",
                            "()V",
                            false
                        );
                        super.visitCode();
                    }
                };
            }
            return mv;
        }
    }
}
