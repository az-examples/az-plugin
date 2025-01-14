package fr.nathan818.azplugin.bukkit;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class AZMaterial {

    public static final Material EMERALD_HELMET = Material.getMaterial("EMERALD_HELMET");
    public static final Material EMERALD_CHESTPLATE = Material.getMaterial("EMERALD_CHESTPLATE");
    public static final Material EMERALD_LEGGINGS = Material.getMaterial("EMERALD_LEGGINGS");
    public static final Material EMERALD_BOOTS = Material.getMaterial("EMERALD_BOOTS");
    public static final Material EMERALD_SWORD = Material.getMaterial("EMERALD_SWORD");
    public static final Material EMERALD_SPADE = Material.getMaterial("EMERALD_SPADE");
    public static final Material EMERALD_PICKAXE = Material.getMaterial("EMERALD_PICKAXE");
    public static final Material EMERALD_AXE = Material.getMaterial("EMERALD_AXE");
    public static final Material EMERALD_HOE = Material.getMaterial("EMERALD_HOE");
    public static final Material COLORED_PORTAL = Material.getMaterial("COLORED_PORTAL");
    public static final Material COLORED_PORTAL2 = Material.getMaterial("COLORED_PORTAL2");
    public static final Material BETTER_BARRIER = Material.getMaterial("BETTER_BARRIER");
    public static final Material BETTER_BARRIER2 = Material.getMaterial("BETTER_BARRIER2");
    public static final Material BETTER_BARRIER3 = Material.getMaterial("BETTER_BARRIER3");
    public static final Material STAINED_OBSIDIAN = Material.getMaterial("STAINED_OBSIDIAN");

    @Contract("null -> false")
    public static boolean isEmeraldArmor(@Nullable Material material) {
        return (
            material != null &&
            (material == EMERALD_HELMET ||
                material == EMERALD_CHESTPLATE ||
                material == EMERALD_LEGGINGS ||
                material == EMERALD_BOOTS)
        );
    }

    @Contract("null -> false")
    public static boolean isEmeraldTool(@Nullable Material material) {
        return (
            material != null &&
            (material == EMERALD_SWORD ||
                material == EMERALD_SPADE ||
                material == EMERALD_PICKAXE ||
                material == EMERALD_AXE ||
                material == EMERALD_HOE)
        );
    }

    @Contract("null -> false")
    public static boolean isPortal(@Nullable Material material) {
        return (
            material != null &&
            (material == Material.PORTAL || material == COLORED_PORTAL || material == COLORED_PORTAL2)
        );
    }

    @Contract("null -> false")
    public static boolean isColoredPortal(@Nullable Material material) {
        return material != null && (material == COLORED_PORTAL || material == COLORED_PORTAL2);
    }

    @Contract("null -> false")
    public static boolean isBarrier(@Nullable Material material) {
        return (
            material != null &&
            (material == Material.BARRIER ||
                material == BETTER_BARRIER ||
                material == BETTER_BARRIER2 ||
                material == BETTER_BARRIER3)
        );
    }

    @Contract("null -> false")
    public static boolean isBetterBarrier(@Nullable Material material) {
        return (
            material != null &&
            (material == BETTER_BARRIER || material == BETTER_BARRIER2 || material == BETTER_BARRIER3)
        );
    }

    @Contract("null -> false")
    public static boolean isObsidian(@Nullable Material material) {
        return material != null && (material == Material.OBSIDIAN || material == STAINED_OBSIDIAN);
    }
}
