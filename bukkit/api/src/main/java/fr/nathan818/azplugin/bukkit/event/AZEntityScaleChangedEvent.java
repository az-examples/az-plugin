package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.appearance.AZEntityScale;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZEntityScaleChangedEvent extends AZEntityEvent {

    private final @Nullable AZNetworkValue<AZEntityScale> oldScale;
    private final @Nullable AZNetworkValue<AZEntityScale> newScale;

    public AZEntityScaleChangedEvent(
        @NotNull AZEntity azEntity,
        @Nullable AZNetworkValue<AZEntityScale> oldScale,
        @Nullable AZNetworkValue<AZEntityScale> newScale
    ) {
        super(azEntity);
        this.oldScale = oldScale;
        this.newScale = newScale;
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
