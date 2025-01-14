package fr.nathan818.azplugin.bukkit.compat.event;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class EntityTrackBeginEvent extends EntityEvent {

    private final @NotNull Player viewer;

    public EntityTrackBeginEvent(@NotNull Entity entity, @NotNull Player viewer) {
        super(entity);
        this.viewer = viewer;
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
