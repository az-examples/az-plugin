package fr.nathan818.azplugin.common.utils.agent;

@FunctionalInterface
public interface ClassTransformer {
    byte[] transform(ClassLoader loader, String className, byte[] bytecode);
}
