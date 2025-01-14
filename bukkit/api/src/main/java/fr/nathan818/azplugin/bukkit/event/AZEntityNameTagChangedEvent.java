package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.appearance.AZNameTag;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZEntityNameTagChangedEvent extends AZEntityEvent {

    private final @NotNull AZNameTag.Slot slot;
    private final @Nullable AZNetworkValue<AZNameTag> oldNameTag;
    private final @Nullable AZNetworkValue<AZNameTag> newNameTag;

    public AZEntityNameTagChangedEvent(
        @NotNull AZEntity azEntity,
        @NonNull AZNameTag.Slot slot,
        @Nullable AZNetworkValue<AZNameTag> oldNameTag,
        @Nullable AZNetworkValue<AZNameTag> newNameTag
    ) {
        super(azEntity);
        this.slot = slot;
        this.oldNameTag = oldNameTag;
        this.newNameTag = newNameTag;
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
