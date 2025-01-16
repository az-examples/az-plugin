package fr.nathan818.azplugin.bukkit.compat.network;

import fr.nathan818.azplugin.common.network.AZNetworkContext;
import org.jetbrains.annotations.NotNull;

public interface BlockRewriter {
    int@NotNull[] getRewriteBlockOutPalette(@NotNull AZNetworkContext ctx);
}
