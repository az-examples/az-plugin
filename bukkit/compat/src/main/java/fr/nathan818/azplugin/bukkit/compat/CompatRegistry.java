package fr.nathan818.azplugin.bukkit.compat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

public class CompatRegistry {

    // This class MUST be public to avoid IllegalAccessError, as it will be accessed by different class loaders.

    private static final List<CompatEntry> KNOWN_COMPATS;

    static {
        String compatClass = System.getProperty("fr.nathan818.azplugin.bukkit.compat.provider", "");
        if (!compatClass.isEmpty()) {
            KNOWN_COMPATS = Collections.singletonList(new CompatEntry(compatClass, "java.lang.Object"));
        } else {
            KNOWN_COMPATS = Arrays.asList(
                new CompatEntry(
                    "fr.nathan818.azplugin.bukkit.compat.v1_8_R3.BukkitCompat1_8_R3",
                    "net.minecraft.server.v1_8_R3.MinecraftServer"
                ),
                new CompatEntry(
                    "fr.nathan818.azplugin.bukkit.compat.v1_9_R2.BukkitCompat1_9_R2",
                    "net.minecraft.server.v1_9_R2.MinecraftServer"
                )
            );
        }
    }

    public static @Nullable String detectCompatClass() {
        for (CompatEntry entry : KNOWN_COMPATS) {
            try {
                Class.forName(entry.detectClass);
                return entry.compatClass;
            } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }

    public static List<String> getAgentCompatClasses() {
        return KNOWN_COMPATS.stream()
            .map(entry -> guessAgentCompatClass(entry.compatClass))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static @Nullable String guessAgentCompatClass(@Nullable String compatClass) {
        if (compatClass == null) {
            return null;
        }
        String compatCassNamePrefix = ".BukkitCompat";
        int compatIndex = compatClass.lastIndexOf(compatCassNamePrefix);
        if (compatIndex == -1) {
            return null;
        }
        return (
            compatClass.substring(0, compatIndex) +
            ".agent.BukkitAgentCompat" +
            compatClass.substring(compatIndex + compatCassNamePrefix.length())
        );
    }

    @RequiredArgsConstructor
    private static class CompatEntry {

        private final String compatClass;
        private final String detectClass;
    }
}
