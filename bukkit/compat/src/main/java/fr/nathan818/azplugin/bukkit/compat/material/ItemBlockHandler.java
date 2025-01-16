package fr.nathan818.azplugin.bukkit.compat.material;

import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_DATA;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_TRANSLATION_KEY;

import org.jetbrains.annotations.NotNull;

public class ItemBlockHandler extends ItemHandler {

    public ItemBlockHandler(@NotNull ItemDefinition definition) {
        super(definition);
    }

    public int filterData(int itemData) {
        return DEFAULT_ITEM_DATA;
    }

    public String getTranslationKey(int itemData) {
        return DEFAULT_TRANSLATION_KEY;
    }
}
