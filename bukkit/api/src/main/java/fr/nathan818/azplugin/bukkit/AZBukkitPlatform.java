package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.common.AZPlatform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public interface AZBukkitPlatform extends AZPlatform<Player, AZPlayer> {
    @Override
    @NotNull
    AZBukkitAPI getAPI();

    @NotNull
    ItemStack asCraftCopy(@NotNull ItemStack item);

    @Nullable
    ItemStack createItemStack(int itemId, int count, int damage, @Nullable NotchianNbtTagCompound tag);

    @Nullable
    NotchianNbtTagCompound getItemStackTag(@NotNull ItemStack itemStack);

    int getActiveContainerWindowId(@NotNull Player bukkitPlayer);

    void closeInventoryServerSide(@NotNull Player bukkitPlayer);
}
