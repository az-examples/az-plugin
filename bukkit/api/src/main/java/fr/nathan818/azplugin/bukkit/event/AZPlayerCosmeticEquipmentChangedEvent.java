package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZPlayerCosmeticEquipmentChangedEvent extends AZPlayerEvent {

    private final @NotNull AZCosmeticEquipment.Slot slot;
    private final @Nullable AZNetworkValue<AZCosmeticEquipment> oldEquipment;
    private final @Nullable AZNetworkValue<AZCosmeticEquipment> newEquipment;

    public AZPlayerCosmeticEquipmentChangedEvent(
        @NotNull AZPlayer azPlayer,
        @NonNull AZCosmeticEquipment.Slot slot,
        @Nullable AZNetworkValue<AZCosmeticEquipment> oldEquipment,
        @Nullable AZNetworkValue<AZCosmeticEquipment> newEquipment
    ) {
        super(azPlayer);
        this.slot = slot;
        this.oldEquipment = oldEquipment;
        this.newEquipment = newEquipment;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
