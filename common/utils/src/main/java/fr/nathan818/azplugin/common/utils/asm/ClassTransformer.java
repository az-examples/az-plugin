package fr.nathan818.azplugin.common.utils.asm;

@FunctionalInterface
public interface ClassTransformer {
    byte[] transform(String className, byte[] bytecode);
}
