package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.gui.AZVignette;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZPlayerVignetteChangedEvent extends AZPlayerEvent {

    private final @Nullable AZVignette oldVignette;
    private final @Nullable AZVignette newVignette;

    public AZPlayerVignetteChangedEvent(
        AZPlayer azPlayer,
        @Nullable AZVignette oldVignette,
        @Nullable AZVignette newVignette
    ) {
        super(azPlayer);
        this.oldVignette = oldVignette;
        this.newVignette = newVignette;
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
