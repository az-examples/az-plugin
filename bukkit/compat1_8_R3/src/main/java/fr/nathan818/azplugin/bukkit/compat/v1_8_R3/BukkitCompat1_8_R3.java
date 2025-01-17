package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;

import fr.nathan818.azplugin.bukkit.compat.BukkitCompat;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterBlockResult;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterItemResult;
import fr.nathan818.azplugin.bukkit.compat.network.BlockRewriter;
import fr.nathan818.azplugin.bukkit.compat.network.ItemStackRewriter;
import fr.nathan818.azplugin.bukkit.compat.network.NettyPacketBuffer;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.compat.util.CompatAlerts;
import fr.nathan818.azplugin.bukkit.compat.util.NetworkUtil;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.CompatBridge1_8_R3;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.itemstack.BukkitItemStackProxy1_8_R3;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.itemstack.NMSItemStackProxy1_8_R3;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.material.MaterialRegistry1_8_R3;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.AZ;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import fr.nathan818.azplugin.common.utils.java.CollectionsUtil;
import fr.nathan818.azplugin.common.utils.java.IOConsumer;
import fr.nathan818.azplugin.common.utils.java.IOFunction;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
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

    public static @Nullable ItemStack asCraftItemStack(@Nullable net.minecraft.server.v1_8_R3.ItemStack nmsItemStack) {
        return nmsItemStack == null ? null : CraftItemStack.asCraftMirror(nmsItemStack);
    }

    public static @Nullable net.minecraft.server.v1_8_R3.ItemStack asNMSItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack instanceof CraftItemStack) {
            return CompatBridge1_8_R3.getItemStackHandle((CraftItemStack) itemStack);
        } else {
            return CraftItemStack.asNMSCopy(CraftItemStack.asCraftCopy(itemStack));
        }
    }

    public static @NotNull PacketDataSerializer toNMSPacketBuffer(@NotNull AZPacketBuffer buf) {
        if (buf instanceof NettyPacketBuffer) {
            return new PacketDataSerializer(((NettyPacketBuffer) buf).getNettyBuffer());
        } else {
            CompatAlerts.nonNettyPacketBufferSend();
            return new PacketDataSerializer(Unpooled.wrappedBuffer(buf.toByteArray()));
        }
    }

    @SneakyThrows(IOException.class)
    private static <T> T readFromNMSPacketBuffer(
        @NotNull AZPacketBuffer buf,
        IOFunction<? super PacketDataSerializer, T> fn
    ) {
        if (buf instanceof NettyPacketBuffer) {
            PacketDataSerializer nmsBuf = new PacketDataSerializer(((NettyPacketBuffer) buf).getNettyBuffer());
            return fn.apply(nmsBuf);
        } else {
            CompatAlerts.nonNettyPacketBufferRead();
            PacketDataSerializer nmsBuf = new PacketDataSerializer(Unpooled.wrappedBuffer(buf.toByteArray()));
            T ret = fn.apply(nmsBuf);
            int bytesRead = nmsBuf.readerIndex();
            if (bytesRead > 0) {
                buf.readBytes(new byte[bytesRead]); // TODO: Use skipBytes when available
            }
            return ret;
        }
    }

    @SneakyThrows(IOException.class)
    private static void writeToNMSPacketBuffer(
        @NotNull AZPacketBuffer buf,
        IOConsumer<? super PacketDataSerializer> fn
    ) {
        if (buf instanceof NettyPacketBuffer) {
            PacketDataSerializer nmsBuf = new PacketDataSerializer(((NettyPacketBuffer) buf).getNettyBuffer());
            fn.accept(nmsBuf);
        } else {
            CompatAlerts.nonNettyPacketBufferWrite();
            PacketDataSerializer nmsBuf = new PacketDataSerializer(Unpooled.wrappedBuffer(buf.toByteArray()));
            fn.accept(nmsBuf);
            int bytesWritten = nmsBuf.writerIndex();
            if (bytesWritten > 0) {
                byte[] bytes = new byte[bytesWritten];
                nmsBuf.readBytes(bytes);
                buf.writeBytes(bytes);
            }
        }
    }

    @Override
    public RegisterBlockResult registerBlock(@NotNull BlockDefinition definition) {
        return MaterialRegistry1_8_R3.INSTANCE.registerBlock(definition);
    }

    @Override
    public RegisterItemResult registerItem(@NotNull ItemDefinition definition) {
        return MaterialRegistry1_8_R3.INSTANCE.registerItem(definition);
    }

    @Override
    public void setBlockRewriter(@NotNull BlockRewriter rewriter) {
        CompatBridge1_8_R3.writeChunkDataFunction = (buf, nmsPlayer, data, sectionsMask, complete, prefixLen) -> {
            AZPlayer player = (nmsPlayer == null) ? null : az(nmsPlayer.getBukkitEntity());
            int[] rewritePalette = rewriter.getRewriteBlockOutPalette(AZNetworkContext.of(player));
            ChunkCodec1_8_R3.writeChunkData(buf, data, sectionsMask, complete, prefixLen, rewritePalette);
        };
        CompatBridge1_8_R3.rewriteBlockStateFunction = (blockStateId, nmsPlayer) -> {
            AZPlayer player = (nmsPlayer == null) ? null : az(nmsPlayer.getBukkitEntity());
            int[] rewritePalette = rewriter.getRewriteBlockOutPalette(AZNetworkContext.of(player));
            if (blockStateId >= 0 && blockStateId < rewritePalette.length) {
                return rewritePalette[blockStateId];
            }
            return blockStateId;
        };
    }

    @Override
    public void setItemStackRewriter(@NotNull ItemStackRewriter rewriter) {
        CompatBridge1_8_R3.rewriteItemStackOutFunction = (nmsPlayer, nmsItemStack) -> {
            if (nmsItemStack == null || nmsItemStack.getItem() == null) {
                return null;
            }
            AZPlayer player = (nmsPlayer == null) ? null : az(nmsPlayer.getBukkitEntity());
            NMSItemStackProxy1_8_R3 proxy = new NMSItemStackProxy1_8_R3(nmsItemStack, true);
            rewriter.rewriteItemStackOut(AZNetworkContext.of(player), proxy);
            return proxy.getForRead();
        };
        CompatBridge1_8_R3.rewriteItemStackInFunction = (nmsPlayer, nmsItemStack) -> {
            if (nmsItemStack == null || nmsItemStack.getItem() == null) {
                return null;
            }
            AZPlayer player = (nmsPlayer == null) ? null : az(nmsPlayer.getBukkitEntity());
            NMSItemStackProxy1_8_R3 proxy = new NMSItemStackProxy1_8_R3(nmsItemStack, false);
            rewriter.rewriteItemStackIn(AZNetworkContext.of(player), proxy);
            return proxy.getForRead();
        };
    }

    @Override
    public @NotNull PlayerConnection initPlayerConnection(@NotNull Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        NetworkManager networkManager = nmsPlayer.playerConnection.networkManager;
        NetworkUtil.injectPlayerInHandlers(networkManager.channel.pipeline(), nmsPlayer);
        return new PlayerConnection1_8_R3(networkManager);
    }

    @Override
    public @Nullable ItemStack asCraftCopy(@Nullable ItemStack item) {
        return item == null ? null : CraftItemStack.asCraftCopy(item);
    }

    @Override
    public @Nullable ItemStack createItemStack(
        int itemId,
        int count,
        int damage,
        @Nullable NotchianNbtTagCompound tag
    ) {
        Item item = Item.getById(itemId);
        if (item == null) {
            return null;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = new net.minecraft.server.v1_8_R3.ItemStack(
            item,
            count,
            damage
        );
        nmsItemStack.setTag(asNMSTagCompound(tag));
        return asCraftItemStack(nmsItemStack);
    }

    @Override
    public @Nullable NotchianNbtTagCompound getItemStackTag(@Nullable ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = asNMSItemStack(itemStack);
        NBTTagCompound nmsTag = (nmsItemStack == null) ? null : nmsItemStack.getTag();
        return (nmsTag == null) ? null : new NotchianNbtTagCompound1_8_R3(nmsTag);
    }

    @Override
    public @Nullable ItemStackProxy getItemStackProxy(@Nullable ItemStack itemStack, boolean copyOnWrite) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack instanceof CraftItemStack) {
            return new NMSItemStackProxy1_8_R3(
                CompatBridge1_8_R3.getItemStackHandle((CraftItemStack) itemStack),
                copyOnWrite
            );
        } else {
            return new BukkitItemStackProxy1_8_R3(itemStack, copyOnWrite);
        }
    }

    private @Nullable NBTTagCompound asNMSTagCompound(@Nullable NotchianNbtTagCompound tag) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof NotchianNbtTagCompound1_8_R3) {
            return ((NotchianNbtTagCompound1_8_R3) tag).getHandle();
        }
        try (AZPacketBuffer buf = AZ.platform().createHeapPacketBuffer(null)) {
            tag.write(buf);
            return readFromNMSPacketBuffer(buf, PacketDataSerializer::h);
        }
    }

    @Override
    public @Nullable ItemStack readItemStack(@NotNull AZPacketBuffer buf) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = readFromNMSPacketBuffer(buf, PacketDataSerializer::i);
        return asCraftItemStack(nmsItemStack);
    }

    @Override
    public void writeItemStack(@NotNull AZPacketBuffer buf, @Nullable ItemStack itemStack) {
        writeToNMSPacketBuffer(buf, nmsBuf -> nmsBuf.a(asNMSItemStack(itemStack)));
    }

    @Override
    public @Nullable NotchianNbtTagCompound readNotchianNbtTagCompound(@NotNull AZPacketBuffer buf) {
        NBTTagCompound nmsTag = readFromNMSPacketBuffer(buf, PacketDataSerializer::h);
        return nmsTag == null ? null : new NotchianNbtTagCompound1_8_R3(nmsTag);
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
    public int closeActiveContainerServerSide(@NotNull Player bukkitPlayer) {
        // EntityPlayer.closeInventory(), but without sending the packet
        EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
        int windowId = nmsPlayer.activeContainer.windowId;
        CraftEventFactory.handleInventoryCloseEvent(nmsPlayer);
        nmsPlayer.p();
        return windowId;
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

    @Override
    public void setNextWindowId(@NotNull Player player, int windowId) {
        CompatBridge1_8_R3.setNextWindowId(((CraftPlayer) player).getHandle(), windowId);
    }
}
