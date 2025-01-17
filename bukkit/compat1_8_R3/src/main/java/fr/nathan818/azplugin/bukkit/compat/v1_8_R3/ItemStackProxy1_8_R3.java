package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.compat.proxy.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.compat.proxy.NbtCompoundProxy;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackProxy1_8_R3 implements ItemStackProxy {

    private ItemStack handle;
    private boolean copied;

    public ItemStackProxy1_8_R3(@NonNull ItemStack handle, boolean copyOnWrite) {
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
    public void setTypeId(int type) {
        getForWrite().setItem(Item.getById(type));
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
        return (tag == null) ? null : new NbtCompoundProxy1_8_R3(tag, true);
    }

    @Override
    public @NotNull NbtCompoundProxy getTagForWrite() {
        ItemStack handle = getForWrite();
        NBTTagCompound tag = handle.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            handle.setTag(tag);
        }
        return new NbtCompoundProxy1_8_R3(tag, false);
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
    public String toString() {
        return handle.toString();
    }
}
