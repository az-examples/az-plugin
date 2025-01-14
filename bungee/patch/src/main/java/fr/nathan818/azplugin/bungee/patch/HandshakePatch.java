package fr.nathan818.azplugin.bungee.patch;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.Contract;

/**
 * When IP Forwarding is enabled, BungeeCord don't send the "extra data in handshake" to the spigot server... But it is
 * required to detect the AZ Launcher from Spigot. So we fix that!
 */
public class HandshakePatch implements Listener {

    private static final Pattern PACTIFY_HOSTNAME_PATTERN = Pattern.compile("\u0000(PAC[0-9A-F]{5})\u0000");

    private static final Class<?> initialHandlerClass;
    private static final Method getExtraDataInHandshakeMethod;

    static {
        try {
            initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");
            getExtraDataInHandshakeMethod = initialHandlerClass.getDeclaredMethod("getExtraDataInHandshake");
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new RuntimeException("Failed to initialize reflection", ex);
        }
    }

    @Contract(value = "null -> null; !null -> !null", pure = true)
    @SneakyThrows(ReflectiveOperationException.class)
    private static String getExtraDataInHandshake(PendingConnection connection) {
        Object initialHandler = initialHandlerClass.cast(connection);
        String extraDataInHandshake = (String) getExtraDataInHandshakeMethod.invoke(initialHandler);
        return extraDataInHandshake;
    }

    @EventHandler
    public void onPlayerHandshake(PlayerHandshakeEvent event) {
        Matcher m = PACTIFY_HOSTNAME_PATTERN.matcher(getExtraDataInHandshake(event.getConnection()));
        if (m.find()) {
            // Send the AZ Launcher handshake using \u0002 instead of \u0000 (to avoid conflicts with the IP Forwarding)
            event.getHandshake().setHost(event.getHandshake().getHost() + "\u0002" + m.group(1) + "\u0002");
        }
    }
}
