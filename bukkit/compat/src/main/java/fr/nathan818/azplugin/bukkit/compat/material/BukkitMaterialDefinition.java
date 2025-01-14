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
public final class BukkitMaterialDefinition implements EnumDefinition {

    private final @NonNull String name;
    private final int id;
    private final boolean isBlock;
    private final boolean isEdible;
    private final boolean isSolid;
    private final boolean isTransparent;
    private final boolean isFlammable;
    private final boolean isBurnable;
    private final boolean isOccluding;
    private final boolean hasGravity;

    @lombok.Builder.Default
    private final int stack = 64;

    private final int durability;
}
