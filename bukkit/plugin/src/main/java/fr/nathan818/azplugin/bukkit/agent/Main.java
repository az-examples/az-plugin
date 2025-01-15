package fr.nathan818.azplugin.bukkit.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.common.utils.JvmMagic;
import fr.nathan818.azplugin.common.utils.agent.PluginSupport;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Main implements ClassFileTransformer {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            if (!PluginSupport.markAgentInjected(Main.class)) {
                return;
            }

            // Early check for accessibility issues
            try {
                Class.forName("jdk.internal.loader.URLClassPath").getDeclaredFields()[0].setAccessible(true);
                java.lang.String.class.getDeclaredFields()[0].setAccessible(true);
                java.net.URL.class.getDeclaredFields()[0].setAccessible(true);
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                // Java 8
            }

            // Register the CraftBukkit Main class detector
            inst.addTransformer(new Main(agentArgs, inst));
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex);
        }
    }

    private final String agentArgs;
    private final Instrumentation inst;

    @SneakyThrows
    @Override
    public byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain,
        byte[] classfileBuffer
    ) {
        try {
            if ("org/bukkit/craftbukkit/Main".equals(className)) {
                inst.removeTransformer(this);
                log(Level.INFO, "CraftBukkit Main class detected, injecting agent");

                // Add the plugin to the CraftBukkit classloader
                ClassLoader mainLoader = Main.class.getClassLoader();
                if (!mainLoader.equals(loader)) {
                    log(Level.INFO, "Moving agent jar to CraftBukkit classloader");
                    URL jar = Main.class.getProtectionDomain().getCodeSource().getLocation();
                    JvmMagic.removeJarFromClassLoader(mainLoader, jar);
                    JvmMagic.addJarToClassloader(loader, jar);
                }

                // Load the real agent
                Class<?> main2 = Class.forName(Main.class.getName() + "2", false, loader);
                main2.getMethod("premain", String.class, Instrumentation.class).invoke(null, agentArgs, inst);
            }
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex);
        }
        return null;
    }
}
