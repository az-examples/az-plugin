package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import fr.nathan818.azplugin.common.network.AZPacketBufferAbstract;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.io.DataInput;
import java.io.DataOutput;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NettyPacketBufferImpl extends AZPacketBufferAbstract implements NettyPacketBuffer {

    private @Getter ByteBuf nettyBuffer;
    private DataInput dataInputCache;
    private DataOutput dataOutputCache;

    protected NettyPacketBufferImpl(@Nullable AZClient client) {
        this(client, Unpooled.buffer());
    }

    protected NettyPacketBufferImpl(@Nullable AZClient client, int initialCapacity) {
        this(client, Unpooled.buffer(initialCapacity));
    }

    protected NettyPacketBufferImpl(@Nullable AZClient client, @NonNull ByteBuf nettyBuffer) {
        super(client);
        this.nettyBuffer = nettyBuffer;
    }

    @Override
    protected final AZPacketBuffer returnValue() {
        return this;
    }

    @Override
    public int readableBytes() {
        return nettyBuffer.readableBytes();
    }

    @Override
    public AZPacketBuffer writeBytes(byte[] src) {
        nettyBuffer.writeBytes(src);
        return this;
    }

    @Override
    public AZPacketBuffer writeBytes(byte[] src, int srcIndex, int length) {
        nettyBuffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public AZPacketBuffer readBytes(byte[] dst) {
        nettyBuffer.readBytes(dst);
        return this;
    }

    @Override
    public AZPacketBuffer readBytes(byte[] dst, int dstIndex, int length) {
        nettyBuffer.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public byte readByte() {
        return nettyBuffer.readByte();
    }

    @Override
    public AZPacketBuffer writeByte(int value) {
        nettyBuffer.writeByte(value);
        return this;
    }

    @Override
    public byte@NotNull[] toByteArray() {
        return ByteBufUtil.getBytes(nettyBuffer, nettyBuffer.readerIndex(), nettyBuffer.readableBytes(), true);
    }

    @Override
    public void close() {
        ByteBuf nettyBuffer = this.nettyBuffer;
        if (nettyBuffer != null) {
            this.nettyBuffer = null;
            this.dataInputCache = null;
            this.dataOutputCache = null;
            nettyBuffer.release();
        }
    }

    @Override
    public @NotNull DataInput asDataInput() {
        DataInput ret = dataInputCache;
        if (ret == null) {
            dataInputCache = ret = new ByteBufInputStream(nettyBuffer);
        }
        return ret;
    }

    @Override
    public @NotNull DataOutput asDataOutput() {
        DataOutput ret = dataOutputCache;
        if (ret == null) {
            dataOutputCache = ret = new ByteBufOutputStream(nettyBuffer);
        }
        return ret;
    }
}
