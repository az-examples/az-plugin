package fr.nathan818.azplugin.common;

import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;
import pactify.client.api.mcprotocol.model.SimpleNotchianChatComponent;
import pactify.client.api.mcprotocol.model.SimpleNotchianItemStack;

public interface AZPlatform<Player, Client extends AZClient> {
    @Contract(pure = true)
    @NotNull
    AZAPI<Player, Client> getAPI();

    @Contract(pure = true)
    @NotNull
    Logger getLogger();

    @Nullable
    default Object parseJson(@NotNull Reader reader) throws IOException {
        throw new UnsupportedOperationException("Platform does not support parsing JSON");
    }

    default NotchianChatComponent readNotchianChatComponent(@NotNull AZPacketBuffer buf) {
        return SimpleNotchianChatComponent.read(buf);
    }

    default void writeNotchianChatComponent(@NotNull AZPacketBuffer buf, NotchianChatComponent chatComponent) {
        NotchianChatComponent.write(buf, chatComponent);
    }

    @Nullable
    default NotchianItemStack readNotchianItemStack(@NotNull AZPacketBuffer buf) {
        return SimpleNotchianItemStack.read(buf);
    }

    default void writeNotchianItemStack(@NotNull AZPacketBuffer buf, @Nullable NotchianItemStack itemStack) {
        NotchianItemStack.write(buf, itemStack);
    }

    @Nullable
    default NotchianNbtTagCompound readNotchianNbtTagCompound(@NotNull AZPacketBuffer buf) {
        throw new UnsupportedOperationException("Platform does not support reading NotchianNbtTagCompound");
    }

    default void writeNotchianNbtTagCompound(
        @NotNull AZPacketBuffer buf,
        @Nullable NotchianNbtTagCompound nbtTagCompound
    ) {
        NotchianNbtTagCompound.write(buf, nbtTagCompound);
    }

    @Contract(pure = true)
    @NotNull
    static Logger logger() {
        AZPlatform<?, ?> api = AZ.platform;
        if (api != null) {
            return api.getLogger();
        } else {
            return AZFallbackLogger.getLogger();
        }
    }

    static void log(@NotNull Level level, @NotNull String message) {
        logger().log(level, message);
    }

    static void log(@NotNull Level level, @NotNull String message, @Nullable Throwable thrown) {
        logger().log(level, message, thrown);
    }

    static void log(@NotNull Level level, @NotNull String format, @NotNull Object... args) {
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            LogRecord lr = new LogRecord(level, format);
            lr.setParameters(args); // Throwable is not removed, it will be ignored
            lr.setThrown((Throwable) args[args.length - 1]);
            logger().log(lr);
        } else {
            logger().log(level, format, args);
        }
    }
}
