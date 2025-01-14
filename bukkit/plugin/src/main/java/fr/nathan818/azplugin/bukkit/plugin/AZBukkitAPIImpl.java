package fr.nathan818.azplugin.bukkit.plugin;

import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;
import static java.util.Objects.requireNonNull;

import fr.nathan818.azplugin.bukkit.AZBukkitAPI;
import fr.nathan818.azplugin.bukkit.compat.network.NettyPacketBuffer;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.plugin.entity.AZEntityImpl;
import fr.nathan818.azplugin.bukkit.plugin.entity.AZPlayerImpl;
import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AZBukkitAPIImpl implements AZBukkitAPI {

    private final AZPlugin plugin;

    @Override
    public @Nullable AZPlayerImpl getClient(@Nullable Player player) {
        return plugin.clientManager.getClient(player);
    }

    @Override
    public @Nullable AZPlayerImpl getClientOrFail(@Nullable Player player) throws IllegalStateException {
        if (player == null) {
            return null;
        }
        AZPlayerImpl client = getClient(player);
        if (client == null) {
            throw new IllegalStateException("AZPlayer not found for player: " + getPlayerName(player));
        }
        return client;
    }

    @Override
    public @NotNull String getPlayerName(@Nullable Player player) {
        if (player == null) {
            return "<null>";
        }
        try {
            return requireNonNull(player.getName());
        } catch (Exception ex) {
            return "<getPlayerName() failed>";
        }
    }

    @Override
    public @NotNull AZPacketBuffer createHeapPacketBuffer(@Nullable AZClient client) {
        return NettyPacketBuffer.create(client);
    }

    @Override
    public @NotNull AZPacketBuffer createHeapPacketBuffer(@Nullable AZClient client, int initialCapacity) {
        return NettyPacketBuffer.create(client, initialCapacity);
    }

    @Override
    public @Nullable AZEntity getEntity(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Player) {
            return getClient((Player) entity);
        }
        AZEntity ret = compat().getAZEntity(entity);
        if (ret == null) {
            synchronized (entity) {
                ret = compat().getAZEntity(entity);
                if (ret == null) {
                    ret = compat().setAZEntity(entity, () -> new AZEntityImpl(plugin, entity));
                }
            }
        }
        return ret;
    }

    @Override
    public @Nullable AZEntity getEntityIfPresent(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Player) {
            return getClient((Player) entity);
        }
        return compat().getAZEntity(entity);
    }
}
