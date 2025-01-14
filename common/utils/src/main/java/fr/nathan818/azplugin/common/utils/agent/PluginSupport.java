package fr.nathan818.azplugin.common.utils.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import lombok.Lombok;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PluginSupport {

    private static boolean agentLoaded = false;
    private static Class<?> agentMainClass;

    public static synchronized void markAgentLoaded(Class<?> agentMainClass) {
        if (!PluginSupport.agentLoaded) {
            PluginSupport.agentMainClass = agentMainClass;
            PluginSupport.agentLoaded = true;
            log(Level.INFO, "Successfully injected agent");
        }
    }

    public static synchronized void assertAgentLoaded(Class<?> agentMainClass) {
        if (!PluginSupport.agentLoaded) {
            PluginSupport.agentMainClass = agentMainClass;
            throw new IllegalStateException("Agent not loaded!");
        }
    }

    public static RuntimeException handleFatalError(Throwable exception) {
        String message = "[AZPlugin] Fatal error occurred: " + exception.getMessage();
        message += "\n\n" + getExceptionTrace(exception);
        if (isBadCommandException(exception)) {
            message += "\n\nTry starting your server with the following command:\n\n" + getRecommendedCommand();
        }
        message += "\n\nExiting...";
        System.err.println(message);
        System.exit(1); // Exit now to prevent damage to the server if custom blocks/items are not registered
        throw Lombok.sneakyThrow(exception);
    }

    private static boolean isBadCommandException(Throwable exception) {
        return (
            "Agent not loaded!".equals(exception.getMessage()) ||
            "InaccessibleObjectException".equals(exception.getClass().getSimpleName())
        );
    }

    public static String getRecommendedCommand() {
        List<String> cmd = getJavaCommand();
        if (!agentLoaded) {
            addIfMissing(cmd, 1, "-javaagent:" + getAgentPath());
        }
        if (supportsJavaModules()) {
            addIfMissing(cmd, 1, "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED");
        }
        return String.join(" ", cmd);
    }

    private static List<String> getJavaCommand() {
        RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.addAll(runtime.getInputArguments());
        String javaCommand = System.getProperty("sun.java.command", "");
        if (javaCommand.startsWith(runtime.getClassPath())) {
            cmd.add("-jar");
            cmd.add(javaCommand);
        } else {
            cmd.add("-cp");
            cmd.add(runtime.getClassPath());
            cmd.add(javaCommand);
        }
        return cmd;
    }

    private static String getAgentPath() {
        Class<?> agentClass = PluginSupport.agentMainClass;
        if (agentClass == null) {
            return "path/to/az-plugin-agent.jar";
        }
        URL agentUrl = agentClass.getProtectionDomain().getCodeSource().getLocation();
        if (agentUrl.getProtocol().equals("file")) {
            try {
                Path basePath = Paths.get("").toAbsolutePath();
                Path agentPath = Paths.get(agentUrl.getFile()).toAbsolutePath();
                return basePath.relativize(agentPath).toString();
            } catch (IllegalArgumentException ignored) {}
        }
        return agentUrl.toString();
    }

    private static boolean supportsJavaModules() {
        try {
            Class.forName("java.lang.Module");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static String getExceptionTrace(Throwable exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static void addIfMissing(List<String> list, int index, String str) {
        if (!list.contains(str)) {
            list.add(Math.min(index, list.size()), str);
        }
    }
}
