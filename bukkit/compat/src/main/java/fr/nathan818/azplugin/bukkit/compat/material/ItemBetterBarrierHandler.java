package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.ItemData;
import fr.nathan818.azplugin.bukkit.item.ItemStackProxy;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemBetterBarrierHandler extends ItemBlockHandler {

    public ItemBetterBarrierHandler(@NonNull ItemDefinition definition) {
        super(definition);
    }

    @Override
    public ItemData applyFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return new ItemData(Material.BARRIER.getId(), 0);
    }

    @Override
    public ItemData revertFallbackItem(@NotNull AZNetworkContext ctx, @NotNull ItemData orig) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return new ItemData(definition.getId(), filterData(orig.getData()));
    }

    @Override
    public int filterData(int itemData) {
        return Variant.byItemIndex(itemData).getIndex();
    }

    @RequiredArgsConstructor
    @Getter
    public enum Variant {
        FULL("full"),
        FULL_UP("full_up"),
        FULL_DOWN("full_down"),
        SLAB("slab"),
        PANE("pane"),
        PANE_UP("pane_up"),
        PANE_DOWN("pane_down"),
        PANE_OUT("pane_out"),
        WALL("wall"),
        WALL_UP("wall_up"),
        WALL_DOWN("wall_down"),
        CORNER("corner"),
        CORNER_UP("corner_up"),
        CORNER_DOWN("corner_down");

        private static final Variant[] VALUES = values();
        private static final Map<String, Variant> BY_NAME;

        static {
            BY_NAME = new HashMap<>(VALUES.length);
            for (Variant variant : VALUES) {
                BY_NAME.put(variant.getName(), variant);
            }
        }

        public static Variant byItemIndex(int itemData) {
            return (itemData >= 0 && itemData < VALUES.length) ? VALUES[itemData] : FULL;
        }

        public static Variant byName(String name) {
            return BY_NAME.get(name);
        }

        private final String name;

        public int getIndex() {
            return ordinal();
        }
    }
}
