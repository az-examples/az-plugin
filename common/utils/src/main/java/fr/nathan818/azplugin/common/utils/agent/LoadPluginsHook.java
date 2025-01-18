package fr.nathan818.azplugin.common.utils.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.NO_ARGS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.JvmMagic;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.CtClassLoader;
import java.net.URL;
import java.util.function.Predicate;
import java.util.logging.Level;
import lombok.Getter;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

public class LoadPluginsHook {

    private static Agent agent;

    public static void register(Agent agent, Predicate<String> classNameFilter) {
        LoadPluginsHook.agent = agent;
        agent.addTransformer(classNameFilter, clazz -> {
            HookClassTransformer tr = clazz.rewrite(HookClassTransformer::new);
            if (tr.isHooked()) {
                log(Level.INFO, "Successfully hooked into {0}.loadPlugins()", clazz.getClassName());
            }
        });
    }

    @SneakyThrows
    public static void onLoadPlugins() {
        try {
            // Ensure that all transformations are applied
            initClass(AgentSupport.class);
            for (String className : agent.getClassesToPreload()) {
                initClass(className.replace('/', '.'));
            }
            CtClassLoader.disableCache();

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
            throw AgentSupport.handleFatalError(ex);
        }
    }

    private static void initClass(Class<?> clazz) {
        initClass(clazz.getName());
    }

    private static void initClass(String className) {
        try {
            Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException ignored) {
            // Ignore
        } catch (Throwable ex) {
            if (isClassNotFoundException(ex)) {
                if (Agent.DEBUG) {
                    log(Level.INFO, "[DEBUG] initClass failed: {0}", className, ex);
                }
                return;
            }
            throw ex;
        }
    }

    private static boolean isClassNotFoundException(Throwable ex) {
        while (ex != null) {
            if (ex instanceof ClassNotFoundException) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }

    private static class HookClassTransformer extends AZClassVisitor {

        private final String targetMethod;
        private final String targetDescriptor;
        private @Getter boolean hooked;

        public HookClassTransformer(int api, ClassVisitor cv) {
            this(api, cv, "loadPlugins", "()V");
        }

        public HookClassTransformer(int api, ClassVisitor cv, String targetMethod, String targetDescriptor) {
            super(api, cv);
            this.targetMethod = targetMethod;
            this.targetDescriptor = targetDescriptor;
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
            if (targetMethod.equals(name) && targetDescriptor.equals(descriptor)) {
                return new AZGeneratorAdapter(api, cv, access, name, descriptor, signature, exceptions) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        invokeStatic(t(LoadPluginsHook.class), new Method("onLoadPlugins", VOID_TYPE, NO_ARGS));
                        hooked = true;
                    }
                };
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
