package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.EquipmentSlot;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemDefinitions {

    public static final ItemDefinition EMERALD_HELMET = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(768)
        .bukkitName("EMERALD_HELMET")
        .minecraftName("emerald_helmet")
        .translationKey("helmetEmerald")
        .type(ItemDefinition.Armor.builder().material("EMERALD").slot(EquipmentSlot.HEAD).build())
        .build();

    public static final ItemDefinition EMERALD_CHESTPLATE = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(769)
        .bukkitName("EMERALD_CHESTPLATE")
        .minecraftName("emerald_chestplate")
        .translationKey("chestplateEmerald")
        .type(ItemDefinition.Armor.builder().material("EMERALD").slot(EquipmentSlot.CHEST).build())
        .build();

    public static final ItemDefinition EMERALD_LEGGINGS = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(770)
        .bukkitName("EMERALD_LEGGINGS")
        .minecraftName("emerald_leggings")
        .translationKey("leggingsEmerald")
        .type(ItemDefinition.Armor.builder().material("EMERALD").slot(EquipmentSlot.LEGS).build())
        .build();

    public static final ItemDefinition EMERALD_BOOTS = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(771)
        .bukkitName("EMERALD_BOOTS")
        .minecraftName("emerald_boots")
        .translationKey("bootsEmerald")
        .type(ItemDefinition.Armor.builder().material("EMERALD").slot(EquipmentSlot.FEET).build())
        .build();

    public static final ItemDefinition EMERALD_SWORD = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(772)
        .bukkitName("EMERALD_SWORD")
        .minecraftName("emerald_sword")
        .translationKey("swordEmerald")
        .type(ItemDefinition.Sword.builder().material("EMERALD").build())
        .build();

    public static final ItemDefinition EMERALD_SHOVEL = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(773)
        .bukkitName("EMERALD_SHOVEL")
        .minecraftName("emerald_shovel")
        .translationKey("shovelEmerald")
        .type(ItemDefinition.Spade.builder().material("EMERALD").build())
        .build();

    public static final ItemDefinition EMERALD_PICKAXE = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(774)
        .bukkitName("EMERALD_PICKAXE")
        .minecraftName("emerald_pickaxe")
        .translationKey("pickaxeEmerald")
        .type(ItemDefinition.Pickaxe.builder().material("EMERALD").build())
        .build();

    public static final ItemDefinition EMERALD_AXE = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(775)
        .bukkitName("EMERALD_AXE")
        .minecraftName("emerald_axe")
        .translationKey("axeEmerald")
        .type(ItemDefinition.Axe.builder().material("EMERALD").attackDamage(8.0F).attackSpeed(-2.9F).build())
        .build();

    public static final ItemDefinition EMERALD_HOE = ItemDefinition.builder()
        .sinceProtocolVersion(1)
        .id(776)
        .bukkitName("EMERALD_HOE")
        .minecraftName("emerald_hoe")
        .translationKey("hoeEmerald")
        .type(ItemDefinition.Hoe.builder().material("EMERALD").build())
        .build();

    public static final List<ItemDefinition> ITEMS = Arrays.asList(
        EMERALD_HELMET,
        EMERALD_CHESTPLATE,
        EMERALD_LEGGINGS,
        EMERALD_BOOTS,
        EMERALD_SWORD,
        EMERALD_SHOVEL,
        EMERALD_PICKAXE,
        EMERALD_AXE,
        EMERALD_HOE
    );
}
