package fr.nathan818.azplugin.bukkit.plugin;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;
import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@RequiredArgsConstructor
public class ConfManager implements Listener {

    private final AZPlugin plugin;

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    private static int getChatMessageMaxSize(AZPlayer azPlayer) {
        int ret;
        if (azPlayer == null) {
            ret = compat().getDefaultChatMessageMaxSize();
        } else if (azPlayer.hasAZLauncher()) {
            ret = Math.max(0, azPlayer.getChatMessageMaxSize());
        } else if (azPlayer.getMCProtocolVersion() >= 315) { // 315 = 1.11
            ret = 256;
        } else {
            ret = 100;
        }
        return ret;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        AZPlayer azPlayer = az(event.getPlayer());
        int limit = getChatMessageMaxSize(azPlayer);
        String message = event.getMessage();
        if (message.length() > limit) {
            event.setMessage(message.substring(0, limit));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        AZPlayer azPlayer = az(event.getPlayer());
        int limit = getChatMessageMaxSize(azPlayer);
        String message = event.getMessage();
        if (message.length() > limit) {
            event.setMessage(message.substring(0, limit));
        }
    }
}
