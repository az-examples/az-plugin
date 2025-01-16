package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.compat.network.NettyPlayerConnection;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class PlayerConnection1_8_R3 extends NettyPlayerConnection {

    private final NetworkManager networkManager;

    @Override
    public Channel getNettyChannel() {
        return networkManager.channel;
    }

    @Override
    public void sendPluginMessage(
        @NotNull String channel,
        @NotNull AZPacketBuffer buf,
        @Nullable Consumer<? super @Nullable Throwable> callback
    ) {
        ChannelFuture channelFuture;
        PacketDataSerializer nmsBuf = BukkitCompat1_8_R3.toNMSPacketBuffer(buf);
        nmsBuf.retain();
        try {
            channelFuture = networkManager.channel.writeAndFlush(new PacketPlayOutCustomPayload(channel, nmsBuf));
            channelFuture.addListener(future -> nmsBuf.release());
        } catch (Throwable ex) {
            nmsBuf.release();
            throw ex;
        }

        if (callback != null) {
            channelFuture.addListener(future -> callback.accept(future.cause()));
        }
        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
