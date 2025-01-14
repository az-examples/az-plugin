package fr.nathan818.azplugin.common.appearance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZNameTag {

    private final @Nullable String text;
    private final @Nullable Rarity rarity;

    private final @Nullable Float viewDistance;
    private final @Nullable Float opacity;
    private final @Nullable Float throughWallOpacity;
    private final @Nullable Float scale;
    private final @Nullable Visibility teamVisibility;

    private final @Nullable Float sneakViewDistance;
    private final @Nullable Float sneakOpacity;
    private final @Nullable Float sneakThroughWallOpacity;
    private final @Nullable Float sneakScale;
    private final @Nullable Visibility sneakTeamVisibility;

    private final @Nullable Float pointedOpacity;
    private final @Nullable Float pointedScale;
    private final @Nullable Visibility pointedTeamVisibility;

    public boolean isEmpty() {
        return (
            text == null &&
            rarity == null &&
            viewDistance == null &&
            opacity == null &&
            throughWallOpacity == null &&
            scale == null &&
            teamVisibility == null &&
            sneakViewDistance == null &&
            sneakOpacity == null &&
            sneakThroughWallOpacity == null &&
            sneakScale == null &&
            sneakTeamVisibility == null &&
            pointedOpacity == null &&
            pointedScale == null &&
            pointedTeamVisibility == null
        );
    }

    public enum Visibility {
        ALWAYS,
        NEVER,
        HIDE_FOR_OTHER_TEAMS,
        HIDE_FOR_OWN_TEAM,
    }

    public enum Rarity {
        NONE,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY,
        MYTHIC,
    }

    public enum Slot {
        MAIN,
        SUP,
        SUB,
    }
}
