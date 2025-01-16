package fr.nathan818.azplugin.common.network;

import fr.nathan818.azplugin.common.AZClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
final class AZNetworkContextEmpty implements AZNetworkContext {

    static final AZNetworkContext UNKNOWN = new AZNetworkContextEmpty(false);
    static final AZNetworkContext EFFECTIVE = new AZNetworkContextEmpty(true);

    private final boolean effective;

    @Override
    public @Nullable AZClient getViewer() {
        return null;
    }
}
