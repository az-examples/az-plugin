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

    private final int sinceProtocolVersion;

    private final int id;
    private final @NonNull String bukkitName;
    private final @NonNull String minecraftName;
    private final @NonNull String translationKey;

    private final @NonNull Type<?> type;

    public abstract static class Type<T extends ItemHandler> { // consider sealed

        public abstract @NonNull ItemHandler.Constructor<T> getHandler();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class ItemBlock extends Type<ItemBlockHandler> {

        private final boolean hasSubtypes;
        private final @NonNull ItemHandler.Constructor<ItemBlockHandler> handler;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Armor extends Type<ItemHandler> {

        private final @NonNull String material;
        private final @NonNull EquipmentSlot slot;
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler;

        public static class Builder {

            @SuppressWarnings("ConstantValue")
            public Builder slot(EquipmentSlot slot) {
                this.slot = slot;
                if (this.handler == null) {
                    switch (slot) {
                        case HEAD:
                            this.handler = ItemFallbackHandler.of(302); // CHAINMAIL_HELMET
                            break;
                        case CHEST:
                            this.handler = ItemFallbackHandler.of(303); // CHAINMAIL_CHESTPLATE
                            break;
                        case LEGS:
                            this.handler = ItemFallbackHandler.of(304); // CHAINMAIL_LEGGINGS
                            break;
                        case FEET:
                            this.handler = ItemFallbackHandler.of(305); // CHAINMAIL_BOOTS
                            break;
                    }
                }
                return this;
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Sword extends Type<ItemHandler> {

        private final @NonNull String material;

        @lombok.Builder.Default
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler = ItemFallbackHandler.of(276); // DIAMOND_SWORD
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Spade extends Type<ItemHandler> {

        private final @NonNull String material;

        @lombok.Builder.Default
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler = ItemFallbackHandler.of(277); // DIAMOND_SPADE
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Pickaxe extends Type<ItemHandler> {

        private final @NonNull String material;

        @lombok.Builder.Default
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler = ItemFallbackHandler.of(278); // DIAMOND_PICKAXE
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Axe extends Type<ItemHandler> {

        private final @NonNull String material;
        private final float attackDamage;
        private final float attackSpeed;

        @lombok.Builder.Default
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler = ItemFallbackHandler.of(279); // DIAMOND_AXE
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @lombok.Builder(builderClassName = "Builder")
    @Getter
    @ToString
    public static final class Hoe extends Type<ItemHandler> {

        private final @NonNull String material;

        @lombok.Builder.Default
        private final @NonNull ItemHandler.Constructor<ItemHandler> handler = ItemFallbackHandler.of(293); // DIAMOND_HOE
    }
}
