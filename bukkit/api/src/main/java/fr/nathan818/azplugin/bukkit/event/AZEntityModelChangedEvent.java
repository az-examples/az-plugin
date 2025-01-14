package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.appearance.AZEntityModel;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZEntityModelChangedEvent extends AZEntityEvent {

    private final @Nullable AZNetworkValue<AZEntityModel> oldModel;
    private final @Nullable AZNetworkValue<AZEntityModel> newModel;

    public AZEntityModelChangedEvent(
        @NotNull AZEntity azEntity,
        @Nullable AZNetworkValue<AZEntityModel> oldModel,
        @Nullable AZNetworkValue<AZEntityModel> newModel
    ) {
        super(azEntity);
        this.oldModel = oldModel;
        this.newModel = newModel;
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
