package fr.nathan818.azplugin.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Access point to common (platform-independent) AZPlugin singletons.
 * <p>
 * <b>IMPORTANT:</b> If you use Bukkit/Spigot, look at {@link fr.nathan818.azplugin.bukkit.AZBukkit}
 * instead.
 *
 * @see AZAPI
 */
@SuppressWarnings({ "JavadocReference", "rawtypes" })
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AZ {

    @Contract(pure = true)
    public static @NotNull AZAPI api() {
        return platform().getAPI();
    }

    static AZPlatform platform;

    public static AZPlatform platform() {
        AZPlatform ret = platform;
        if (ret == null) {
            throw new IllegalStateException("AZPlatform is not initialized yet");
        }
        return ret;
    }

    /**
     * @deprecated Used internally to initialize the platform.
     */
    @Deprecated
    public static void init(AZPlatform platform) {
        synchronized (AZ.class) {
            if (AZ.platform != null) {
                throw new IllegalStateException("AZPlatform is already initialized");
            }
            AZ.platform = platform;
        }
    }
}
