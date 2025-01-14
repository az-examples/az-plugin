package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZClient;
import java.io.DataInput;
import java.io.DataOutput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.plsp.PLSPPacketBuffer;

public interface AZPacketBuffer extends PLSPPacketBuffer<AZPacketBuffer>, AutoCloseable {
    @Contract("null -> null; !null -> !null")
    static @Nullable DataInput asDataInput(@Nullable NotchianPacketBuffer buf) {
        if (buf == null) {
            return null;
        }
        if (buf instanceof AZPacketBuffer) {
            return ((AZPacketBuffer) buf).asDataInput();
        }
        return new NotchianPacketBufferDataInput(buf);
    }

    @Contract("null -> null; !null -> !null")
    static @Nullable DataOutput asDataOutput(@Nullable NotchianPacketBuffer buf) {
        if (buf == null) {
            return null;
        }
        if (buf instanceof AZPacketBuffer) {
            return ((AZPacketBuffer) buf).asDataOutput();
        }
        return new NotchianPacketBufferDataOutput(buf);
    }

    @Nullable
    AZClient getClient();

    byte@NotNull[] toByteArray();

    @Override
    void close();

    @NotNull
    DataInput asDataInput();

    @NotNull
    DataOutput asDataOutput();
}
