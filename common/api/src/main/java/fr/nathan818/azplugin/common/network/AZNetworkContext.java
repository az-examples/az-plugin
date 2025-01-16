package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.PLSPProtocol;

public interface AZNetworkContext {
    static AZNetworkContext unknown() {
        return AZNetworkContextEmpty.UNKNOWN;
    }

    static AZNetworkContext effective() {
        return AZNetworkContextEmpty.EFFECTIVE;
    }

    static @NotNull AZNetworkContext of(@Nullable AZClient client) {
        return client == null ? unknown() : client.getNetworkContext();
    }

    @Nullable
    AZClient getViewer();

    default boolean isEffective() {
        return false;
    }

    default int getAZProtocolVersion() {
        return getAZProtocolVersion(PLSPProtocol.PROTOCOL_VERSION);
    }

    default int getAZProtocolVersion(int fallback) {
        AZClient viewer = getViewer();
        return viewer != null ? viewer.getAZProtocolVersion() : fallback;
    }
}
