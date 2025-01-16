package fr.nathan818.azplugin.common.utils.java;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
    void accept(T t) throws IOException;
}
