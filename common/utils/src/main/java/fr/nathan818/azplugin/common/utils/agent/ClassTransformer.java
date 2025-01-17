package fr.nathan818.azplugin.common.utils.agent;

@FunctionalInterface
public interface ClassTransformer {
    void transform(LoadingClass clazz);
}
