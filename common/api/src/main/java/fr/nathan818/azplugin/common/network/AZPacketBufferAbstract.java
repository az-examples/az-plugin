package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZ;
import fr.nathan818.azplugin.common.AZClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;
import pactify.client.api.plsp.AbstractPLSPPacketBuffer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AZPacketBufferAbstract
    extends AbstractPLSPPacketBuffer<AZPacketBuffer>
    implements AZPacketBuffer {

    protected final @Nullable AZClient client;

    @Override
    public int getPactifyClientVersion() {
        return client != null ? client.getAZProtocolVersion() : getPactifyServerVersion();
    }

    @Override
    public NotchianChatComponent readNotchianChatComponent() {
        return AZ.platform().readNotchianChatComponent(this);
    }

    @Override
    public NotchianItemStack readNotchianItemStack() {
        return AZ.platform().readNotchianItemStack(this);
    }

    @Override
    public NotchianNbtTagCompound readNotchianNbtTagCompound() {
        return AZ.platform().readNotchianNbtTagCompound(this);
    }
}
