package fr.nathan818.azplugin.bukkit.compat.type;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition.MaterialColor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
@Getter
public enum DyeColor {
    WHITE(0, 15, "white", "white", MaterialColor.WHITE, ChatColor.WHITE),
    ORANGE(1, 14, "orange", "orange", MaterialColor.ORANGE, ChatColor.GOLD),
    MAGENTA(2, 13, "magenta", "magenta", MaterialColor.MAGENTA, ChatColor.AQUA),
    LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MaterialColor.LIGHT_BLUE, ChatColor.BLUE),
    YELLOW(4, 11, "yellow", "yellow", MaterialColor.YELLOW, ChatColor.YELLOW),
    LIME(5, 10, "lime", "lime", MaterialColor.LIME, ChatColor.GREEN),
    PINK(6, 9, "pink", "pink", MaterialColor.PINK, ChatColor.LIGHT_PURPLE),
    GRAY(7, 8, "gray", "gray", MaterialColor.GRAY, ChatColor.DARK_GRAY),
    SILVER(8, 7, "silver", "silver", MaterialColor.SILVER, ChatColor.GRAY),
    CYAN(9, 6, "cyan", "cyan", MaterialColor.CYAN, ChatColor.DARK_AQUA),
    PURPLE(10, 5, "purple", "purple", MaterialColor.PURPLE, ChatColor.DARK_PURPLE),
    BLUE(11, 4, "blue", "blue", MaterialColor.BLUE, ChatColor.DARK_BLUE),
    BROWN(12, 3, "brown", "brown", MaterialColor.BROWN, ChatColor.GOLD),
    GREEN(13, 2, "green", "green", MaterialColor.GREEN, ChatColor.DARK_GREEN),
    RED(14, 1, "red", "red", MaterialColor.RED, ChatColor.DARK_RED),
    BLACK(15, 0, "black", "black", MaterialColor.BLACK, ChatColor.BLACK);

    private static final DyeColor[] BY_ITEM_INDEX = new DyeColor[16];
    private static final DyeColor[] BY_BLOCK_INDEX = new DyeColor[16];

    static {
        for (DyeColor color : values()) {
            BY_ITEM_INDEX[color.itemIndex] = color;
            BY_BLOCK_INDEX[color.blockIndex] = color;
        }
    }

    public static DyeColor byItemIndex(int itemIndex) {
        return (itemIndex >= 0 && itemIndex < BY_ITEM_INDEX.length) ? BY_ITEM_INDEX[itemIndex] : WHITE;
    }

    public static DyeColor byBlockIndex(int blockIndex) {
        return (blockIndex >= 0 && blockIndex < BY_BLOCK_INDEX.length) ? BY_BLOCK_INDEX[blockIndex] : WHITE;
    }

    private final int itemIndex;
    private final int blockIndex;
    private final @NonNull String name;
    private final @NonNull String translationKey;
    private final @NonNull MaterialColor materialColor;
    private final @NonNull ChatColor chatColor;
}
