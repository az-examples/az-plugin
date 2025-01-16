package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemColoredPortalHandler extends ItemBlockHandler {

    public ItemColoredPortalHandler(@NotNull ItemDefinition definition) {
        super(definition);
    }

    @Override
    public void applyFallback(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return;
        }
        itemStack.setType(Material.STAINED_GLASS_PANE);
        itemStack.setDurability(itemStack.getDurability());
    }

    @Override
    public int filterData(int itemData) {
        return DyeColor.byItemIndex(itemData).getItemIndex();
    }

    @Override
    public String getTranslationKey(int itemData) {
        return "portal." + DyeColor.byItemIndex(itemData).getTranslationKey();
    }
}
