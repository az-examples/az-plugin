package fr.nathan818.azplugin.common.utils.asm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class CtClassLoader {

    private static Map<ClassLoader, CtClassLoader> LOADERS_CACHE = new HashMap<>();

    public static synchronized @NotNull CtClassLoader fromCache(@NonNull ClassLoader classLoader) {
        if (LOADERS_CACHE == null) {
            return new CtClassLoader(classLoader);
        }
        return LOADERS_CACHE.computeIfAbsent(classLoader, CtClassLoader::new);
    }

    public static synchronized boolean invalidate(@NotNull ClassLoader classLoader, String className) {
        if (LOADERS_CACHE == null) {
            return false;
        }
        CtClassLoader loader = LOADERS_CACHE.get(classLoader);
        return loader != null && loader.invalidate(className);
    }

    public static synchronized void disableCache() {
        LOADERS_CACHE = null;
    }

    private final @Getter ClassLoader loader;
    private final Map<String, CtClass> cache = new ConcurrentHashMap<>();

    public CtClass load(@NonNull String className) {
        return cache.computeIfAbsent(className, this::load0);
    }

    private CtClass load0(String className) {
        String typeName = className.replace('.', '/');
        if (typeName.equals("java/lang/Object")) {
            return new CtClass(this, "java/lang/Object", ACC_PUBLIC | ACC_SUPER, null, null, Collections.emptyList());
        }
        try {
            byte[] bytes = readClassBytes(loader, typeName + ".class");
            return CtClass.load(this, bytes);
        } catch (Exception ex) {
            throw new TypeNotPresentException(typeName, ex);
        }
    }

    private boolean invalidate(String className) {
        return cache.remove(className) != null;
    }

    private static byte[] readClassBytes(ClassLoader classLoader, String resourcePath) throws IOException {
        try (InputStream in = classLoader.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new FileNotFoundException(resourcePath);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            for (int read; (read = in.read(buffer)) != -1;) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }
}
