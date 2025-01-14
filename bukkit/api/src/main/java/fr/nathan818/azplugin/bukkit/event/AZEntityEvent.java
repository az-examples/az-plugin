package fr.nathan818.azplugin.bukkit.event;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import lombok.Getter;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AZEntityEvent extends EntityEvent {

    private final @NotNull AZEntity azEntity;

    public AZEntityEvent(@NotNull AZEntity azEntity) {
        super(azEntity.getBukkitEntity());
        this.azEntity = azEntity;
    }
}
