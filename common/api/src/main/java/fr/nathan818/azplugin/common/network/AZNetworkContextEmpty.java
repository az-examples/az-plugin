package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class AZNetworkContextEmpty implements AZNetworkContext {

    static final AZNetworkContext INSTANCE = new AZNetworkContextEmpty();

    @Override
    public @Nullable AZClient getViewer() {
        return null;
    }
}
