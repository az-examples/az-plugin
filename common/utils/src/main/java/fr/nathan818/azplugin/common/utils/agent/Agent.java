package fr.nathan818.azplugin.common.utils.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class Agent implements ClassFileTransformer {

    public static final boolean DEBUG = Boolean.getBoolean("fr.nathan818.azplugin.debugAgent");
    private static final String DEBUG_DUMP_DIR = System.getProperty(
        "fr.nathan818.azplugin.debugAgentDumpDir",
        "az-plugin-debug"
    );

    static {
        if (DEBUG) {
            log(Level.INFO, "Agent debug mode enabled, transformed classes will be dumped to: {0}", DEBUG_DUMP_DIR);
        }
    }

    private final Set<String> classesToPreload = Collections.newSetFromMap(new LinkedHashMap<>());
    private final Map<String, List<ClassTransformer>> transformers = new LinkedHashMap<>();
    private final List<PredicateTransformer> predicateTransformers = new ArrayList<>();

    public Collection<String> getClassesToPreload() {
        return classesToPreload;
    }

    public void addClassToPreload(String className) {
        classesToPreload.add(className);
    }

    public void addTransformer(String className, ClassTransformer transformer) {
        addClassToPreload(className);
        transformers.computeIfAbsent(className, ignored -> new LinkedList<>()).add(transformer);
    }

    public void addTransformer(Predicate<String> className, ClassTransformer transformer) {
        predicateTransformers.add(new PredicateTransformer(className, transformer));
    }

    @Override
    public byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain,
        byte[] classfileBuffer
    ) {
        if (className == null) {
            return null;
        }
        byte[] ret = classfileBuffer;
        try {
            List<ClassTransformer> transformers = this.transformers.getOrDefault(className, Collections.emptyList());
            for (ClassTransformer transformer : transformers) {
                byte[] transformed = transformer.transform(loader, className, ret);
                if (transformed != null) {
                    ret = transformed;
                }
            }
            for (PredicateTransformer transformer : predicateTransformers) {
                if (!transformer.getClassName().test(className)) {
                    continue;
                }
                byte[] transformed = transformer.getTransformer().transform(loader, className, ret);
                if (transformed != null) {
                    ret = transformed;
                }
            }
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(new RuntimeException("Failed to transform class: " + className, ex));
        }
        if (ret == classfileBuffer) {
            return null;
        }
        if (DEBUG) {
            try {
                Path path = Paths.get(DEBUG_DUMP_DIR, className + ".class");
                Files.createDirectories(path.getParent());
                Files.write(path, ret);
            } catch (Exception ex) {
                log(Level.WARNING, "Failed to dump transformed class to disk: {0}", className, ex);
            }
        }
        return ret;
    }

    @RequiredArgsConstructor
    @Getter
    private static final class PredicateTransformer {

        private final Predicate<String> className;
        private final ClassTransformer transformer;
    }
}
