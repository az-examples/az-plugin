package fr.nathan818.azplugin.common;

import static java.util.Objects.requireNonNull;

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
 * @see AZ#api()
 */
@SuppressWarnings("JavadocReference")
public interface AZAPI<Player, Client extends AZClient> {
    /**
     * Returns the AZClient instance associated with the given player.
     * <p>
     * This method returns a non-null value when the player is online, from the login to the quit. Else, if the player
     * is pre-login, offline or null, this method returns null.
     *
     * @param player the player to get the AZClient for
     * @return the AZClient instance associated with the given player, or null if not found
     * @az.async-safe
     */
    @Contract(value = "null -> null; !null -> _", pure = true)
    @Nullable
    Client getClient(@Nullable Player player);

    /**
     * Returns the AZClient instance associated with the given player.
     * <p>
     * This method returns a non-null value when the player is online, from the login to the quit. Else, if the player
     * is pre-login, offline or null, this method fails with an {@link IllegalStateException}.
     *
     * @param player the player to get the AZClient for
     * @return the AZClient instance associated with the given player
     * @throws IllegalStateException if the player is not found
     * @az.async-safe
     */
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

    /**
     * Returns the name of the given player.
     * <p>
     * This method never fails and always returns a non-null value. Useful for logging.
     *
     * @param player the player to get the name
     * @return the name of the player, or the string "&lt;null&gt;" if the player is null
     * @az.async-safe
     */
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
}
