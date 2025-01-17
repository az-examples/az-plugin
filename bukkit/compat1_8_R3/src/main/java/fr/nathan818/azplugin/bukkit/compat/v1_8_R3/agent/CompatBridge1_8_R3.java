package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompatBridge1_8_R3 {

    public static WriteChunkDataFunction writeChunkDataFunction = WriteChunkDataFunction.DEFAULT;
    public static RewriteBlockStateFunction rewriteBlockStateFunction = RewriteBlockStateFunction.DEFAULT;
    public static RewriteItemStackFunction rewriteItemStackOutFunction = RewriteItemStackFunction.DEFAULT;
    public static RewriteItemStackFunction rewriteItemStackInFunction = RewriteItemStackFunction.DEFAULT;

    public static ItemStack getItemStackHandle(CraftItemStack itemStack) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static Object getAZEntity(@NotNull CraftEntity entity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static void setAZEntity(@NotNull CraftEntity entity, @Nullable Object azEntity) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static void setBboxScale(@NotNull CraftEntity entity, float width, float height) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_8_R3
    }

    public static void setNextWindowId(@NotNull EntityPlayer handle, int windowId) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static void writeChunkData(
        @NotNull PacketDataSerializer buf,
        @Nullable EntityPlayer nmsPlayer,
        byte[] data,
        int sectionsMask,
        boolean complete,
        boolean prefixLen
    ) {
        writeChunkDataFunction.writeChunkData(buf, nmsPlayer, data, sectionsMask, complete, prefixLen);
    }

    public static int rewriteBlockState(int blockStateId, @Nullable EntityPlayer nmsPlayer) {
        return rewriteBlockStateFunction.rewriteBlockState(blockStateId, nmsPlayer);
    }

    public static @Nullable ItemStack rewriteItemStackOut(
        @Nullable EntityPlayer nmsPlayer,
        @Nullable ItemStack nmsItemStack
    ) {
        return rewriteItemStackOutFunction.rewriteItemStack(nmsPlayer, nmsItemStack);
    }

    public static @Nullable ItemStack rewriteItemStackIn(
        @Nullable EntityPlayer nmsPlayer,
        @Nullable ItemStack nmsItemStack
    ) {
        return rewriteItemStackInFunction.rewriteItemStack(nmsPlayer, nmsItemStack);
    }

    public interface WriteChunkDataFunction {
        WriteChunkDataFunction DEFAULT = (buf, nmsPlayer, data, sectionsMask, complete, prefixLen) -> {
            if (prefixLen) {
                buf.a(data);
            } else {
                buf.writeBytes(data);
            }
        };

        void writeChunkData(
            @NotNull PacketDataSerializer buf,
            @Nullable EntityPlayer nmsPlayer,
            byte[] data,
            int sectionsMask,
            boolean complete,
            boolean prefixLen
        );
    }

    public interface RewriteBlockStateFunction {
        RewriteBlockStateFunction DEFAULT = (blockStateId, nmsPlayer) -> blockStateId;

        int rewriteBlockState(int blockStateId, @Nullable EntityPlayer nmsPlayer);
    }

    public interface RewriteItemStackFunction {
        RewriteItemStackFunction DEFAULT = (nmsPlayer, nmsItemStack) -> nmsItemStack;

        @Nullable
        ItemStack rewriteItemStack(@Nullable EntityPlayer nmsPlayer, @Nullable ItemStack nmsItemStack);
    }
}
