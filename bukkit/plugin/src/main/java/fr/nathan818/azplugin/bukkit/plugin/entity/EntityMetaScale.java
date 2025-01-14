package fr.nathan818.azplugin.bukkit.plugin.entity;

import fr.nathan818.azplugin.common.appearance.AZEntityScale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plprotocol.metadata.PactifyScaleMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;

final class EntityMetaScale extends EntityMeta<AZEntityScale, PactifyScaleMetadata, PLSPPacketAbstractMeta> {

    @Override
    public boolean isDefault(@Nullable AZEntityScale value) {
        return value == null || value.isOne();
    }

    @Override
    public @NotNull PactifyScaleMetadata toPLSPModel(@Nullable AZEntityScale value, boolean isSelf) {
        if (value == null) {
            return PLSPPacketAbstractMeta.DEFAULT_SCALE;
        }
        PactifyScaleMetadata metadata = new PactifyScaleMetadata();
        metadata.setDefined(true);
        metadata.setBboxW(value.getBboxWidth());
        metadata.setBboxH(value.getBboxHeight());
        metadata.setRenderW(value.getRenderWidth());
        metadata.setRenderD(value.getRenderDepth());
        metadata.setRenderH(value.getRenderHeight());
        metadata.setItemW(value.getItemInHandWidth());
        metadata.setItemD(value.getItemInHandDepth());
        metadata.setItemH(value.getItemInHandHeight());
        metadata.setTags(value.getNameTags());
        return metadata;
    }

    @Override
    public void applyToPacket(@NotNull PLSPPacketAbstractMeta packet, @Nullable PactifyScaleMetadata value) {
        packet.setScale(value);
    }
}
