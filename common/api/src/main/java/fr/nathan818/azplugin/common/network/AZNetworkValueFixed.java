package fr.nathan818.azplugin.common.network;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
final class AZNetworkValueFixed<T> implements AZNetworkValue<T> {

    private final @Nullable T value;

    @Override
    public @Nullable T get(@NotNull AZNetworkContext ctx) {
        return value;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AZNetworkValue)) {
            return false;
        }
        AZNetworkValue<?> that = (AZNetworkValue<?>) o;
        return that.isFixed() && Objects.equals(value, that.get(AZNetworkContext.empty()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
