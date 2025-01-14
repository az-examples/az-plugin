package fr.nathan818.azplugin.bukkit.plugin.entity;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import java.util.Objects;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
abstract class EntityMeta<APIModel, PLSPModel, PLSPPacket> {

    private final AZNetworkValue<APIModel> defaultValue = AZNetworkValue.fixed(createDefault());
    private AZNetworkValue<APIModel> netValue = defaultValue;

    public final @Nullable AZNetworkValue<APIModel> get() {
        return netValue;
    }

    public final boolean set(@Nullable AZNetworkValue<APIModel> netValue) {
        if (isDefault(netValue)) {
            netValue = defaultValue;
        }
        if (Objects.equals(this.netValue, netValue)) {
            return false;
        }
        this.netValue = netValue;
        return true;
    }

    public final boolean apply(
        @NotNull AZEntity entity,
        @NotNull PLSPPacket packet,
        @Nullable AZNetworkValue<? extends APIModel> netValue,
        @NotNull AZNetworkContext ctx,
        boolean isSelf,
        boolean skipIfDefault
    ) {
        APIModel value;
        if (netValue == null) {
            value = null;
        } else {
            try {
                value = netValue.get(ctx);
            } catch (Throwable ex) {
                log(
                    Level.WARNING,
                    "Exception getting network value for {0} (entity: {1}, viewer: {2})",
                    getClass().getSimpleName(),
                    entity,
                    ctx.getViewer(),
                    ex
                );
                return false;
            }
        }
        if (skipIfDefault && isDefault(value)) {
            return false;
        }
        applyToPacket(packet, toPLSPModel(value, isSelf));
        return true;
    }

    protected @Nullable APIModel createDefault() {
        return null;
    }

    public final boolean isDefault(AZNetworkValue<? extends APIModel> netValue) {
        return (netValue == null || netValue.isFixed()) && isDefault(AZNetworkValue.getFixed(netValue));
    }

    public abstract boolean isDefault(@Nullable APIModel value);

    public abstract @Nullable PLSPModel toPLSPModel(@Nullable APIModel value, boolean isSelf);

    public abstract void applyToPacket(@NotNull PLSPPacket packet, @Nullable PLSPModel value);
}
