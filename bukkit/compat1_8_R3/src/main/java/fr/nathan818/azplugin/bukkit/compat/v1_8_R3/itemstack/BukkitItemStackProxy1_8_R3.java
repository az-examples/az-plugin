package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.itemstack;

import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.CompatBridge1_8_R3;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.item.NbtCompoundProxy;
import lombok.NonNull;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitItemStackProxy1_8_R3 implements ItemStackProxy {

    private ItemStack handle;
    private boolean copied;

    public BukkitItemStackProxy1_8_R3(@NonNull ItemStack handle, boolean copyOnWrite) {
        if (handle instanceof CraftItemStack) {
            throw new IllegalArgumentException("CraftItemStack is not supported");
        }
        this.handle = handle;
        this.copied = !copyOnWrite;
    }

    public ItemStack getForRead() {
        return handle;
    }

    public ItemStack getForWrite() {
        if (!copied) {
            copied = true;
            handle = handle.clone();
        }
        return handle;
    }

    @Override
    public int getTypeId() {
        return getForRead().getTypeId();
    }

    @Override
    public void setTypeId(int typeId) {
        getForWrite().setTypeId(typeId);
    }

    @Override
    public int getAmount() {
        return getForRead().getAmount();
    }

    @Override
    public void setAmount(int amount) {
        getForWrite().setAmount(amount);
    }

    @Override
    public int getDurability() {
        return getForRead().getDurability();
    }

    @Override
    public void setDurability(int durability) {
        getForWrite().setDurability((short) durability);
    }

    @Override
    public @Nullable NbtCompoundProxy getTagForRead() {
        ItemStack handle = getForRead();
        if (!handle.hasItemMeta()) {
            return null;
        }
        return new NbtCompoundProxy1_8_R3(CompatBridge1_8_R3.getMetaItemUnhandledTags(handle.getItemMeta()), true);
    }

    @Override
    public @NotNull NbtCompoundProxy getTagForWrite() {
        return new NbtCompoundProxy1_8_R3(
            CompatBridge1_8_R3.getMetaItemUnhandledTags(getForWrite().getItemMeta()),
            false
        );
    }

    @Override
    public boolean removeTag() {
        if (!getForRead().hasItemMeta()) {
            return false;
        }
        getForWrite().setItemMeta(null);
        return true;
    }

    @Override
    public @NotNull ItemStack asItemStack() {
        return getForRead();
    }

    @Override
    public String toString() {
        return handle.toString();
    }
}
