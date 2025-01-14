package fr.nathan818.azplugin.bukkit.compat.material;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BukkitMaterialDefinitions {

    public static final BukkitMaterialDefinition EMERALD_HELMET = BukkitMaterialDefinition.builder()
        .name("EMERALD_HELMET")
        .id(768)
        .stack(1)
        .durability(187)
        .build();
    public static final BukkitMaterialDefinition EMERALD_CHESTPLATE = BukkitMaterialDefinition.builder()
        .name("EMERALD_CHESTPLATE")
        .id(769)
        .stack(1)
        .durability(272)
        .build();
    public static final BukkitMaterialDefinition EMERALD_LEGGINGS = BukkitMaterialDefinition.builder()
        .name("EMERALD_LEGGINGS")
        .id(770)
        .stack(1)
        .durability(255)
        .build();
    public static final BukkitMaterialDefinition EMERALD_BOOTS = BukkitMaterialDefinition.builder()
        .name("EMERALD_BOOTS")
        .id(771)
        .stack(1)
        .durability(221)
        .build();

    public static final BukkitMaterialDefinition EMERALD_SWORD = BukkitMaterialDefinition.builder()
        .name("EMERALD_SWORD")
        .id(772)
        .stack(1)
        .durability(709)
        .build();
    public static final BukkitMaterialDefinition EMERALD_SPADE = BukkitMaterialDefinition.builder()
        .name("EMERALD_SPADE")
        .id(773)
        .stack(1)
        .durability(709)
        .build();
    public static final BukkitMaterialDefinition EMERALD_PICKAXE = BukkitMaterialDefinition.builder()
        .name("EMERALD_PICKAXE")
        .id(774)
        .stack(1)
        .durability(709)
        .build();
    public static final BukkitMaterialDefinition EMERALD_AXE = BukkitMaterialDefinition.builder()
        .name("EMERALD_AXE")
        .id(775)
        .stack(1)
        .durability(709)
        .build();
    public static final BukkitMaterialDefinition EMERALD_HOE = BukkitMaterialDefinition.builder()
        .name("EMERALD_HOE")
        .id(776)
        .stack(1)
        .durability(709)
        .build();

    public static final BukkitMaterialDefinition COLORED_PORTAL = BukkitMaterialDefinition.builder()
        .name("COLORED_PORTAL")
        .id(3072)
        .isBlock(true)
        .isTransparent(true)
        .build();
    public static final BukkitMaterialDefinition COLORED_PORTAL2 = BukkitMaterialDefinition.builder()
        .name("COLORED_PORTAL2")
        .id(3073)
        .isBlock(true)
        .isTransparent(true)
        .build();

    public static final BukkitMaterialDefinition BETTER_BARRIER = BukkitMaterialDefinition.builder()
        .name("BETTER_BARRIER")
        .id(3076)
        .isBlock(true)
        .isSolid(true)
        .isTransparent(true)
        .build();
    public static final BukkitMaterialDefinition BETTER_BARRIER2 = BukkitMaterialDefinition.builder()
        .name("BETTER_BARRIER2")
        .id(3077)
        .isBlock(true)
        .isSolid(true)
        .isTransparent(true)
        .build();
    public static final BukkitMaterialDefinition BETTER_BARRIER3 = BukkitMaterialDefinition.builder()
        .name("BETTER_BARRIER3")
        .id(3078)
        .isBlock(true)
        .isSolid(true)
        .isTransparent(true)
        .build();

    public static final BukkitMaterialDefinition STAINED_OBSIDIAN = BukkitMaterialDefinition.builder()
        .name("STAINED_OBSIDIAN")
        .id(3079)
        .isBlock(true)
        .isSolid(true)
        .isOccluding(true)
        .build();

    public static final List<BukkitMaterialDefinition> MATERIALS = Arrays.asList(
        EMERALD_HELMET,
        EMERALD_CHESTPLATE,
        EMERALD_LEGGINGS,
        EMERALD_BOOTS,
        EMERALD_SWORD,
        EMERALD_SPADE,
        EMERALD_PICKAXE,
        EMERALD_AXE,
        EMERALD_HOE,
        COLORED_PORTAL,
        COLORED_PORTAL2,
        BETTER_BARRIER,
        BETTER_BARRIER2,
        BETTER_BARRIER3,
        STAINED_OBSIDIAN
    );
}
