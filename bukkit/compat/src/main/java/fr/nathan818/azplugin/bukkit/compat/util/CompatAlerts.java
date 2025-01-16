package fr.nathan818.azplugin.bukkit.compat.util;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.util.logging.Level;

public class CompatAlerts {

    // Non-atomic booleans are used here; it doesn't matter if a warning is shown more than once
    private static boolean NON_NETTY_PACKET_BUFFER_SEND = false;
    private static boolean NON_NETTY_PACKET_BUFFER_READ = false;
    private static boolean NON_NETTY_PACKET_BUFFER_WRITE = false;

    public static void nonNettyPacketBufferSend() {
        if (!NON_NETTY_PACKET_BUFFER_SEND) {
            NON_NETTY_PACKET_BUFFER_SEND = true;
            log(
                Level.WARNING,
                "Someone used a non-Netty packet buffer to send data! Performance may be affected. This message will only be shown once.",
                new RuntimeException("Trace")
            );
        }
    }

    public static void nonNettyPacketBufferRead() {
        if (!NON_NETTY_PACKET_BUFFER_READ) {
            NON_NETTY_PACKET_BUFFER_READ = true;
            log(
                Level.WARNING,
                "Someone used a non-Netty packet buffer to read data! Performance may be affected. This message will only be shown once.",
                new RuntimeException("Trace")
            );
        }
    }

    public static void nonNettyPacketBufferWrite() {
        if (!NON_NETTY_PACKET_BUFFER_WRITE) {
            NON_NETTY_PACKET_BUFFER_WRITE = true;
            log(
                Level.WARNING,
                "Someone used a non-Netty packet buffer to write data! Performance may be affected. This message will only be shown once.",
                new RuntimeException("Trace")
            );
        }
    }
}
