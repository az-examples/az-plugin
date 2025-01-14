package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZClient;
import org.jetbrains.annotations.Nullable;

public interface AZNetworkContext {
    static AZNetworkContext empty() {
        return AZNetworkContextEmpty.INSTANCE;
    }

    @Nullable
    AZClient getViewer();
}
