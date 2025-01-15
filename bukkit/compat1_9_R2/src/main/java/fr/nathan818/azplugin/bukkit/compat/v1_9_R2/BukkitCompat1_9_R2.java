package fr.nathan818.azplugin.bukkit.compat.v1_9_R2;

import fr.nathan818.azplugin.bukkit.compat.BukkitCompat;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.CompatBridge1_9_R2;
import fr.nathan818.azplugin.bukkit.compat.v1_9_R2.material.MaterialRegistry1_9_R2;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.common.utils.java.CollectionsUtil;
import java.util.Collections;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.EntityTrackerEntry;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NetworkManager;
import net.minecraft.server.v1_9_R2.WorldServer;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BukkitCompat1_9_R2 implements BukkitCompat {

    public static final BukkitCompat1_9_R2 INSTANCE = new BukkitCompat1_9_R2();

    @Override
    public void registerBlock(@NotNull BlockDefinition definition) {
        MaterialRegistry1_9_R2.INSTANCE.registerBlock(definition);
    }

    @Override
    public void registerItem(@NotNull ItemDefinition definition) {
        MaterialRegistry1_9_R2.INSTANCE.registerItem(definition);
    }

    @Override
    public @NotNull PlayerConnection getPlayerConnection(@NotNull Player player) {
        NetworkManager networkManager = ((CraftPlayer) player).getHandle().playerConnection.networkManager;
        return new PlayerConnection1_9_R2(networkManager);
    }

    @Override
    public @NotNull ItemStack asCraftCopy(@NotNull ItemStack item) {
        return CraftItemStack.asCraftCopy(item);
    }

    @Override
    public @Nullable NotchianNbtTagCompound getItemStackTag(@NotNull ItemStack itemStack) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack;
        if (itemStack instanceof CraftItemStack) {
            nmsItemStack = CompatBridge1_9_R2.getItemStackHandle((CraftItemStack) itemStack);
        } else {
            nmsItemStack = CraftItemStack.asNMSCopy(CraftItemStack.asCraftCopy(itemStack));
        }
        NBTTagCompound tag = (nmsItemStack != null) ? nmsItemStack.getTag() : null;
        return tag != null ? new NotchianNbtTagCompound1_9_R2(tag) : null;
    }

    @Override
    public BlockFace getEntityDirection(@NotNull Entity entity) {
        return Conversions1_9_R2.getBlockFace(((CraftEntity) entity).getHandle().getDirection());
    }

    @Override
    public int getMCProtocolVersion(Player player) {
        return 110;
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
        nmsPlayer.s();
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
        net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        net.minecraft.server.v1_9_R2.World nmsWorld = nmsEntity.world;
        if (!(nmsWorld instanceof WorldServer)) {
            return null;
        }
        return ((WorldServer) nmsWorld).tracker.trackedEntities.get(nmsEntity.getId());
    }

    @Override
    public @Nullable AZEntity getAZEntity(@NotNull Entity entity) {
        if (entity instanceof CraftEntity) {
            return (AZEntity) CompatBridge1_9_R2.getAZEntity((CraftEntity) entity);
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
            CompatBridge1_9_R2.setAZEntity((CraftEntity) entity, value);
            return value;
        }
        return null;
    }

    @Override
    public boolean isSneaking(@NotNull Entity entity) {
        net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        return nmsEntity.isSneaking();
    }

    @Override
    public boolean isElytraFlying(@NotNull Entity entity) {
        net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        return nmsEntity instanceof EntityLiving && ((EntityLiving) nmsEntity).cC();
    }

    @Override
    public void setBboxScale(@NotNull Entity entity, float width, float height) {
        CompatBridge1_9_R2.setBboxScale((CraftEntity) entity, width, height);
    }
}
