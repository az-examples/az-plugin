package fr.nathan818.azplugin.bukkit.plugin.entity;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.bukkit.compat.event.EntityTrackBeginEvent;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.plugin.AZPlugin;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

@RequiredArgsConstructor
public class EntityManager implements Listener {

    private final AZPlugin plugin;

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // TODO: reload all entities
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        AZPlayer azPlayer = az(event.getPlayer());
        if (azPlayer != null) {
            // Send initial metadata to self
            // Delayed to be sent after PacketPlayOutPlayerInfo
            // TODO: Use a player-targeted scheduler utility
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.scheduleSyncDelayedTask(plugin, () -> {
                if (azPlayer.isValid()) {
                    azPlayer.flushAllMetadata(Collections.singleton(azPlayer.getBukkitPlayer()), true);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTrackBegin(EntityTrackBeginEvent event) {
        AZEntity azEntity = AZBukkit.api().getEntityIfPresent(event.getEntity());
        if (azEntity != null) {
            azEntity.flushAllMetadata(Collections.singleton(event.getViewer()), true);
        }
    }
}
