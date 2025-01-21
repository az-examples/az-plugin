package fr.nathan818.azplugin.common;

import static fr.nathan818.azplugin.common.chat.NotchianChatComponentLike.convertNonNull;
import static fr.nathan818.azplugin.common.item.NotchianItemStackLike.convertNonNull;

import fr.nathan818.azplugin.common.appearance.AZWorldEnv;
import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import fr.nathan818.azplugin.common.gui.AZChatBehavior;
import fr.nathan818.azplugin.common.gui.AZPopupAlert;
import fr.nathan818.azplugin.common.gui.AZPopupConfirm;
import fr.nathan818.azplugin.common.gui.AZPopupPrompt;
import fr.nathan818.azplugin.common.gui.AZUiComponent;
import fr.nathan818.azplugin.common.gui.AZVignette;
import fr.nathan818.azplugin.common.item.NotchianItemStackLike;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;
import pactify.client.api.plsp.packet.client.PLSPPacketChatBehavior;
import pactify.client.api.plsp.packet.client.PLSPPacketChatBehaviors;
import pactify.client.api.plsp.packet.client.PLSPPacketChatMessage;
import pactify.client.api.plsp.packet.client.PLSPPacketOpenBook;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupAlert;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupConfirm;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupPrompt;
import pactify.client.api.plsp.packet.client.PLSPPacketUiAction;
import pactify.client.api.plsp.packet.client.PLSPPacketUiComponent;
import pactify.client.api.plsp.packet.client.PLSPPacketUiComponents;

/**
 * Interface representing a player connected to the server.
 * <p>
 * Get an instance of this class by calling {@link AZAPI#getClient(Object)}.
 * <p>
 * <b>IMPORTANT:</b> If you use Bukkit/Spigot, look at {@link fr.nathan818.azplugin.bukkit.entity.AZPlayer}
 * instead.
 *
 * @see AZAPI#getClient(Object)
 */
public interface AZClient {
    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     * @az.async-safe
     */
    String getName();

    /**
     * Returns the Minecraft protocol version of the client used by the player.
     * <p>
     * A list of protocol versions can be found <a href="https://minecraft.wiki/w/Protocol_version"
     * target="_blank">here</a>. E.g., 1.8.x is protocol version 47, 1.9.4 is protocol version 110, etc.
     *
     * @return the Minecraft protocol version
     * @az.async-safe
     */
    int getMCProtocolVersion();

    /**
     * Returns the AZ protocol version of the client used by the player.
     * <p>
     * {@code -1} means the player does not use AZ Launcher. Else, the value is greater than or equal to 0.
     *
     * @return the AZ protocol version, or {@code -1} if the player does not use AZ Launcher
     * @az.async-safe
     */
    int getAZProtocolVersion();

    /**
     * Checks if the player is using the AZ Launcher.
     *
     * @return true if the player is using the AZ Launcher, false otherwise
     * @az.equivalent {@code getAZProtocolVersion() >= 0}
     * @az.async-safe
     */
    default boolean hasAZLauncher() {
        return getAZProtocolVersion() >= 0;
    }

    /**
     * Checks if the player has the AZLauncher with a minimum protocol version.
     *
     * @param protocolVersion the minimum protocol version to check
     * @return true if the player is using the AZ Launcher with at least the given protocol version, false otherwise
     * @az.equivalent {@code hasAZLauncher() && getAZProtocolVersion() >= protocolVersion}
     * @az.async-safe
     */
    default boolean hasAZLauncher(int protocolVersion) {
        return protocolVersion >= 0 && getAZProtocolVersion() >= protocolVersion;
    }

    /**
     * Checks if the player is disconnected.
     * <p>
     * This method returns false if the player is online, from the login to the quit (included). Then, it returns true.
     *
     * @return true if the player is disconnected, false otherwise
     * @az.async-safe
     */
    boolean isClosed();

    /**
     * Checks if the current thread is the network thread.
     * <p>
     * The network thread is the thread that handles network packets (receiving and sending). On most platforms, this
     * corresponds to the Netty channel event loop.
     *
     * @return true if the current thread is the network thread, false otherwise
     * @az.async-safe
     */
    boolean isInNetworkThread();

    /**
     * Asserts that the current thread is the {@linkplain #isInNetworkThread() network thread}.
     * <p>
     * This method throws an {@link IllegalStateException} if the current thread is not the network thread. Else, it
     * does nothing.
     *
     * @throws IllegalStateException if the current thread is not the network thread
     * @az.equivalent {@code if (!isInNetworkThread()) throw new IllegalStateException(...);}
     * @az.async-safe
     */
    default void assertInNetworkThread() {
        if (!isInNetworkThread()) {
            throw new IllegalStateException("Not in network thread");
        }
    }

    /**
     * Schedules a task to be executed in the {@linkplain #isInNetworkThread() network thread}.
     * <p>
     * If the current thread is the network thread, the task is queued to be executed after the current task.
     * <p>
     * The task is NOT guaranteed to be executed if the player connection is closed before it is executed.
     *
     * @param task the task to execute
     * @az.async-safe
     */
    void executeInNetworkThread(@NotNull Runnable task);

    /**
     * Schedules a task to be executed in the {@linkplain #isInNetworkThread() network thread}.
     * <p>
     * If the current thread is the network thread, the task is queued to be executed after the current task.
     * <p>
     * The returned future is guaranteed to be completed. If the player connection is closed, it is completed
     * exceptionally ({@link CompletableFuture#isCancelled()}).
     *
     * @param task the task to execute
     * @return a future that will be completed when the task is executed
     * @az.async-safe
     */
    <T> @NotNull CompletableFuture<T> executeInNetworkThread(@NotNull Callable<? extends T> task);

    /**
     * Gets the network context of the player.
     *
     * @return the network context
     * @az.async-safe
     */
    @NotNull
    AZNetworkContext getNetworkContext();

    /**
     * Sends an AZ Launcher packet to the player.
     * <p>
     * The AZ Launcher compatibility is checked before sending the packet. If the player does not support the packet
     * (because it does not have the AZ Launcher or the protocol version is too low), the method returns false and the
     * packet is not sent. Else, the method returns true, and the packet is sent (or scheduled to be sent).
     * <p>
     * This method can be called from any thread. If the player is inside the
     * {@linkplain #isInNetworkThread() network thread}, the packet is sent immediately. Else, the packet is scheduled
     * to be sent in the network thread.
     *
     * @param packet the packet to send
     * @return true if the player supports the packet
     * @az.low-level
     * @az.async-safe
     */
    boolean sendPacket(@NotNull PLSPPacket<PLSPPacketHandler.ClientHandler> packet);

    /**
     * @az.low-level
     * @az.async-safe
     * @deprecated NOT IMPLEMENTED YET
     */
    @Deprecated
    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(@NotNull Req packet, @NotNull Class<Res> responseClass) {
        return sendQueryPacket(packet, responseClass, null, null);
    }

    /**
     * @az.low-level
     * @az.async-safe
     * @deprecated NOT IMPLEMENTED YET
     */
    @Deprecated
    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable BiPredicate<Req, Res> responseMatcher
    ) {
        return sendQueryPacket(packet, responseClass, responseMatcher, null);
    }

    /**
     * @az.low-level
     * @az.async-safe
     * @deprecated NOT IMPLEMENTED YET
     */
    @Deprecated
    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable Duration timeout
    ) {
        return sendQueryPacket(packet, responseClass, null, timeout);
    }

    /**
     * @az.low-level
     * @az.async-safe
     * @deprecated NOT IMPLEMENTED YET
     */
    @Deprecated
    <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable BiPredicate<Req, Res> responseMatcher,
        @Nullable Duration timeout
    );

    /**
     * Returns the value of an AZ Launcher configuration flag.
     * <p>
     * If the player is not using the AZ Launcher, a default value is deducted.
     *
     * @param key the configuration key
     * @return the value of the configuration
     * @throws IllegalArgumentException if the key is unknown
     * @az.low-level
     * @az.async-safe
     */
    default boolean getConfFlag(@NotNull String key) {
        AZConstants.assertConfFlagExists(key);
        return AZConstants.getDefaultConfFlag(key, getAZProtocolVersion(), getMCProtocolVersion());
    }

    /**
     * Returns the value of an AZ Launcher configuration integer.
     * <p>
     * If the player is not using the AZ Launcher, a default value is deducted.
     *
     * @param key the configuration key
     * @return the value of the configuration
     * @throws IllegalArgumentException if the key is unknown
     * @az.low-level
     * @az.async-safe
     */
    default int getConfInt(@NotNull String key) {
        AZConstants.assertConfIntExists(key);
        return AZConstants.getDefaultConfInt(key, getAZProtocolVersion(), getMCProtocolVersion());
    }

    /**
     * Sets the value of an AZ Launcher configuration flag.
     * <p>
     * The AZ Launcher compatibility is checked first, and if the player does not support this key (because it does not
     * have the AZ Launcher or the protocol version is too low), this method returns false. Else, the configuration is
     * set and the method returns true.
     *
     * @param key   the configuration key
     * @param value the value to set
     * @return true if the configuration was set, false if the player does not support this key
     * @throws IllegalArgumentException if the key is unknown
     * @az.low-level
     * @az.async-safe
     */
    default boolean setConfFlag(@NotNull String key, boolean value) {
        AZConstants.assertConfFlagExists(key);
        throw new UnsupportedOperationException("setConfFlag is not supported on this platform");
    }

    /**
     * Sets the value of an AZ Launcher configuration integer.
     * <p>
     * The AZ Launcher compatibility is checked first, and if the player does not support this key (because it does not
     * have the AZ Launcher or the protocol version is too low), this method returns false. Else, the configuration is
     * set and the method returns true.
     *
     * @param key   the configuration key
     * @param value the value to set
     * @return true if the configuration was set, false if the player does not support this key
     * @throws IllegalArgumentException if the key is unknown
     * @az.low-level
     * @az.async-safe
     */
    default boolean setConfInt(@NotNull String key, int value) {
        AZConstants.assertConfFlagExists(key);
        throw new UnsupportedOperationException("setConfInt is not supported on this platform");
    }

    /**
     * Checks whether the attack cooldown is disabled.
     *
     * @return true if the attack cooldown is disabled, false otherwise
     * @az.equivalent {@code !getConfFlag("attack_cooldown")}
     * @az.async-safe
     * @see #setDisableAttackCooldown(boolean)
     */
    default boolean isDisableAttackCooldown() {
        return !getConfFlag("attack_cooldown");
    }

    /**
     * Disable/enable the attack cooldown.
     * <p>
     * When disabled, the attack cooldown (added in 1.9) is fully removed, and the player can attack as fast as
     * possible, like in 1.8.
     *
     * @param value true to disable the attack cooldown, false to enable it
     * @return true if the configuration was set, false if the player does not support this key
     * @az.equivalent {@code setConfFlag("attack_cooldown", !value)}
     * @az.async-safe
     * @see #isDisableAttackCooldown()
     */
    default boolean setDisableAttackCooldown(boolean value) {
        return setConfFlag("attack_cooldown", !value);
    }

    /**
     * Checks whether the player can hit-and-block.
     *
     * @return true if the hit-and-block is enabled, false otherwise
     * @az.equivalent {@code getConfFlag("hit_and_block")}
     * @az.async-safe
     * @see #setHitAndBlock(boolean)
     */
    default boolean isHitAndBlock() {
        return getConfFlag("hit_and_block");
    }

    /**
     * Enable/disable the hit-and-block.
     * <p>
     * When enabled, the player can block (right-click with sword/shield) while playing the digging animation, like in
     * 1.7.
     *
     * @param value true to enable the hit-and-block, false to disable it
     * @return true if the configuration was set, false if the player does not support this key
     * @az.equivalent {@code setConfFlag("hit_and_block", value)}
     * @az.async-safe
     * @see #isHitAndBlock()
     */
    default boolean setHitAndBlock(boolean value) {
        return setConfFlag("hit_and_block", value);
    }

    default boolean isDisableHitIndicator() {
        return !getConfFlag("hit_indicator");
    }

    default boolean setDisableHitIndicator(boolean value) {
        return setConfFlag("hit_indicator", !value);
    }

    /**
     * Checks whether the player uses large hitboxes for entities.
     *
     * @return true if the large hitboxes are enabled, false otherwise
     * @az.equivalent {@code getConfFlag("large_hitbox")}
     * @az.async-safe
     * @see #setLargeHitbox(boolean)
     */
    default boolean isLargeHitbox() {
        return getConfFlag("large_hitbox");
    }

    /**
     * Enable/disable the use of large hitboxes for entities.
     * <p>
     * When enabled, the player uses 1.8 hitboxes for entities, which are +0.1 blocks larger in each direction compared
     * to 1.9+.
     *
     * @param value true to enable the use of large hitboxes, false to disable it
     * @return true if the configuration was set, false if the player does not support this key
     * @az.equivalent {@code setConfFlag("large_hitbox", value)}
     * @az.async-safe
     * @see #isLargeHitbox()
     */
    default boolean setLargeHitbox(boolean value) {
        return setConfFlag("large_hitbox", value);
    }

    /**
     * Checks whether the old enchantments interface is used.
     *
     * @return true if the old enchantments interface is used, false otherwise
     * @az.equivalent {@code getConfFlag("old_enchantments")}
     * @az.async-safe
     * @see #setOldEnchantments(boolean)
     */
    default boolean isOldEnchantments() {
        return getConfFlag("old_enchantments");
    }

    /**
     * Enable/disable the old enchantments interface.
     * <p>
     * When enabled, the player uses the 1.7 enchanting interface.
     *
     * @param value true to enable the old enchantments interface, false to disable it
     * @return true if the configuration was set, false if the player does not support this key
     * @az.equivalent {@code setConfFlag("old_enchantments", value)}
     * @az.async-safe
     * @see #isHitAndBlock()
     * @deprecated This feature is not yet implemented on the server side!
     */
    default boolean setOldEnchantments(boolean value) {
        // TODO: Support this server-side
        return setConfFlag("old_enchantments", value);
    }

    default boolean isDisablePistonsRetractEntities() {
        return !getConfFlag("pistons_retract_entities");
    }

    default boolean setDisablePistonsRetractEntities(boolean value) {
        return setConfFlag("pistons_retract_entities", !value);
    }

    default boolean isDisablePlayerPush() {
        return !getConfFlag("player_push");
    }

    default boolean setDisablePlayerPush(boolean value) {
        return setConfFlag("player_push", !value);
    }

    default boolean isPvpHitPriority() {
        return getConfFlag("pvp_hit_priority");
    }

    default boolean setPvpHitPriority(boolean value) {
        return setConfFlag("pvp_hit_priority", value);
    }

    default boolean isSeeChunks() {
        return getConfFlag("see_chunks");
    }

    default boolean setSeeChunks(boolean value) {
        return setConfFlag("see_chunks", value);
    }

    default boolean isServerSideAnvil() {
        return getConfFlag("server_side_anvil");
    }

    default boolean setServerSideAnvil(boolean value) {
        return setConfFlag("server_side_anvil", value);
    }

    default boolean isDisableSidebarScores() {
        return !getConfFlag("sidebar_scores");
    }

    default boolean setDisableSidebarScores(boolean value) {
        return setConfFlag("sidebar_scores", !value);
    }

    default boolean isSmoothExperienceBar() {
        return getConfFlag("smooth_experience_bar");
    }

    default boolean setSmoothExperienceBar(boolean value) {
        return setConfFlag("smooth_experience_bar", value);
    }

    default boolean isSortTabListByNames() {
        return getConfFlag("sort_tab_list_by_names");
    }

    default boolean setSortTabListByNames(boolean value) {
        return setConfFlag("sort_tab_list_by_names", value);
    }

    default boolean isSwordBlocking() {
        return getConfFlag("sword_blocking");
    }

    default boolean setSwordBlocking(boolean value) {
        return setConfFlag("sword_blocking", value);
    }

    default int getChatMessageMaxSize() {
        return getConfInt("chat_message_max_size");
    }

    default boolean setChatMessageMaxSize(int value) {
        return setConfInt("chat_message_max_size", value);
    }

    default int getMaxBuildHeight() {
        return getConfInt("max_build_height");
    }

    default boolean setMaxBuildHeight(int value) {
        return setConfInt("max_build_height", value);
    }

    /**
     * Sends a chat message to the player.
     * <p>
     * The chat message is identified by a unique ID, which can be used to alter the message later.
     *
     * @param id      the unique ID of the message
     * @param message the message to send
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean sendMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.ADD, id, convertNonNull(message)));
    }

    /**
     * Replaces the content of a chat message previously sent to the player.
     * <p>
     * If no message with the given ID is found, nothing happens. Else, the existing message is replaced in-place with
     * the new one.
     *
     * @param id      the unique ID of the message to update
     * @param message the new message to replace with
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean replaceMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.REPLACE, id, convertNonNull(message)));
    }

    /**
     * Appends to the content of a chat message previously sent to the player.
     * <p>
     * If no message with the given ID is found, nothing happens. Else, the new message is appended to the existing
     * one.
     *
     * @param id      the unique ID of the message to update
     * @param message the message to append
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean appendMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.APPEND, id, convertNonNull(message)));
    }

    /**
     * Prepends to the content of a chat message previously sent to the player.
     * <p>
     * If no message with the given ID is found, nothing happens. Else, the new message is prepended to the existing
     * one.
     *
     * @param id      the unique ID of the message to update
     * @param message the message to prepend
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean prependMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.PREPEND, id, convertNonNull(message)));
    }

    /**
     * Removes a chat message previously sent to the player.
     * <p>
     * If no message with the given ID is found, nothing happens. Else, the message is removed from the chat.
     *
     * @param id the unique ID of the message to remove
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean removeMessage(@NonNull UUID id) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.REMOVE, id, null));
    }

    /**
     * Opens the book reading interface to the player.
     *
     * @param book the book to open
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean openBook(@NonNull NotchianItemStackLike book) {
        return sendPacket(new PLSPPacketOpenBook(convertNonNull(book)));
    }

    /**
     * Returns the custom vignette currently displayed on the player's screen.
     * <p>
     * If no custom vignette is set, this method returns null.
     *
     * @return the custom vignette, or null if none
     * @az.async-safe
     */
    @Nullable
    default AZVignette getVignette() {
        return null;
    }

    /**
     * Sets a custom vignette to be displayed on the player's screen.
     *
     * @param vignette the vignette to set, or null to remove the custom vignette
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZVignette AZVignette for more information
     */
    default boolean setVignette(@Nullable AZVignette vignette) {
        throw new UnsupportedOperationException("setVignette is not supported on this platform");
    }

    /**
     * Returns the custom world environment currently set for the player.
     * <p>
     * If no custom world environment is set, this method returns null.
     *
     * @return the custom world environment, or null if none
     * @az.async-safe
     */
    @Nullable
    default AZWorldEnv getWorldEnv() {
        return null;
    }

    /**
     * Sets a custom world environment to the player.
     * <p>
     * The world environment is unset when switching worlds.
     *
     * @param env the world environment to set, or null to remove the custom world environment
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZWorldEnv AZWorldEnv for more information
     */
    default boolean setWorldEnv(@Nullable AZWorldEnv env) {
        throw new UnsupportedOperationException("setWorldEnv is not supported on this platform");
    }

    /**
     * Updates UI components.
     *
     * @see #setUiComponent(Iterable)
     */
    default boolean setUiComponent(@NotNull AZUiComponent @NotNull... components) {
        return setUiComponent(Arrays.asList(components));
    }

    /**
     * Updates UI components.
     *
     * @param components the components to be set/replaced/removed
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZUiComponent AZUiComponent for more information
     */
    default boolean setUiComponent(@NotNull Iterable<? extends AZUiComponent> components) {
        List<PLSPPacketUiComponent> packets = new ArrayList<>();
        for (AZUiComponent component : components) {
            packets.add(new PLSPPacketUiComponent(component.getSlot().getId(), component.getButton()));
        }
        switch (packets.size()) {
            case 0:
                return hasAZLauncher(PLSPPacketUiComponent.SINCE_PROTOCOL_VERSION);
            case 1:
                return sendPacket(packets.get(0));
            default:
                return sendPacket(new PLSPPacketUiComponents(packets));
        }
    }

    /**
     * Updates chat behaviors.
     *
     * @see #setChatBehavior(Iterable)
     */
    default boolean setChatBehavior(@NotNull AZChatBehavior @NotNull... behaviors) {
        return setChatBehavior(Arrays.asList(behaviors));
    }

    /**
     * Updates chat behaviors.
     *
     * @param behaviors the chat behaviors to be set/replaced/removed
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZChatBehavior AZChatBehavior for more information
     */
    default boolean setChatBehavior(@NotNull Iterable<? extends AZChatBehavior> behaviors) {
        List<PLSPPacketChatBehavior> packets = new ArrayList<>();
        for (AZChatBehavior behavior : behaviors) {
            if (behavior.isSet()) {
                packets.add(
                    new PLSPPacketChatBehavior(
                        PLSPPacketChatBehavior.Action.ADD,
                        behavior.getId(),
                        behavior.getPattern(),
                        behavior.getMessage(),
                        behavior.getSerializedTagColor(),
                        behavior.getPriority()
                    )
                );
            } else {
                packets.add(new PLSPPacketChatBehavior(PLSPPacketChatBehavior.Action.REMOVE, behavior.getId()));
            }
        }
        switch (packets.size()) {
            case 0:
                return hasAZLauncher(PLSPPacketChatBehavior.SINCE_PROTOCOL_VERSION);
            case 1:
                return sendPacket(packets.get(0));
            default:
                return sendPacket(new PLSPPacketChatBehaviors(packets));
        }
    }

    /**
     * Removes all added chat behaviors.
     *
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see #setChatBehavior(Iterable)
     */
    default boolean removeChatBehaviors() {
        return sendPacket(new PLSPPacketChatBehavior(PLSPPacketChatBehavior.Action.REMOVE_ALL));
    }

    /**
     * Opens a popup to the player.
     *
     * @param alert the alert popup to open
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZPopupAlert AZPopupAlert for more information
     */
    default boolean openPopup(@NotNull AZPopupAlert alert) {
        return sendPacket(new PLSPPacketPopupAlert(alert.getDescription(), alert.getCloseEvent()));
    }

    /**
     * Opens a popup to the player.
     *
     * @param confirm the confirmation popup to open
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZPopupConfirm AZPopupConfirm for more information
     */
    default boolean openPopup(@NotNull AZPopupConfirm confirm) {
        return sendPacket(
            new PLSPPacketPopupConfirm(confirm.getDescription(), confirm.getOkEvent(), confirm.getCancelEvent())
        );
    }

    /**
     * Opens a popup to the player.
     *
     * @param prompt the prompt popup to open
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     * @see AZPopupPrompt AZPopupPrompt for more information
     */
    default boolean openPopup(@NotNull AZPopupPrompt prompt) {
        return sendPacket(
            new PLSPPacketPopupPrompt(
                prompt.getDescription(),
                prompt.getOkEvent(),
                prompt.getCancelEvent(),
                prompt.getDefaultValue(),
                prompt.getTypingRegex(),
                prompt.getFinalRegex(),
                prompt.isPassword()
            )
        );
    }

    /**
     * Opens a loading screen to the player.
     *
     * @az.equivalent {@code openLoadScreen(null, 0, false)}
     * @see #openLoadScreen(UUID, int, boolean)
     */
    default boolean openLoadScreen() {
        return openLoadScreen(null);
    }

    /**
     * Opens a loading screen to the player.
     *
     * @az.equivalent {@code openLoadScreen(id, 0, false)}
     * @see #openLoadScreen(UUID, int, boolean)
     */
    default boolean openLoadScreen(@Nullable UUID id) {
        return openLoadScreen(id, 0, false);
    }

    /**
     * Opens a loading screen to the player.
     * <p>
     * The loading screen is a full-screen overlay, displaying a loading animation.
     * <p>
     * The loading screen closes when:
     * <ul>
     * <li>another screen is opened (e.g., an inventory, a book, a popup, another loading screen, etc.)</li>
     * <li>the server closes it manually (using {@link #closeLoadScreen(UUID)})</li>
     * <li>the {@code autoCloseDelayTicks} delay expires</li>
     * <li>the player manually closes it by pressing the escape key (unless the {@code blockEscape} parameter is set
     * to true)</li>
     * <li>(the player disconnects)</li>
     * </ul>
     *
     * @param id                  the unique ID identifying the loading screen, or null if not needed
     * @param autoCloseDelayTicks the delay in ticks before automatically closing the loading screen, or 0 to keep it
     *                            open indefinitely
     * @param blockEscape         true to prevent the escape key from closing the loading screen
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean openLoadScreen(@Nullable UUID id, int autoCloseDelayTicks, boolean blockEscape) {
        StringBuilder params = new StringBuilder();
        if (id != null) {
            params.append("id=").append(id);
        }
        if (autoCloseDelayTicks > 0) {
            if (params.length() > 0) {
                params.append('&');
            }
            params.append("delay=").append(autoCloseDelayTicks);
        }
        if (blockEscape) {
            if (params.length() > 0) {
                params.append('&');
            }
            params.append("noesc");
        }
        return sendPacket(new PLSPPacketUiAction("OPEN_LOAD", params.toString()));
    }

    /**
     * Closes a loading screen.
     *
     * @az.equivalent {@code closeLoadScreen(null)}
     * @see #closeLoadScreen(UUID)
     */
    default boolean closeLoadScreen() {
        return closeLoadScreen(null);
    }

    /**
     * Closes a loading screen.
     *
     * @param id the unique ID of the loading screen to close, or null to close any
     * @return true if the player supports this feature, false otherwise
     * @az.async-safe
     */
    default boolean closeLoadScreen(@Nullable UUID id) {
        String params = (id == null) ? "" : "id=" + id;
        return sendPacket(new PLSPPacketUiAction("CLOSE_LOAD", params));
    }
}
