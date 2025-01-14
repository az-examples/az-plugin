package fr.nathan818.azplugin.common.nbt;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

@FunctionalInterface
public interface NotchianNbtTagCompoundLike {
    static @Nullable NotchianNbtTagCompound convert(@Nullable NotchianNbtTagCompoundLike message) {
        return message == null ? null : message.asNotchianNbtTagCompound();
    }

    static @NotNull NotchianNbtTagCompound convertNonNull(@NotNull NotchianNbtTagCompoundLike message) {
        return Objects.requireNonNull(message.asNotchianNbtTagCompound(), "asNotchianNbtTagCompound() returned null");
    }

    @Nullable
    NotchianNbtTagCompound asNotchianNbtTagCompound();
}
