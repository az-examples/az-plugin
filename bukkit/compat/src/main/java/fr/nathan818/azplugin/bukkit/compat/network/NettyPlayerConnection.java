package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import io.netty.channel.Channel;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public abstract class NettyPlayerConnection implements PlayerConnection {

    public abstract Channel getNettyChannel();

    @Override
    public boolean isInNetworkThread() {
        return getNettyChannel().eventLoop().inEventLoop();
    }

    @Override
    public void executeInNetworkThread(@NotNull Runnable task) {
        getNettyChannel().eventLoop().execute(task);
    }

    @Override
    public <T> void executeInNetworkThread(@NotNull Callable<? extends T> task, CompletableFuture<? super T> callback) {
        getNettyChannel()
            .eventLoop()
            .submit(task)
            .addListener(f -> {
                if (f.isSuccess()) {
                    //noinspection unchecked
                    callback.complete((T) f.getNow());
                } else {
                    callback.completeExceptionally(f.cause());
                }
            });
    }

    @Override
    public @NotNull AZPacketBuffer createNetworkPacketBuffer(AZPlayer azPlayer) {
        return new NettyPacketBufferImpl(azPlayer, getNettyChannel().alloc().buffer());
    }
}
