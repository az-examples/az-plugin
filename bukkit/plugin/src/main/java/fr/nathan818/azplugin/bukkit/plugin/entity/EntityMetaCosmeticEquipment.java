package fr.nathan818.azplugin.bukkit.plugin.entity;

import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipmentSlot;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipmentSymbol;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractCosmeticEquipment;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityCosmeticEquipment;
import pactify.client.api.plsp.packet.client.PLSPPacketPlayerCosmeticEquipment;

@RequiredArgsConstructor
final class EntityMetaCosmeticEquipment
    extends EntityMeta<
        AZCosmeticEquipment,
        pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment,
        PLSPPacketAbstractCosmeticEquipment
    > {

    private final PactifyCosmeticEquipmentSlot slot;

    public EntityMetaCosmeticEquipment(AZCosmeticEquipment.Slot slot) {
        this.slot = convertSlot(slot);
    }

    @Override
    public boolean isDefault(@Nullable AZCosmeticEquipment value) {
        return value == null;
    }

    @Override
    public @Nullable PactifyCosmeticEquipment toPLSPModel(@Nullable AZCosmeticEquipment value, boolean isSelf) {
        if (value == null) {
            return null;
        }
        PactifyCosmeticEquipment equipment = new PactifyCosmeticEquipment();
        equipment.setItem(value.getItem());
        equipment.setMatchPatterns(value.getMatchPattern().getPatterns());
        if (isSelf) {
            equipment.setHideInInventory(value.isHideInInventory());
            equipment.setTooltipPrefix(value.getTooltipPrefix());
            equipment.setTooltipSuffix(value.getTooltipSuffix());
            equipment.setSymbol(convertSymbol(value.getSymbol()));
        }
        return equipment;
    }

    @Override
    public void applyToPacket(
        @NotNull PLSPPacketAbstractCosmeticEquipment packet,
        @Nullable PactifyCosmeticEquipment value
    ) {
        if (packet instanceof PLSPPacketPlayerCosmeticEquipment) {
            PLSPPacketPlayerCosmeticEquipment playerPacket = (PLSPPacketPlayerCosmeticEquipment) packet;
            playerPacket.setSlot(slot);
            playerPacket.setEquipment(value);
        } else {
            PLSPPacketEntityCosmeticEquipment entityPacket = (PLSPPacketEntityCosmeticEquipment) packet;
            entityPacket.setSlot(slot);
            entityPacket.setEquipment(value);
        }
    }

    private static PactifyCosmeticEquipmentSlot convertSlot(@Nullable AZCosmeticEquipment.Slot slot) {
        return slot == null ? null : PactifyCosmeticEquipmentSlot.byIndex(slot.getIndex());
    }

    private static PactifyCosmeticEquipmentSymbol convertSymbol(@Nullable AZCosmeticEquipment.Symbol symbol) {
        return symbol == null ? null : PactifyCosmeticEquipmentSymbol.byId(symbol.getId());
    }
}
