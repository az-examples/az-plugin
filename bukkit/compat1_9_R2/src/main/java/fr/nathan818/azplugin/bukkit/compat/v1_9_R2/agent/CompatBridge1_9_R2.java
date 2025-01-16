package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import java.util.function.BiFunction;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.ItemStack;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompatBridge1_9_R2 {

    public static BiFunction<EntityPlayer, ItemStack, ItemStack> rewriteItemStackOutFunction = (
        nmsPlayer,
        nmsItemStack
    ) -> nmsItemStack;
    public static BiFunction<EntityPlayer, ItemStack, ItemStack> rewriteItemStackInFunction = (
        nmsPlayer,
        nmsItemStack
    ) -> nmsItemStack;

    public static ItemStack getItemStackHandle(CraftItemStack itemStack) {
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

    public static ItemStack rewriteItemStackOut(@Nullable EntityPlayer nmsPlayer, @Nullable ItemStack nmsItemStack) {
        return rewriteItemStackOutFunction.apply(nmsPlayer, nmsItemStack);
    }

    public static ItemStack rewriteItemStackIn(@Nullable EntityPlayer nmsPlayer, @Nullable ItemStack nmsItemStack) {
        return rewriteItemStackInFunction.apply(nmsPlayer, nmsItemStack);
    }
}
