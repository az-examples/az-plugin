package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AZBukkitShortcuts {

    public static @NotNull AZBukkitAPI az() {
        return AZBukkit.api();
    }

    @Contract(value = "null -> null; !null -> _", pure = true)
    public static @Nullable AZPlayer az(@Nullable Player player) {
        return AZBukkit.api().getClient(player);
    }
}
