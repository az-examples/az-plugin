package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NettyPacketBuffer extends AZPacketBuffer {
    static @NotNull AZPacketBuffer create(@Nullable AZClient client) {
        return new NettyPacketBufferImpl(client);
    }

    static @NotNull AZPacketBuffer create(@Nullable AZClient client, int initialCapacity) {
        return new NettyPacketBufferImpl(client, initialCapacity);
    }

    ByteBuf getNettyBuffer();
}
