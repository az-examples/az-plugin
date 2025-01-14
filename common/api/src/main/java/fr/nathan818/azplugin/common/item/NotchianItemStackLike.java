package fr.nathan818.azplugin.common.item;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianItemStack;

@FunctionalInterface
public interface NotchianItemStackLike {
    static @Nullable NotchianItemStack convert(@Nullable NotchianItemStackLike message) {
        return message == null ? null : message.asNotchianItemStack();
    }

    static @NotNull NotchianItemStack convertNonNull(@NotNull NotchianItemStackLike message) {
        return Objects.requireNonNull(message.asNotchianItemStack(), "asNotchianItemStack() returned null");
    }

    @Nullable
    NotchianItemStack asNotchianItemStack();
}
