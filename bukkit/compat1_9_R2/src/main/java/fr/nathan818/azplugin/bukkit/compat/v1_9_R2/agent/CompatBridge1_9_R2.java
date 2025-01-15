package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompatBridge1_9_R2 {

    public static net.minecraft.server.v1_9_R2.ItemStack getItemStackHandle(CraftItemStack itemStack) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static Object getAZEntity(@NotNull CraftEntity entity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static void setAZEntity(@NotNull CraftEntity entity, @Nullable Object azEntity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static void setBboxScale(@NotNull CraftEntity entity, float width, float height) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }
}
