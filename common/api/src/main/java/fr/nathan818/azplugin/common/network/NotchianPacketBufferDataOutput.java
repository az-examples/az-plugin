package fr.nathan818.azplugin.common.network;

import java.io.DataOutput;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;

@RequiredArgsConstructor
public final class NotchianPacketBufferDataOutput implements DataOutput {

    private final @NonNull NotchianPacketBuffer buf;

    @Override
    public void write(int b) {
        buf.writeByte((byte) b);
    }

    @Override
    public void write(byte[] b) {
        buf.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        buf.writeBytes(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) {
        buf.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) {
        buf.writeByte(v);
    }

    @Override
    public void writeShort(int v) {
        buf.writeShort(v);
    }

    @Override
    public void writeInt(int v) {
        buf.writeInt(v);
    }

    @Override
    public void writeLong(long v) {
        buf.writeLong(v);
    }

    @Override
    public void writeFloat(float v) {
        buf.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) {
        buf.writeDouble(v);
    }

    @Override
    public void writeChar(int v) {
        buf.writeShort(v);
    }

    @Override
    public void writeBytes(@NotNull String s) {
        byte[] b = s.getBytes(StandardCharsets.US_ASCII);
        buf.writeBytes(b);
    }

    @Override
    public void writeChars(@NotNull String s) {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeUTF(@NotNull String s) {
        throw new UnsupportedOperationException();
    }
}
