package fr.nathan818.azplugin.bukkit.plugin.entity;

import fr.nathan818.azplugin.common.appearance.AZNameTag;
import fr.nathan818.azplugin.common.appearance.AZNameTag.Rarity;
import fr.nathan818.azplugin.common.appearance.AZNameTag.Visibility;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;

@RequiredArgsConstructor
final class EntityMetaNameTag extends EntityMeta<AZNameTag, PactifyTagMetadata, PLSPPacketAbstractMeta> {

    private final @NonNull BiConsumer<@NotNull PLSPPacketAbstractMeta, @Nullable PactifyTagMetadata> fieldSetter;

    @Override
    public boolean isDefault(@Nullable AZNameTag value) {
        return value == null || value.isEmpty();
    }

    @Override
    public @NotNull PactifyTagMetadata toPLSPModel(@Nullable AZNameTag value, boolean isSelf) {
        if (value == null) {
            return PLSPPacketAbstractMeta.DEFAULT_TAG;
        }
        PactifyTagMetadata metadata = new PactifyTagMetadata();
        metadata.setText(value.getText());
        metadata.setRarity(convertNameTagRarity(value.getRarity()));
        metadata.setDistance(convertFloat(value.getViewDistance()));
        metadata.setOpacity(convertFloat(value.getOpacity()));
        metadata.setThroughWallOpacity(convertFloat(value.getThroughWallOpacity()));
        metadata.setScale(convertFloat(value.getScale()));
        metadata.setTeamVisibility(convertNameTagVisibility(value.getTeamVisibility()));
        metadata.setSneakDistance(convertFloat(value.getSneakViewDistance()));
        metadata.setSneakOpacity(convertFloat(value.getSneakOpacity()));
        metadata.setSneakThroughWallOpacity(convertFloat(value.getSneakThroughWallOpacity()));
        metadata.setSneakScale(convertFloat(value.getSneakScale()));
        metadata.setSneakTeamVisibility(convertNameTagVisibility(value.getSneakTeamVisibility()));
        metadata.setPointedOpacity(convertFloat(value.getPointedOpacity()));
        metadata.setPointedScale(convertFloat(value.getPointedScale()));
        metadata.setPointedTeamVisibility(convertNameTagVisibility(value.getPointedTeamVisibility()));
        return metadata;
    }

    @Override
    public void applyToPacket(@NotNull PLSPPacketAbstractMeta packet, @Nullable PactifyTagMetadata value) {
        fieldSetter.accept(packet, value);
    }

    private static float convertFloat(@Nullable Float value) {
        return value != null ? value : -1.0F;
    }

    private static PactifyTagMetadata.Rarity convertNameTagRarity(@Nullable Rarity rarity) {
        if (rarity == null) {
            return PactifyTagMetadata.Rarity.AUTO;
        }
        switch (rarity) {
            case NONE:
                return PactifyTagMetadata.Rarity.NONE;
            case UNCOMMON:
                return PactifyTagMetadata.Rarity.UNCOMMON;
            case RARE:
                return PactifyTagMetadata.Rarity.RARE;
            case EPIC:
                return PactifyTagMetadata.Rarity.EPIC;
            case LEGENDARY:
                return PactifyTagMetadata.Rarity.LEGENDARY;
            case MYTHIC:
                return PactifyTagMetadata.Rarity.MYTHIC;
            default:
                throw new IllegalArgumentException("Unknown rarity: " + rarity);
        }
    }

    private static int convertNameTagVisibility(@Nullable Visibility teamVisibility) {
        if (teamVisibility == null) {
            return 0;
        }
        switch (teamVisibility) {
            case ALWAYS:
                return 1;
            case NEVER:
                return 2;
            case HIDE_FOR_OTHER_TEAMS:
                return 3;
            case HIDE_FOR_OWN_TEAM:
                return 4;
            default:
                throw new IllegalArgumentException("Unknown visibility: " + teamVisibility);
        }
    }
}
