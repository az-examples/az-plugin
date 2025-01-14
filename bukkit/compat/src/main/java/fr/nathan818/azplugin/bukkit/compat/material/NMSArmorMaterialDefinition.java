package fr.nathan818.azplugin.bukkit.compat.material;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
@Getter
@ToString
public class NMSArmorMaterialDefinition implements EnumDefinition {

    private final @NonNull String name;
    private final int durabilityFactor;
    private final int bootsModifier;
    private final int leggingsModifier;
    private final int chestplateModifier;
    private final int helmetModifier;
    private final int enchantability;
    private final float toughness;

    public int[] getModifiers() {
        return new int[] { bootsModifier, leggingsModifier, chestplateModifier, helmetModifier };
    }
}
