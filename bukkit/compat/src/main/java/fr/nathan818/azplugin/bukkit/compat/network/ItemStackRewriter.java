package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import org.jetbrains.annotations.NotNull;

public interface ItemStackRewriter {
    void rewriteItemStackOut(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack);

    void rewriteItemStackIn(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack);
}
