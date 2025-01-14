package fr.nathan818.azplugin.bukkit.item;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.common.item.NotchianItemStackLike;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public interface AZBukkitItemStack extends NotchianItemStack, NotchianItemStackLike {
    @Contract("null -> null; !null -> new")
    static @Nullable AZBukkitItemStack copyOf(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        return new AZBukkitItemStackImpl(AZBukkit.platform().asCraftCopy(itemStack));
    }

    @Contract("null -> null; !null -> new")
    static @Nullable AZBukkitItemStack mirrorOf(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        return new AZBukkitItemStackImpl(itemStack);
    }

    @NotNull
    ItemStack getBukkitItemStack();

    @Override
    default void write(NotchianPacketBuffer buf) {
        ItemStack itemStack = getBukkitItemStack();
        if (itemStack.getTypeId() <= 0) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(itemStack.getTypeId());
            buf.writeByte(itemStack.getAmount());
            buf.writeShort(itemStack.getDurability());
            NotchianNbtTagCompound tag = AZBukkit.platform().getItemStackTag(itemStack);
            buf.writeNotchianNbtTagCompound(tag);
        }
    }

    @Override
    default AZBukkitItemStack shallowClone() {
        return mirrorOf(getBukkitItemStack());
    }

    @Override
    default AZBukkitItemStack deepClone() {
        return copyOf(getBukkitItemStack());
    }

    @Override
    default AZBukkitItemStack asNotchianItemStack() {
        return this;
    }
}
