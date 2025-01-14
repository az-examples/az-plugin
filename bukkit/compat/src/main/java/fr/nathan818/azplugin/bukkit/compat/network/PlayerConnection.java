package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlayerConnection {
    boolean isInNetworkThread();

    void executeInNetworkThread(@NotNull Runnable task);

    <T> void executeInNetworkThread(@NotNull Callable<? extends T> task, CompletableFuture<? super T> callback);

    void sendPluginMessage(
        @NotNull String channel,
        @NotNull AZPacketBuffer buf,
        @Nullable Consumer<? super @Nullable Throwable> callback
    );

    @NotNull
    AZPacketBuffer createNetworkPacketBuffer(AZPlayer azPlayer);
}
