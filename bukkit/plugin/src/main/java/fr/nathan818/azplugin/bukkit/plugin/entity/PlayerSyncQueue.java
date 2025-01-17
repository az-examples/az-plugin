package fr.nathan818.azplugin.bukkit.plugin.entity;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PlayerSyncQueue {

    private final AZPlayerImpl player;
    private final Map<String, Object> knownValues = new HashMap<>();
    private final Set<String> scheduledKeys = ConcurrentHashMap.newKeySet();

    public <T> void onChange(
        String key,
        Function<? super String, ? extends T> getDefault,
        Function<? super String, ? extends T> getCurrent,
        Callback<T> callback
    ) {
        if (AZBukkit.platform().isSync(player)) {
            onChangeSync(key, getDefault, getCurrent, callback);
        } else if (scheduledKeys.add(key)) {
            AZBukkit.platform()
                .scheduleSync(player, () -> {
                    scheduledKeys.remove(key);
                    onChangeSync(key, getDefault, getCurrent, callback);
                });
        }
    }

    private <T> void onChangeSync(
        String key,
        Function<? super String, ? extends T> getDefault,
        Function<? super String, ? extends T> getCurrent,
        Callback<T> callback
    ) {
        if (!player.isValid()) {
            return;
        }
        @SuppressWarnings("unchecked")
        T oldValue = (T) knownValues.computeIfAbsent(key, getDefault);
        T newValue = getCurrent.apply(key);
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        knownValues.put(key, newValue);
        callback.call(key, oldValue, newValue);
    }

    public interface Callback<T> {
        void call(String key, T prevValue, T newValue);
    }
}
