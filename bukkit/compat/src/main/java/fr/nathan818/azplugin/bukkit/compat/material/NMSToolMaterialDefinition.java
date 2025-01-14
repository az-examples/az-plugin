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
public class NMSToolMaterialDefinition implements EnumDefinition {

    private final @NonNull String name;
    private final int harvestLevel;
    private final int durability;
    private final float digSpeed;
    private final float damages;
    private final int enchantability;
}
