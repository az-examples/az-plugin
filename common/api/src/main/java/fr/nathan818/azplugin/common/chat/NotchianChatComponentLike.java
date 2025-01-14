package fr.nathan818.azplugin.common.chat;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;

@FunctionalInterface
public interface NotchianChatComponentLike {
    static @Nullable NotchianChatComponent convert(@Nullable NotchianChatComponentLike message) {
        return message == null ? null : message.asNotchianChatComponent();
    }

    static @NotNull NotchianChatComponent convertNonNull(@NotNull NotchianChatComponentLike message) {
        return Objects.requireNonNull(message.asNotchianChatComponent(), "asNotchianChatComponent() returned null");
    }

    @Nullable
    NotchianChatComponent asNotchianChatComponent();
}
