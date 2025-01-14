package fr.nathan818.azplugin.bukkit.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
final class AZBukkitItemStackImpl extends AZBukkitItemStackAbstract {

    private final @NonNull ItemStack bukkitItemStack;
}
