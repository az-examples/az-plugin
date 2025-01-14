package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import org.jetbrains.annotations.NotNull;

public class ItemColoredPortalHandler extends ItemHandler {

    public ItemColoredPortalHandler(@NotNull ItemDefinition definition) {
        super(definition);
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
