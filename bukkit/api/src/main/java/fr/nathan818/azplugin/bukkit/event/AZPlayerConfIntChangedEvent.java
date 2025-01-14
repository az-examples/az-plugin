package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AZPlayerConfIntChangedEvent extends AZPlayerEvent {

    private final @NotNull String key;
    private final int oldValue;
    private final int newValue;

    public AZPlayerConfIntChangedEvent(@NotNull AZPlayer azPlayer, @NonNull String key, int oldValue, int newValue) {
        super(azPlayer);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
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
