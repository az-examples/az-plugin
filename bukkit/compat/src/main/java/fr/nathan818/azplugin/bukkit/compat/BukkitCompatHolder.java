package fr.nathan818.azplugin.bukkit.compat;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import java.util.logging.Level;
import org.jetbrains.annotations.Nullable;

final class BukkitCompatHolder {

    static @Nullable BukkitCompat instance;

    static {
        String compatClassName = CompatRegistry.detectCompatClass();
        if (compatClassName != null) {
            try {
                instance = (BukkitCompat) Class.forName(compatClassName).getDeclaredField("INSTANCE").get(null);
            } catch (Exception ex) {
                log(Level.WARNING, "Failed to initialize compat class: {0}", compatClassName, ex);
            }
        }
    }
}
