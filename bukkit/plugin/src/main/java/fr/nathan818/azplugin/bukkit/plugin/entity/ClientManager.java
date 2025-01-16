package fr.nathan818.azplugin.bukkit.plugin.entity;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;
import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;
import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.plugin.AZPlugin;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ClientManager implements Listener {

    private static final Pattern HOSTNAME_VERSION_PATTERN = Pattern.compile(
        "[\u0000\u0002]PAC([0-9A-F]{5})[\u0000\u0002]"
    );

    private final AZPlugin plugin;
    private final Map<Player, AZPlayerImpl> clients = new ConcurrentHashMap<>();

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            try {
                onPlayerLogin(player, compat().getLoginHostname(player));
                onPlayerJoin(player);
            } catch (Exception ex) {
                log(Level.WARNING, "Failed to initialize AZPlayer for {0}", az().getPlayerName(player), ex);
            }
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public AZPlayerImpl getClient(@Nullable Player player) {
        return clients.get(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        onPlayerLogin(event.getPlayer(), event.getHostname());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginMonitor(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            onPlayerQuit(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerQuit(event.getPlayer());
    }

    private void onPlayerLogin(Player player, String hostname) {
        if (hostname == null) {
            log(
                Level.WARNING,
                "Unable to retrieve hostname for {0} (probably caused by /reload)",
                az().getPlayerName(player)
            );
        }

        int mcProtocolVersion = compat().getMCProtocolVersion(player);
        // TODO: Support ViaVersion, etc
        int azProtocolVersion = parseAZProtocolVersion(hostname);
        AZPlayerImpl azPlayer = new AZPlayerImpl(plugin, player, mcProtocolVersion, azProtocolVersion);
        clients.put(player, azPlayer);
        if (azProtocolVersion >= 0) {
            log(
                Level.INFO,
                "Player {0} logged in with AZ Launcher (protocol version: {1})",
                az().getPlayerName(player),
                azProtocolVersion
            );
        } else {
            log(Level.INFO, "Player {0} logged in without AZ Launcher", az().getPlayerName(player));
        }
    }

    private void onPlayerJoin(Player player) {
        AZPlayerImpl azPlayer = clients.get(player);
        PlayerConnection playerConnection = compat().initPlayerConnection(player);
        azPlayer.setPlayerConnection(playerConnection);
    }

    private void onPlayerQuit(Player player) {
        AZPlayerImpl azPlayer = clients.remove(player);
        if (azPlayer != null) {
            azPlayer.markClosed();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        AZPlayerImpl azPlayer = clients.get(event.getPlayer());
        if (azPlayer != null) {
            azPlayer.setWorldEnv(null); // WorldEnv is cleared client-side when switching worlds
        }
    }

    private static int parseAZProtocolVersion(@Nullable String hostname) {
        if (hostname != null) {
            Matcher m = HOSTNAME_VERSION_PATTERN.matcher(hostname);
            if (m.find()) {
                return Math.max(1, Integer.parseInt(m.group(1), 16));
            }
        }
        return -1;
    }
}
