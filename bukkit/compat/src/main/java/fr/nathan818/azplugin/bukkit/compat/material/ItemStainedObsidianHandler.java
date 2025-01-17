package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.proxy.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import fr.nathan818.azplugin.bukkit.compat.type.ItemData;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStainedObsidianHandler extends ItemBlockHandler {

    public ItemStainedObsidianHandler(@NonNull ItemDefinition definition) {
        super(definition);
    }

    @Override
    public ItemData applyFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return new ItemData(Material.OBSIDIAN.getId(), 0);
    }

    @Override
    public @Nullable ItemData revertFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemData orig) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return new ItemData(definition.getId(), filterData(orig.getData()));
    }

    @Override
    public int filterData(int itemData) {
        return DyeColor.byItemIndex(itemData).getItemIndex();
    }

    @Override
    public String getTranslationKey(int itemData) {
        return definition.getTranslationKey() + '.' + DyeColor.byItemIndex(itemData).getTranslationKey();
    }
}
