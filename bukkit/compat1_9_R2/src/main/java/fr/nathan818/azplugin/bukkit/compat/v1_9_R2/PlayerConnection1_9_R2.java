package fr.nathan818.azplugin.bukkit.compat.v1_9_R2;

import fr.nathan818.azplugin.bukkit.compat.network.NettyPacketBuffer;
import fr.nathan818.azplugin.bukkit.compat.network.NettyPlayerConnection;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_9_R2.NetworkManager;
import net.minecraft.server.v1_9_R2.PacketDataSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutCustomPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class PlayerConnection1_9_R2 extends NettyPlayerConnection {

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
        if (buf instanceof NettyPacketBuffer) {
            ByteBuf nettyBuf = ((NettyPacketBuffer) buf).getNettyBuffer().retain();
            try {
                channelFuture = networkManager.channel.writeAndFlush(
                    new PacketPlayOutCustomPayload(channel, new PacketDataSerializer(nettyBuf))
                );
                channelFuture.addListener(future -> nettyBuf.release());
            } catch (Throwable ex) {
                nettyBuf.release();
                throw ex;
            }
        } else {
            channelFuture = networkManager.channel.writeAndFlush(
                new PacketPlayOutCustomPayload(
                    channel,
                    new PacketDataSerializer(Unpooled.wrappedBuffer(buf.toByteArray()))
                )
            );
        }

        if (callback != null) {
            channelFuture.addListener(future -> callback.accept(future.cause()));
        }
        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
