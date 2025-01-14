package fr.nathan818.azplugin.bukkit.compat.material;

import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_DATA;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_TRANSLATION_KEY;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ItemHandler {

    protected final @NonNull ItemDefinition definition;

    public int filterData(int itemData) {
        return DEFAULT_ITEM_DATA;
    }

    public String getTranslationKey(int itemData) {
        return DEFAULT_TRANSLATION_KEY;
    }

    public interface Constructor {
        @NotNull
        ItemHandler create(@NotNull ItemDefinition definition);
    }
}
