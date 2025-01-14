package fr.nathan818.azplugin.common.network;

import java.io.DataInput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;

@RequiredArgsConstructor
public final class NotchianPacketBufferDataInput implements DataInput {

    private final @NonNull NotchianPacketBuffer buf;

    @Override
    public void readFully(byte[] b) {
        buf.readBytes(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        buf.readBytes(b, off, len);
    }

    @Override
    public int skipBytes(int n) {
        int nBytes = Math.min(buf.readableBytes(), n);
        buf.readBytes(new byte[nBytes]);
        return nBytes;
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public int readUnsignedByte() {
        return buf.readByte() & 0xFF;
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readShort() & 0xFFFF;
    }

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public String readLine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String readUTF() {
        throw new UnsupportedOperationException();
    }
}
