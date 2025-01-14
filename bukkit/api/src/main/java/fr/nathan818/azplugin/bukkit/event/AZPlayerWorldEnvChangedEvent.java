package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.appearance.AZWorldEnv;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

@Getter
public class AZPlayerWorldEnvChangedEvent extends AZPlayerEvent {

    private final @Nullable AZWorldEnv oldWorldEnv;
    private final @Nullable AZWorldEnv newWorldEnv;

    public AZPlayerWorldEnvChangedEvent(
        AZPlayer azPlayer,
        @Nullable AZWorldEnv oldWorldEnv,
        @Nullable AZWorldEnv newWorldEnv
    ) {
        super(azPlayer);
        this.oldWorldEnv = oldWorldEnv;
        this.newWorldEnv = newWorldEnv;
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
