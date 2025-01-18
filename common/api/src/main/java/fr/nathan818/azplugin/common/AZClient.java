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

public interface AZClient {
    String getName();

    int getMCProtocolVersion();

    int getAZProtocolVersion();

    default boolean hasAZLauncher() {
        return getAZProtocolVersion() >= 0;
    }

    default boolean hasAZLauncher(int protocolVersion) {
        return protocolVersion >= 0 && getAZProtocolVersion() >= protocolVersion;
    }

    boolean isClosed();

    boolean isInNetworkThread();

    default void assertInNetworkThread() {
        if (!isInNetworkThread()) {
            throw new IllegalStateException("Not in network thread");
        }
    }

    void executeInNetworkThread(@NotNull Runnable task);

    <T> @NotNull CompletableFuture<T> executeInNetworkThread(@NotNull Callable<? extends T> task);

    AZNetworkContext getNetworkContext();

    boolean sendPacket(@NotNull PLSPPacket<PLSPPacketHandler.ClientHandler> packet);

    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(@NotNull Req packet, @NotNull Class<Res> responseClass) {
        return sendQueryPacket(packet, responseClass, null, null);
    }

    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable BiPredicate<Req, Res> responseMatcher
    ) {
        return sendQueryPacket(packet, responseClass, responseMatcher, null);
    }

    default <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable Duration timeout
    ) {
        return sendQueryPacket(packet, responseClass, null, timeout);
    }

    <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable BiPredicate<Req, Res> responseMatcher,
        @Nullable Duration timeout
    );

    default boolean getConfFlag(String key) {
        AZConstants.assertConfFlagExists(key);
        return AZConstants.getDefaultConfFlag(key, getAZProtocolVersion());
    }

    default int getConfInt(String key) {
        AZConstants.assertConfIntExists(key);
        return AZConstants.getDefaultConfInt(key, getAZProtocolVersion());
    }

    default boolean setConfFlag(String key, boolean value) {
        AZConstants.assertConfFlagExists(key);
        throw new UnsupportedOperationException("setConfFlag is not supported on this platform");
    }

    default boolean setConfInt(String key, int value) {
        AZConstants.assertConfFlagExists(key);
        throw new UnsupportedOperationException("setConfInt is not supported on this platform");
    }

    default boolean isDisableAttackCooldown() {
        return !getConfFlag("attack_cooldown");
    }

    default boolean setDisableAttackCooldown(boolean value) {
        // TODO: Support this server-side
        return setConfFlag("attack_cooldown", !value);
    }

    default boolean isHitAndBlock() {
        return getConfFlag("hit_and_block");
    }

    default boolean setHitAndBlock(boolean value) {
        return setConfFlag("hit_and_block", value);
    }

    default boolean isDisableHitIndicator() {
        return !getConfFlag("hit_indicator");
    }

    default boolean setDisableHitIndicator(boolean value) {
        return setConfFlag("hit_indicator", !value);
    }

    default boolean isLargeHitbox() {
        return getConfFlag("large_hitbox");
    }

    default boolean setLargeHitbox(boolean value) {
        return setConfFlag("large_hitbox", value);
    }

    default boolean isOldEnchantments() {
        return getConfFlag("old_enchantments");
    }

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

    default boolean sendMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.ADD, id, convertNonNull(message)));
    }

    default boolean replaceMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.REPLACE, id, convertNonNull(message)));
    }

    default boolean appendMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.APPEND, id, convertNonNull(message)));
    }

    default boolean prependMessage(@NonNull UUID id, @NonNull NotchianChatComponentLike message) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.PREPEND, id, convertNonNull(message)));
    }

    default boolean removeMessage(@NonNull UUID id) {
        return sendPacket(new PLSPPacketChatMessage(PLSPPacketChatMessage.Action.REMOVE, id, null));
    }

    default boolean openBook(@NonNull NotchianItemStackLike book) {
        return sendPacket(new PLSPPacketOpenBook(convertNonNull(book)));
    }

    @Nullable
    default AZVignette getVignette() {
        return null;
    }

    default boolean setVignette(@Nullable AZVignette vignette) {
        throw new UnsupportedOperationException("setVignette is not supported on this platform");
    }

    @Nullable
    default AZWorldEnv getWorldEnv() {
        return null;
    }

    default boolean setWorldEnv(@Nullable AZWorldEnv env) {
        throw new UnsupportedOperationException("setWorldEnv is not supported on this platform");
    }

    default boolean setUiComponent(@NotNull AZUiComponent @NotNull... components) {
        return setUiComponent(Arrays.asList(components));
    }

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

    default boolean setChatBehavior(@NotNull AZChatBehavior @NotNull... behaviors) {
        return setChatBehavior(Arrays.asList(behaviors));
    }

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

    default boolean removeChatBehaviors() {
        return sendPacket(new PLSPPacketChatBehavior(PLSPPacketChatBehavior.Action.REMOVE_ALL));
    }

    default boolean openPopup(@NotNull AZPopupAlert alert) {
        return sendPacket(new PLSPPacketPopupAlert(alert.getDescription(), alert.getCloseEvent()));
    }

    default boolean openPopup(@NotNull AZPopupConfirm confirm) {
        return sendPacket(
            new PLSPPacketPopupConfirm(confirm.getDescription(), confirm.getOkEvent(), confirm.getCancelEvent())
        );
    }

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

    default boolean openLoadScreen() {
        return openLoadScreen(null);
    }

    default boolean openLoadScreen(@Nullable UUID id) {
        return openLoadScreen(id, 0, false);
    }

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

    default boolean closeLoadScreen() {
        return closeLoadScreen(null);
    }

    default boolean closeLoadScreen(@Nullable UUID id) {
        String params = (id != null) ? "id=" + id : "";
        return sendPacket(new PLSPPacketUiAction("CLOSE_LOAD", params));
    }
}
