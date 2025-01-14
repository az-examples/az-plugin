package fr.nathan818.azplugin.bukkit.plugin.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;

final class EntityMetaOpacity extends EntityMeta<Float, Float, PLSPPacketAbstractMeta> {

    @Override
    public boolean isDefault(@Nullable Float value) {
        return value == null;
    }

    @Override
    public @NotNull Float toPLSPModel(@Nullable Float value, boolean isSelf) {
        if (value == null || !Float.isFinite(value)) {
            return PLSPPacketAbstractMeta.DEFAULT_OPACITY;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    @Override
    public void applyToPacket(@NotNull PLSPPacketAbstractMeta packet, @Nullable Float value) {
        packet.setOpacity(value);
    }
}
