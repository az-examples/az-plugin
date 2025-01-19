package fr.nathan818.azplugin.bukkit;

import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.AZPlatform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public interface AZBukkitPlatform extends AZPlatform<Player, AZPlayer> {
    @Override
    @NotNull
    AZBukkitAPI getAPI();

    @Contract("null -> null; !null -> !null")
    @Nullable
    ItemStack asCraftCopy(@Nullable ItemStack item);

    @Nullable
    ItemStack createItemStack(int itemId, int count, int damage, @Nullable NotchianNbtTagCompound tag);

    @Contract("null -> null")
    @Nullable
    NotchianNbtTagCompound getItemStackTag(@Nullable ItemStack itemStack);

    @Contract("null -> null; !null -> !null")
    @Nullable
    ItemStackProxy getItemStackProxy(@Nullable ItemStack itemStack);

    boolean isSync(@NotNull AZEntity target);

    void assertSync(@NotNull AZEntity target, String method);

    void scheduleSync(@NotNull AZEntity target, @NotNull Runnable task);
}
