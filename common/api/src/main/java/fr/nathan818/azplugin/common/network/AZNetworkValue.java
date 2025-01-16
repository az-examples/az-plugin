package fr.nathan818.azplugin.common.network;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface AZNetworkValue<T> {
    @Contract(value = "null -> null; !null -> !null", pure = true)
    static <T> @Nullable AZNetworkValue<T> fixed(@Nullable T value) {
        return (value == null) ? null : new AZNetworkValueFixed<>(value);
    }

    static <T> @Nullable T getFixed(@Nullable AZNetworkValue<T> value) {
        return (value == null || !value.isFixed()) ? null : value.get(AZNetworkContext.unknown());
    }

    @Nullable
    T get(@NotNull AZNetworkContext ctx);

    default boolean isFixed() {
        return false;
    }
}
