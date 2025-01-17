package fr.nathan818.azplugin.common.utils.asm;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class AZClassWriter extends ClassWriter {

    public static void addInfo(ClassVisitor cv, String className, String message, Object... args) {
        if (cv instanceof AZClassWriter) {
            ((AZClassWriter) cv).addInfo(className, message, args);
        }
    }

    private final @NotNull @Getter ClassLoader loader;
    private final @NotNull @Getter String className;
    private final List<LogEntry> pendingMessages = new LinkedList<>();

    public AZClassWriter(@NonNull ClassLoader loader, @NonNull String className, int flags) {
        this(loader, className, null, flags);
    }

    public AZClassWriter(
        @NonNull ClassLoader loader,
        @NonNull String className,
        @Nullable ClassReader classReader,
        int flags
    ) {
        super(classReader, flags);
        this.loader = loader;
        this.className = className;
    }

    @Override
    protected ClassLoader getClassLoader() {
        return loader;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        CtClassLoader loader = CtClassLoader.fromCache(getClassLoader());
        CtClass class1 = loader.load(type1.replace('/', '.'));
        CtClass class2 = loader.load(type2.replace('/', '.'));
        if (class1.isAssignableFrom(class2)) {
            return type1;
        }
        if (class2.isAssignableFrom(class1)) {
            return type2;
        }
        if (class1.isInterface() || class2.isInterface()) {
            return "java/lang/Object";
        }
        do {
            CtClass superClass = class1.getSuperclass();
            if (superClass == null) {
                return "java/lang/Object";
            }
            class1 = superClass;
        } while (!class1.isAssignableFrom(class2));
        return class1.getName().replace('.', '/');
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
