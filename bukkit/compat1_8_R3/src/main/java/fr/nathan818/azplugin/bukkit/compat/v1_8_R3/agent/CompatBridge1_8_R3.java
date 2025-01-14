package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompatBridge1_8_R3 {

    public static net.minecraft.server.v1_8_R3.ItemStack getItemStackHandle(CraftItemStack itemStack) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static Object getAZEntity(@NotNull CraftEntity entity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static void setAZEntity(@NotNull CraftEntity entity, @Nullable Object azEntity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }
}
