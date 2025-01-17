package fr.nathan818.azplugin.common.utils.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.common.utils.agent.LoadingClass.ClassVisitorRewriteConstructor;
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
import org.objectweb.asm.ClassVisitor;

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

    public <T extends ClassVisitor> void addTransformer(
        String className,
        ClassVisitorRewriteConstructor<T> transformer
    ) {
        addTransformer(className, clazz -> clazz.rewrite(transformer));
    }

    public <T extends ClassVisitor, A> void addTransformer(
        String className,
        LoadingClass.ClassVisitorRewriteConstructorWithArg<T, A> transformer,
        A arg
    ) {
        addTransformer(className, clazz -> clazz.rewrite(transformer, arg));
    }

    public void addTransformer(Predicate<String> className, ClassTransformer transformer) {
        predicateTransformers.add(new PredicateTransformer(className, transformer));
    }

    public <T extends ClassVisitor> void addTransformer(
        Predicate<String> className,
        ClassVisitorRewriteConstructor<T> transformer
    ) {
        addTransformer(className, clazz -> clazz.rewrite(transformer));
    }

    public <T extends ClassVisitor, A> void addTransformer(
        Predicate<String> className,
        LoadingClass.ClassVisitorRewriteConstructorWithArg<T, A> transformer,
        A arg
    ) {
        addTransformer(className, clazz -> clazz.rewrite(transformer, arg));
    }

    @Override
    public byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain,
        byte[] bytes
    ) {
        if (className == null) {
            return null;
        }
        LoadingClass clazz = new LoadingClass(loader, className, bytes);
        try {
            List<ClassTransformer> transformers = this.transformers.getOrDefault(className, Collections.emptyList());
            for (ClassTransformer transformer : transformers) {
                transformer.transform(clazz);
            }
            for (PredicateTransformer transformer : predicateTransformers) {
                if (!transformer.getClassName().test(className)) {
                    continue;
                }
                transformer.getTransformer().transform(clazz);
            }
        } catch (Throwable ex) {
            throw AgentSupport.handleFatalError(new RuntimeException("Failed to transform class: " + className, ex));
        }
        byte[] transformed = clazz.getBytes();
        if (transformed == bytes) {
            return null;
        }
        if (DEBUG) {
            try {
                Path path = Paths.get(DEBUG_DUMP_DIR, className + ".class");
                Files.createDirectories(path.getParent());
                Files.write(path, transformed);
            } catch (Exception ex) {
                log(Level.WARNING, "Failed to dump transformed class to disk: {0}", className, ex);
            }
        }
        return transformed;
    }

    @RequiredArgsConstructor
    @Getter
    private static final class PredicateTransformer {

        private final Predicate<String> className;
        private final ClassTransformer transformer;
    }
}
