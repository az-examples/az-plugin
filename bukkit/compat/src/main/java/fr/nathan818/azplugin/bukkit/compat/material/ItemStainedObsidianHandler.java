package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import lombok.NonNull;

public class ItemStainedObsidianHandler extends ItemHandler {

    public ItemStainedObsidianHandler(@NonNull ItemDefinition definition) {
        super(definition);
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
