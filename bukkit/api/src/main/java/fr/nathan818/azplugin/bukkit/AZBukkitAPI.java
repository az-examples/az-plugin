package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.AZAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Entry point to Bukkit AZPlugin API.
 * <p>
 * Get an instance of this class by calling {@link AZBukkit#api()}.
 */
public interface AZBukkitAPI extends AZAPI<Player, AZPlayer> {
    @Contract("null -> null")
    @Nullable
    AZEntity getEntity(@Nullable Entity entity);

    @Contract("null -> null")
    @Nullable
    AZEntity getEntityIfPresent(@Nullable Entity entity);
}
