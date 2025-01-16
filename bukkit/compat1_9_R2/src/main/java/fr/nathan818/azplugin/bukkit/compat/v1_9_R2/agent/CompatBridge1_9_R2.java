package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.PacketDataSerializer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompatBridge1_9_R2 {

    public static WriteChunkDataFunction writeChunkDataFunction = WriteChunkDataFunction.DEFAULT;
    public static RewriteBlockStateFunction rewriteBlockStateFunction = RewriteBlockStateFunction.DEFAULT;
    public static RewriteItemStackFunction rewriteItemStackOutFunction = RewriteItemStackFunction.DEFAULT;
    public static RewriteItemStackFunction rewriteItemStackInFunction = RewriteItemStackFunction.DEFAULT;

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

    public static void writeChunkData(
        @NotNull PacketDataSerializer buf,
        @Nullable EntityPlayer nmsPlayer,
        byte[] data,
        boolean complete,
        int sectionsMask
    ) {
        writeChunkDataFunction.writeChunkData(buf, nmsPlayer, data, complete, sectionsMask);
    }

    public static int rewriteBlockState(int blockStateId, @Nullable EntityPlayer nmsPlayer) {
        return rewriteBlockStateFunction.rewriteBlockState(blockStateId, nmsPlayer);
    }

    public static ItemStack rewriteItemStackOut(@Nullable EntityPlayer nmsPlayer, @Nullable ItemStack nmsItemStack) {
        return rewriteItemStackOutFunction.rewriteItemStack(nmsPlayer, nmsItemStack);
    }

    public static ItemStack rewriteItemStackIn(@Nullable EntityPlayer nmsPlayer, @Nullable ItemStack nmsItemStack) {
        return rewriteItemStackInFunction.rewriteItemStack(nmsPlayer, nmsItemStack);
    }

    public interface WriteChunkDataFunction {
        WriteChunkDataFunction DEFAULT = (buf, nmsPlayer, data, complete, sectionsMask) -> {
            buf.d(data.length);
            buf.writeBytes(data);
        };

        void writeChunkData(
            @NotNull PacketDataSerializer buf,
            @Nullable EntityPlayer nmsPlayer,
            byte[] data,
            boolean complete,
            int sectionsMask
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
