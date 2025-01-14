package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.common.AZ;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Access point to Bukkit AZPlugin singletons.
 *
 * @see AZBukkitAPI
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AZBukkit {

    public static AZBukkitAPI api() {
        return platform().getAPI();
    }

    private static AZBukkitPlatform platform;

    public static AZBukkitPlatform platform() {
        AZBukkitPlatform ret = platform;
        if (ret == null) {
            throw new IllegalStateException("AZBukkitPlatform is not initialized yet");
        }
        return ret;
    }

    /**
     * @deprecated Used internally to initialize the platform.
     */
    @Deprecated
    public static void init(AZBukkitPlatform platform) {
        synchronized (AZ.class) {
            AZ.init(platform);
            AZBukkit.platform = platform;
        }
    }
}
