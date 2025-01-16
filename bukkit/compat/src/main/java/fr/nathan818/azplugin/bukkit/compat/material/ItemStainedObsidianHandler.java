package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemStainedObsidianHandler extends ItemBlockHandler {

    public ItemStainedObsidianHandler(@NonNull ItemDefinition definition) {
        super(definition);
    }

    @Override
    public void applyFallback(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return;
        }
        itemStack.setType(Material.OBSIDIAN);
        itemStack.setDurability(0);
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
