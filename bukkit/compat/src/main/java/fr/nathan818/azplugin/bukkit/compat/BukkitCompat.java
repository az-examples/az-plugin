package fr.nathan818.azplugin.bukkit.compat;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterBlockResult;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterItemResult;
import fr.nathan818.azplugin.bukkit.compat.network.BlockRewriter;
import fr.nathan818.azplugin.bukkit.compat.network.ItemStackRewriter;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.compat.util.MathUtil;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public interface BukkitCompat {
    static BukkitCompat compat() {
        return BukkitCompatHolder.instance;
    }

    default void onLoad() {}

    default @Nullable RegisterBlockResult registerBlock(@NotNull BlockDefinition definition) {
        log(
            Level.WARNING,
            "Unable to register block {0} when using {1}",
            definition.getBukkitName(),
            getClass().getSimpleName()
        );
        return null;
    }

    default @Nullable RegisterItemResult registerItem(@NotNull ItemDefinition definition) {
        log(
            Level.WARNING,
            "Unable to register item {0} when using {1}",
            definition.getBukkitName(),
            getClass().getSimpleName()
        );
        return null;
    }

    default void setBlockRewriter(@NotNull BlockRewriter rewriter) {
        log(
            Level.WARNING,
            "Unable to register block rewriter {0} when using {1}",
            rewriter.getClass().getSimpleName(),
            getClass().getSimpleName()
        );
    }

    default void setItemStackRewriter(@NotNull ItemStackRewriter rewriter) {
        log(
            Level.WARNING,
            "Unable to register item stack rewriter {0} when using {1}",
            rewriter.getClass().getSimpleName(),
            getClass().getSimpleName()
        );
    }

    @NotNull
    PlayerConnection initPlayerConnection(@NotNull Player player);

    @Nullable
    default String getLoginHostname(@NotNull Player player) {
        return null;
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    default ItemStack asCraftCopy(@Nullable ItemStack item) {
        return item == null ? null : item.clone();
    }

    @Nullable
    ItemStack createItemStack(int itemId, int count, int damage, @Nullable NotchianNbtTagCompound tag);

    @Contract("null -> null")
    @Nullable
    NotchianNbtTagCompound getItemStackTag(@Nullable ItemStack itemStack);

    @Contract("null, _ -> null; !null, _ -> !null")
    @Nullable
    ItemStackProxy getItemStackProxy(@Nullable ItemStack itemStack, boolean copyOnWrite);

    @Nullable
    ItemStack readItemStack(@NotNull AZPacketBuffer buf);

    void writeItemStack(@NotNull AZPacketBuffer buf, @Nullable ItemStack itemStack);

    @Nullable
    NotchianNbtTagCompound readNotchianNbtTagCompound(@NotNull AZPacketBuffer buf);

    @Nullable
    default BlockFace getEntityDirection(@NotNull Entity entity) {
        float yaw = entity.getLocation().getYaw();
        switch (Math.abs(MathUtil.floor((double) ((yaw * 4.0F) / 360.0F) + (double) 0.5F) & 3)) {
            case 0:
                return BlockFace.SOUTH;
            case 1:
                return BlockFace.WEST;
            case 2:
                return BlockFace.NORTH;
            default:
                return BlockFace.EAST;
        }
    }

    default int getMCProtocolVersion(Player player) {
        return -1;
    }

    default int getDefaultChatMessageMaxSize() {
        return 100;
    }

    default int closeActiveContainerServerSide(@NotNull Player bukkitPlayer) {
        return -1;
    }

    boolean isViewer(@NotNull Entity entity, @NotNull Player viewer);

    Iterable<? extends @NotNull Player> getViewers(@NotNull Entity entity);

    @Nullable
    AZEntity getAZEntity(@NotNull Entity entity);

    @Nullable
    <T extends AZEntity> T setAZEntity(@NotNull Entity entity, @Nullable Supplier<@NotNull T> azEntity);

    default boolean isSneaking(@NotNull Entity entity) {
        return entity instanceof Player && ((Player) entity).isSneaking();
    }

    default boolean isSleeping(@NotNull Entity entity) {
        return entity instanceof HumanEntity && ((HumanEntity) entity).isSleeping();
    }

    default boolean isElytraFlying(@NotNull Entity entity) {
        return false;
    }

    default void setBboxScale(@NotNull Entity entity, float width, float height) {
        // Do nothing when not implemented
    }

    default void setNextWindowId(@NotNull Player player, int windowId) {
        // Do nothing when not implemented
    }
}
