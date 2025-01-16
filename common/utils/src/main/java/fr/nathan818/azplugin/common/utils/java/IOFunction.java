package fr.nathan818.azplugin.common.utils.java;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T, R> {
    R apply(T t) throws IOException;
}
