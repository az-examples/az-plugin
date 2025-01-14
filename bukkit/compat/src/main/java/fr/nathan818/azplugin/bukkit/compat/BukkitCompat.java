package fr.nathan818.azplugin.bukkit.compat;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.compat.util.MathUtil;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public interface BukkitCompat {
    static BukkitCompat compat() {
        return BukkitCompatHolder.instance;
    }

    default void onLoad() {}

    default void registerBlock(@NotNull BlockDefinition definition) {
        log(
            Level.WARNING,
            "Unable to register block {0} when using {1}",
            definition.getBukkitName(),
            getClass().getSimpleName()
        );
    }

    default void registerItem(@NotNull ItemDefinition definition) {
        log(
            Level.WARNING,
            "Unable to register item {0} when using {1}",
            definition.getBukkitName(),
            getClass().getSimpleName()
        );
    }

    @NotNull
    PlayerConnection getPlayerConnection(@NotNull Player player);

    @Nullable
    default String getLoginHostname(@NotNull Player player) {
        return null;
    }

    @NotNull
    default ItemStack asCraftCopy(@NotNull ItemStack item) {
        return item.clone();
    }

    @Nullable
    default NotchianNbtTagCompound getItemStackTag(@NotNull ItemStack itemStack) {
        return null;
    }

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

    default int getActiveContainerWindowId(@NotNull Player bukkitPlayer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void closeInventoryServerSide(@NotNull Player bukkitPlayer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default boolean isViewer(@NotNull Entity entity, @NotNull Player viewer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default Iterable<? extends @NotNull Player> getViewers(@NotNull Entity entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    default AZEntity getAZEntity(@NotNull Entity entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    default <T extends AZEntity> T setAZEntity(@NotNull Entity entity, @Nullable Supplier<@NotNull T> azEntity) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
