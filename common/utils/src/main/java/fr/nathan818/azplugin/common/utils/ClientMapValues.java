package fr.nathan818.azplugin.common.utils;

import static fr.nathan818.azplugin.common.utils.java.CollectionsUtil.mapToList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;

@lombok.Builder(builderClassName = "Builder", builderMethodName = "__builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class ClientMapValues<K, V> {

    private final @NonNull AZClientAbstract client;

    @lombok.Builder.Default
    private final @NonNull HolderGetFunction<? super Map<K, V>, ? super K, ? extends V> getter = Map::get;

    @lombok.Builder.Default
    private final @NonNull HolderSetFunction<? super Map<K, V>, ? super K, ? super V> setter = Map::put;

    @lombok.Builder.Default
    private final @NonNull BiFunction<
        ? super K,
        ? super V,
        ? extends @Nullable PLSPPacket<PLSPPacketHandler.ClientHandler>
    > packetFactory = (k, v) -> null;

    @lombok.Builder.Default
    private final @NonNull HolderListener<K, V> listener = (k, o, n) -> {};

    @lombok.Builder.Default
    private final Map<K, V> values = new HashMap<>();

    public static <K, V> Builder<K, V> builder(AZClientAbstract client) {
        return ClientMapValues.<K, V>__builder().client(client);
    }

    public V get(K key) {
        synchronized (this) {
            return getter.get(values, key);
        }
    }

    public void set(K key, V value) {
        V oldValue;
        synchronized (this) {
            oldValue = getter.get(values, key);
            if (Objects.equals(oldValue, value)) {
                return;
            }
            setter.set(values, key, value);
        }
        client.sendPacketIfReady(() -> packetFactory.apply(key, get(key)));
        listener.onChange(key, oldValue, value);
    }

    public <T> @Nullable List<T> mapInitial(BiFunction<? super K, ? super V, ? extends T> mapper) {
        synchronized (this) {
            if (values.isEmpty()) {
                return null;
            }
            return mapToList(values.entrySet(), e -> mapper.apply(e.getKey(), e.getValue()));
        }
    }

    public interface HolderGetFunction<H, K, V> {
        V get(H holder, K key);
    }

    public interface HolderSetFunction<H, K, V> {
        void set(H holder, K key, V value);
    }

    public interface HolderListener<K, V> {
        void onChange(K key, V oldValue, V newValue);
    }
}
