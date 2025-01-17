package fr.nathan818.azplugin.bukkit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
final class AZBukkitItemStackImpl implements AZBukkitItemStack {

    private final @NonNull ItemStack bukkitItemStack;

    @Override
    public String toString() {
        return AZBukkitItemStack.toString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return AZBukkitItemStack.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return AZBukkitItemStack.hashCode(this);
    }
}
