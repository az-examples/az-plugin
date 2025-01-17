package fr.nathan818.azplugin.bukkit.compat.proxy;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemStackProxy {
    int getTypeId();

    default Material getType() {
        return Material.getMaterial(getTypeId());
    }

    void setTypeId(int type);

    default void setType(@Nullable Material type) {
        setTypeId(type == null ? 0 : type.getId());
    }

    int getAmount();

    void setAmount(int amount);

    int getDurability();

    void setDurability(int durability);

    @Nullable
    NbtCompoundProxy getTagForRead();

    @NotNull
    NbtCompoundProxy getTagForWrite();

    boolean removeTag();
}
