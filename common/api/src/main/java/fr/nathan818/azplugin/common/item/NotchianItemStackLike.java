package fr.nathan818.azplugin.common.item;

import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianItemStack;

@FunctionalInterface
public interface NotchianItemStackLike {
    @Contract("null -> null; !null -> !null")
    static @Nullable NotchianItemStackLike of(@Nullable NotchianItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack instanceof NotchianItemStackLike) {
            return (NotchianItemStackLike) itemStack;
        }
        return () -> itemStack;
    }

    static @Nullable NotchianItemStack convert(@Nullable NotchianItemStackLike itemStack) {
        return itemStack == null ? null : itemStack.asNotchianItemStack();
    }

    static @NotNull NotchianItemStack convertNonNull(@NotNull NotchianItemStackLike itemStack) {
        return Objects.requireNonNull(itemStack.asNotchianItemStack(), "asNotchianItemStack() returned null");
    }

    @Nullable
    NotchianItemStack asNotchianItemStack();
}
