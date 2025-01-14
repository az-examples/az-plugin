package fr.nathan818.azplugin.common.utils;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;

@lombok.Builder(builderClassName = "Builder", builderMethodName = "__builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ClientValue<V> {

    private final @NonNull AZClientAbstract client;

    @lombok.Builder.Default
    private final @NonNull Function<? super V, ? extends V> mapper = Function.identity();

    @lombok.Builder.Default
    private final @NonNull Function<
        ? super V,
        ? extends @Nullable PLSPPacket<PLSPPacketHandler.ClientHandler>
    > packetFactory = v -> null;

    @lombok.Builder.Default
    private final @NonNull BiConsumer<V, V> listener = (oldValue, newValue) -> {};

    private V value;

    public static <V> Builder<V> builder(AZClientAbstract client) {
        return ClientValue.<V>__builder().client(client);
    }

    public V get() {
        synchronized (this) {
            return value;
        }
    }

    public void set(V value) {
        V oldValue;
        synchronized (this) {
            oldValue = this.value;
            if (Objects.equals(oldValue, value)) {
                return;
            }
            this.value = value;
        }
        client.sendPacketIfReady(() -> packetFactory.apply(get()));
        listener.accept(oldValue, value);
    }

    public void sendInitial() {
        V value = get();
        if (value != null) {
            PLSPPacket<PLSPPacketHandler.ClientHandler> packet = packetFactory.apply(value);
            if (packet != null) {
                client.sendPacket(packet);
            }
        }
    }
}
