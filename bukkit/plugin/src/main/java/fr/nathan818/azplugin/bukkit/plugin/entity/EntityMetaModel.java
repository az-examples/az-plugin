package fr.nathan818.azplugin.bukkit.plugin.entity;

import fr.nathan818.azplugin.common.appearance.AZEntityModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plprotocol.metadata.PactifyModelMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;

final class EntityMetaModel extends EntityMeta<AZEntityModel, PactifyModelMetadata, PLSPPacketAbstractMeta> {

    @Override
    public boolean isDefault(@Nullable AZEntityModel value) {
        return value == null || value.getModelId() == 0;
    }

    @Override
    public @NotNull PactifyModelMetadata toPLSPModel(@Nullable AZEntityModel value, boolean isSelf) {
        if (value == null) {
            return PLSPPacketAbstractMeta.DEFAULT_MODEL;
        }
        PactifyModelMetadata metadata = new PactifyModelMetadata();
        metadata.setId(value.getModelId());
        metadata.setOffsetX(value.getOffsetX());
        metadata.setOffsetY(value.getOffsetY());
        metadata.setOffsetZ(value.getOffsetZ());
        metadata.setMetadata(value.getMetadata());
        metadata.setEyeHeightStand(value.getEyeHeightStand());
        metadata.setEyeHeightSneak(value.getEyeHeightSneak());
        metadata.setEyeHeightSleep(value.getEyeHeightSleep());
        metadata.setEyeHeightElytra(value.getEyeHeightElytra());
        return metadata;
    }

    @Override
    public void applyToPacket(@NotNull PLSPPacketAbstractMeta packet, @Nullable PactifyModelMetadata value) {
        packet.setModel(value);
    }
}
