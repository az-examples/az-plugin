package fr.nathan818.azplugin.common.utils.asm;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class AgentClassWriter extends ClassWriter {

    public static void addInfo(ClassVisitor cv, String className, String message, Object... args) {
        if (cv instanceof AgentClassWriter) {
            ((AgentClassWriter) cv).addInfo(className, message, args);
        }
    }

    private final @Getter ClassLoader loader;
    private final List<LogEntry> pendingMessages = new LinkedList<>();

    public AgentClassWriter(ClassLoader loader, int flags) {
        super(flags);
        this.loader = loader;
    }

    public AgentClassWriter(ClassLoader loader, ClassReader classReader, int flags) {
        super(classReader, flags);
        this.loader = loader;
    }

    @Override
    protected ClassLoader getClassLoader() {
        return loader;
    }

    private void addInfo(String className, String message, Object... args) {
        className = Objects.toString(className);
        className = className.replace('/', '.');
        className = className.replace("net.minecraft.server", "nms");
        className = className.replace("org.bukkit.craftbukkit", "obc");
        className = className.replaceFirst("fr\\.nathan818\\.azplugin\\.bukkit\\.compat\\.[^.]+\\.agent\\.", "");
        pendingMessages.add(new LogEntry("[Transform] " + className + " - " + message, args));
    }

    public void flushMessages() {
        try {
            for (LogEntry message : pendingMessages) {
                log(Level.INFO, message.message, message.args);
            }
        } finally {
            pendingMessages.clear();
        }
    }

    @RequiredArgsConstructor
    private static class LogEntry {

        private final String message;
        private final Object[] args;
    }
}
