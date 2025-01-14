package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.EquipmentSlot;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
public class ItemDefinition {

    private final int id;
    private final @NonNull String bukkitName;
    private final @NonNull String minecraftName;
    private final @NonNull String translationKey;

    private final @NonNull Type type;

    public interface Type {} // consider sealed

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class ItemBlock implements Type {

        private final boolean hasSubtypes;
        private final @NonNull ItemHandler.Constructor handler;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Armor implements Type {

        private final @NonNull String material;
        private final @NonNull EquipmentSlot slot;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Sword implements Type {

        private final @NonNull String material;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Spade implements Type {

        private final @NonNull String material;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Pickaxe implements Type {

        private final @NonNull String material;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Axe implements Type {

        private final @NonNull String material;
        private final float attackDamage;
        private final float attackSpeed;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Hoe implements Type {

        private final @NonNull String material;
    }
}
