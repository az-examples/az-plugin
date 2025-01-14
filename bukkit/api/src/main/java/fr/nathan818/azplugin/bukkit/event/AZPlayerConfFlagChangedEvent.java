package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AZPlayerConfFlagChangedEvent extends AZPlayerEvent {

    private final @NotNull String key;
    private final boolean set;

    public AZPlayerConfFlagChangedEvent(@NotNull AZPlayer azPlayer, @NonNull String key, boolean set) {
        super(azPlayer);
        this.key = key;
        this.set = set;
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
