package fr.nathan818.azplugin.bukkit.compat.material;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NMSMaterialDefinitions {

    public static final NMSArmorMaterialDefinition ARMOR_EMERALD = NMSArmorMaterialDefinition.builder()
        .name("EMERALD")
        .durabilityFactor(17)
        .bootsModifier(4)
        .leggingsModifier(7)
        .chestplateModifier(9)
        .helmetModifier(4)
        .enchantability(6)
        .toughness(2.0F)
        .build();

    public static final NMSToolMaterialDefinition TOOL_EMERALD = NMSToolMaterialDefinition.builder()
        .name("EMERALD")
        .harvestLevel(3)
        .durability(709)
        .digSpeed(12.0F)
        .damages(3.5F)
        .enchantability(6)
        .build();

    public static final List<NMSArmorMaterialDefinition> ARMOR_MATERIALS = Arrays.asList(ARMOR_EMERALD);
    public static final List<NMSToolMaterialDefinition> TOOL_MATERIALS = Arrays.asList(TOOL_EMERALD);

    public static String fixToolMaterialName1_8(String name) {
        // In Minecraft 1.8, the diamond tools material is named "EMERALD" instead of "DIAMOND"
        // So we use "REAL_EMERALD" to avoid conflicts
        return "EMERALD".equals(name) ? "REAL_EMERALD" : name;
    }
}
