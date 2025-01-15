package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.compat.BukkitCompat;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.CompatBridge1_8_R3;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.material.MaterialRegistry1_8_R3;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.utils.java.CollectionsUtil;
import java.util.Collections;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BukkitCompat1_8_R3 implements BukkitCompat {

    public static final BukkitCompat1_8_R3 INSTANCE = new BukkitCompat1_8_R3();

    @Override
    public void registerBlock(@NotNull BlockDefinition definition) {
        MaterialRegistry1_8_R3.INSTANCE.registerBlock(definition);
    }

    @Override
    public void registerItem(@NotNull ItemDefinition definition) {
        MaterialRegistry1_8_R3.INSTANCE.registerItem(definition);
    }

    @Override
    public @NotNull PlayerConnection getPlayerConnection(@NotNull Player player) {
        NetworkManager networkManager = ((CraftPlayer) player).getHandle().playerConnection.networkManager;
        return new PlayerConnection1_8_R3(networkManager);
    }

    @Override
    public @NotNull ItemStack asCraftCopy(@NotNull ItemStack item) {
        return CraftItemStack.asCraftCopy(item);
    }

    @Override
    public @Nullable NotchianNbtTagCompound getItemStackTag(@NotNull ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack;
        if (itemStack instanceof CraftItemStack) {
            nmsItemStack = CompatBridge1_8_R3.getItemStackHandle((CraftItemStack) itemStack);
        } else {
            nmsItemStack = CraftItemStack.asNMSCopy(CraftItemStack.asCraftCopy(itemStack));
        }
        NBTTagCompound tag = (nmsItemStack != null) ? nmsItemStack.getTag() : null;
        return tag != null ? new NotchianNbtTagCompound1_8_R3(tag) : null;
    }

    @Override
    public BlockFace getEntityDirection(@NotNull Entity entity) {
        return Conversions1_8_R3.getBlockFace(((CraftEntity) entity).getHandle().getDirection());
    }

    @Override
    public int getMCProtocolVersion(Player player) {
        return 47;
    }

    @Override
    public int getActiveContainerWindowId(@NotNull Player bukkitPlayer) {
        EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
        return nmsPlayer.activeContainer.windowId;
    }

    @Override
    public void closeInventoryServerSide(@NotNull Player bukkitPlayer) {
        // EntityPlayer.closeInventory(), but without sending the packet
        EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
        CraftEventFactory.handleInventoryCloseEvent(nmsPlayer);
        nmsPlayer.p();
    }

    @Override
    public boolean isViewer(@NotNull Entity entity, @NotNull Player viewer) {
        EntityTrackerEntry nmsTrackerEntry = getTrackerEntry(entity);
        return nmsTrackerEntry != null && nmsTrackerEntry.trackedPlayers.contains(((CraftPlayer) viewer).getHandle());
    }

    @Override
    public Iterable<? extends @NotNull Player> getViewers(@NotNull Entity entity) {
        EntityTrackerEntry nmsTrackerEntry = getTrackerEntry(entity);
        if (nmsTrackerEntry == null) {
            return Collections.emptyList();
        }
        return CollectionsUtil.transformIterable(nmsTrackerEntry.trackedPlayers, EntityPlayer::getBukkitEntity);
    }

    private @Nullable EntityTrackerEntry getTrackerEntry(@NotNull Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        net.minecraft.server.v1_8_R3.World nmsWorld = nmsEntity.world;
        if (!(nmsWorld instanceof WorldServer)) {
            return null;
        }
        return ((WorldServer) nmsWorld).tracker.trackedEntities.get(nmsEntity.getId());
    }

    @Override
    public @Nullable AZEntity getAZEntity(@NotNull Entity entity) {
        if (entity instanceof CraftEntity) {
            return (AZEntity) CompatBridge1_8_R3.getAZEntity((CraftEntity) entity);
        }
        return null;
    }

    @Override
    public <T extends AZEntity> @Nullable T setAZEntity(
        @NotNull Entity entity,
        @Nullable Supplier<@NotNull T> azEntity
    ) {
        if (entity instanceof CraftEntity) {
            T value = (azEntity == null) ? null : azEntity.get();
            CompatBridge1_8_R3.setAZEntity((CraftEntity) entity, value);
            return value;
        }
        return null;
    }

    @Override
    public boolean isSneaking(@NotNull Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        return nmsEntity.isSneaking();
    }

    @Override
    public void setBboxScale(@NotNull Entity entity, float width, float height) {
        CompatBridge1_8_R3.setBboxScale((CraftEntity) entity, width, height);
    }
}
