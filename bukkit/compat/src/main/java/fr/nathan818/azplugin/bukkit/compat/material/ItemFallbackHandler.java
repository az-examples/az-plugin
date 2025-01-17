package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.proxy.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.compat.type.ItemData;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFallbackHandler extends ItemHandler {

    public static @NonNull Constructor<ItemHandler> of(int fallbackItemId) {
        return definition -> new ItemFallbackHandler(definition, fallbackItemId);
    }

    private final Material fallbackItem;

    public ItemFallbackHandler(@NotNull ItemDefinition definition, int fallbackItemId) {
        super(definition);
        this.fallbackItem = Material.getMaterial(fallbackItemId);
    }

    @Override
    public ItemData applyFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }

        short itemMaxDurability = itemStack.getType().getMaxDurability();
        short fallbackMaxDurability = fallbackItem.getMaxDurability();
        int durability;
        if (itemMaxDurability != 0 && fallbackMaxDurability != 0) {
            // Remap item durability to match the fallback item durability
            if (itemStack.getDurability() >= itemMaxDurability) {
                durability = fallbackMaxDurability;
            } else {
                durability = Math.min(
                    fallbackMaxDurability - 1,
                    (itemStack.getDurability() * fallbackMaxDurability) / itemMaxDurability
                );
            }
        } else {
            durability = 0;
        }

        return new ItemData(fallbackItem.getId(), durability);
    }

    @Override
    public @Nullable ItemData revertFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemData orig) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return new ItemData(definition.getId(), orig.getData());
    }
}
