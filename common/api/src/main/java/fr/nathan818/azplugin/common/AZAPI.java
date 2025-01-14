package fr.nathan818.azplugin.common;

import static java.util.Objects.requireNonNull;

import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entry point to common (platform-independent) AZPlugin API.
 * <p>
 * Get an instance of this class by calling {@link AZ#api()}.
 * <p>
 * <b>IMPORTANT:</b> If you use Bukkit/Spigot, look at {@link fr.nathan818.azplugin.bukkit.AZBukkitAPI}
 * instead.
 *
 * @param <Player> the platform-specific player type
 * @param <Client> the platform-specific AZClient type
 */
@SuppressWarnings("JavadocReference")
public interface AZAPI<Player, Client extends AZClient> {
    @Contract(value = "null -> null; !null -> _", pure = true)
    @Nullable
    Client getClient(@Nullable Player player);

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @Nullable
    default Client getClientOrFail(@Nullable Player player) throws IllegalStateException {
        if (player == null) {
            return null;
        }
        Client client = getClient(player);
        if (client == null) {
            throw new IllegalStateException("AZClient not found for player: " + getPlayerName(player));
        }
        return client;
    }

    @Contract(pure = true)
    @NotNull
    default String getPlayerName(@Nullable Player player) {
        if (player == null) {
            return "<null>";
        }
        try {
            return requireNonNull(player.toString());
        } catch (Exception ex) {
            return "<getPlayerName() failed>";
        }
    }

    @NotNull
    AZPacketBuffer createHeapPacketBuffer(@Nullable AZClient client);

    @NotNull
    AZPacketBuffer createHeapPacketBuffer(@Nullable AZClient client, int initialCapacity);
}
