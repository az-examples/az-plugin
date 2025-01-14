package fr.nathan818.azplugin.common;

import fr.nathan818.azplugin.common.network.AZPacketBufferAbstract;
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

    @Nullable
    default NotchianChatComponent readNotchianChatComponent(AZPacketBufferAbstract buf) {
        // TODO: Use AZ chat API component
        throw new UnsupportedOperationException("Platform does not support reading NotchianChatComponent");
    }

    @Nullable
    default NotchianItemStack readNotchianItemStack(AZPacketBufferAbstract buf) {
        // TODO: Use AZ stack API
        throw new UnsupportedOperationException("Platform does not support reading NotchianItemStack");
    }

    @Nullable
    default NotchianNbtTagCompound readNotchianNbtTagCompound(AZPacketBufferAbstract buf) {
        // TODO: Use AZ NBT API
        throw new UnsupportedOperationException("Platform does not support reading NotchianNbtTagCompound");
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
