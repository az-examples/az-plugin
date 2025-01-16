package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ItemHandler {

    protected final @NonNull ItemDefinition definition;

    public void applyFallback(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {}

    public interface Constructor<T extends ItemHandler> {
        @NotNull
        T create(@NotNull ItemDefinition definition);
    }
}
