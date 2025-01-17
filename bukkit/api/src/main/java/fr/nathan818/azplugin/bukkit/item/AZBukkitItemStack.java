package fr.nathan818.azplugin.bukkit.item;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.common.item.NotchianItemStackLike;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Contract("null -> null; !null -> _")
    static @Nullable ItemStack mirrorBukkitItemStack(@Nullable NotchianItemStackLike notchianItemStack) {
        NotchianItemStack that = NotchianItemStackLike.convert(notchianItemStack);
        if (that == null) {
            return null;
        }
        if (that instanceof AZBukkitItemStack) {
            return ((AZBukkitItemStack) that).getBukkitItemStack();
        }
        return AZBukkit.platform().createItemStack(that.getItemId(), that.getCount(), that.getDamage(), that.getTag());
    }

    @NotNull
    ItemStack getBukkitItemStack();

    @Override
    default int getItemId() {
        return getBukkitItemStack().getTypeId();
    }

    @Override
    default int getCount() {
        return getBukkitItemStack().getAmount();
    }

    @Override
    default int getDamage() {
        return getBukkitItemStack().getDurability();
    }

    @Override
    @Nullable
    default NotchianNbtTagCompound getTag() {
        return AZBukkit.platform().getItemStackTag(getBukkitItemStack());
    }

    @Override
    default @NotNull AZBukkitItemStack shallowClone() {
        return mirrorOf(getBukkitItemStack());
    }

    @Override
    default @NotNull AZBukkitItemStack deepClone() {
        return copyOf(getBukkitItemStack());
    }

    @Override
    default AZBukkitItemStack asNotchianItemStack() {
        return this;
    }

    static String toString(@Nullable AZBukkitItemStack item) {
        if (item == null) {
            return "null";
        }
        ItemStack itemStack = item.getBukkitItemStack();
        NotchianNbtTagCompound tag = AZBukkit.platform().getItemStackTag(itemStack);
        return (
            "AZBukkitItemStack[" +
            itemStack.getTypeId() +
            (":" + itemStack.getDurability()) +
            ("*" + itemStack.getAmount()) +
            (tag == null ? "" : " " + tag) +
            "]"
        );
    }

    static boolean equals(@Nullable AZBukkitItemStack a, @Nullable Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || !(b instanceof AZBukkitItemStack)) {
            return false;
        }
        AZBukkitItemStack that = (AZBukkitItemStack) b;
        return Objects.equals(a.getBukkitItemStack(), that.getBukkitItemStack());
    }

    static int hashCode(@Nullable AZBukkitItemStack item) {
        return (item == null) ? 0 : Objects.hashCode(item.getBukkitItemStack());
    }
}
