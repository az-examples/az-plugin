package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.item.ItemStackProxyAbstract;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;

public class ItemStackProxy1_8_R3 extends ItemStackProxyAbstract {

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
}
