package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import com.google.common.base.Function;
import java.util.Map;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.NBTBase;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.PacketDataSerializer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public static Map<String, NBTBase> getMetaItemUnhandledTags(ItemMeta itemMeta) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static Map<String, NBTBase> getNbtCompoundMap(NBTTagCompound compound) {
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

    public static void setNextWindowId(@NotNull EntityPlayer handle, int windowId) {
        throw new UnsupportedOperationException(); // implemented by BukkitAgentCompat1_9_R2
    }

    public static Function<Double, Double> wrapDamageBlocking(
        @NotNull Function<Double, Double> blocking,
        boolean isSwordBlocking
    ) {
        return f -> {
            if (f > 0.0D && isSwordBlocking) {
                double newF = (1.0D + f) * 0.5D;
                if (newF < f) {
                    return -(f - newF);
                }
            }
            return blocking.apply(f);
        };
    }

    public static void writeChunkData(
        @NotNull PacketDataSerializer buf,
        @Nullable EntityPlayer nmsPlayer,
        byte[] data,
        int sectionsMask,
        boolean complete
    ) {
        writeChunkDataFunction.writeChunkData(buf, nmsPlayer, data, sectionsMask, complete);
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
        WriteChunkDataFunction DEFAULT = (buf, nmsPlayer, data, sectionsMask, complete) -> {
            buf.d(data.length);
            buf.writeBytes(data);
        };

        void writeChunkData(
            @NotNull PacketDataSerializer buf,
            @Nullable EntityPlayer nmsPlayer,
            byte[] data,
            int sectionsMask,
            boolean complete
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
