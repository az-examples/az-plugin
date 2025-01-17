package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AZBukkitShortcuts {

    public static @NotNull AZBukkitAPI az() {
        return AZBukkit.api();
    }

    @Contract(value = "null -> null")
    public static @Nullable AZPlayer az(@Nullable Player player) {
        return AZBukkit.api().getClient(player);
    }

    @Contract(value = "null -> null")
    public static @Nullable AZEntity az(@Nullable Entity entity) {
        return AZBukkit.api().getEntity(entity);
    }

    @Contract(value = "null -> null")
    public static @Nullable AZEntity azIfPresent(@Nullable Entity entity) {
        return AZBukkit.api().getEntityIfPresent(entity);
    }
}
