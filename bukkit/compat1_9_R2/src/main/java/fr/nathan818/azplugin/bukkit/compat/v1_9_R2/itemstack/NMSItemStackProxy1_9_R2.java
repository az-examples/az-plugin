package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.itemstack;

import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.item.NbtCompoundProxy;
import lombok.NonNull;
import net.minecraft.server.v1_9_R2.Item;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NMSItemStackProxy1_9_R2 implements ItemStackProxy {

    private ItemStack handle;
    private boolean copied;

    public NMSItemStackProxy1_9_R2(@NonNull ItemStack handle, boolean copyOnWrite) {
        this.handle = handle;
        this.copied = !copyOnWrite;
    }

    public ItemStack getForRead() {
        return handle;
    }

    public ItemStack getForWrite() {
        if (!copied) {
            copied = true;
            handle = handle.cloneItemStack();
        }
        return handle;
    }

    @Override
    public int getTypeId() {
        return Item.getId(getForRead().getItem());
    }

    @Override
    public void setTypeId(int typeId) {
        getForWrite().setItem(Item.getById(typeId));
    }

    @Override
    public int getAmount() {
        return getForRead().count;
    }

    @Override
    public void setAmount(int amount) {
        getForWrite().count = amount;
    }

    @Override
    public int getDurability() {
        return getForRead().getData();
    }

    @Override
    public void setDurability(int durability) {
        getForWrite().setData(durability);
    }

    @Override
    public @Nullable NbtCompoundProxy getTagForRead() {
        NBTTagCompound tag = getForRead().getTag();
        return (tag == null) ? null : new NbtCompoundProxy1_9_R2(tag, true);
    }

    @Override
    public @NotNull NbtCompoundProxy getTagForWrite() {
        ItemStack handle = getForWrite();
        NBTTagCompound tag = handle.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            handle.setTag(tag);
        }
        return new NbtCompoundProxy1_9_R2(tag, false);
    }

    @Override
    public boolean removeTag() {
        if (getForRead().getTag() == null) {
            return false;
        }
        getForWrite().setTag(null);
        return true;
    }

    @Override
    public @NotNull org.bukkit.inventory.ItemStack asItemStack() {
        return CraftItemStack.asCraftMirror(getForRead());
    }

    @Override
    public String toString() {
        return handle.toString();
    }
}
