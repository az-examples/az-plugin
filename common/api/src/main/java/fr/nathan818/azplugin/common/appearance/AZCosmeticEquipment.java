package fr.nathan818.azplugin.common.appearance;

import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import fr.nathan818.azplugin.common.item.NotchianItemStackLike;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment.ItemPattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZCosmeticEquipment {

    private final @Nullable NotchianItemStack item;

    @lombok.Builder.Default
    private final @NonNull MatchPattern matchPattern = MatchPattern.ANY;

    private final boolean hideInInventory;
    private final @Nullable NotchianChatComponent tooltipPrefix;
    private final @Nullable NotchianChatComponent tooltipSuffix;
    private final @Nullable Symbol symbol;

    public static class Builder {

        private Builder item(@Nullable NotchianItemStack item) {
            this.item = item;
            return this;
        }

        public Builder item(@Nullable NotchianItemStackLike item) {
            this.item = NotchianItemStackLike.convert(item);
            return this;
        }

        private Builder tooltipPrefix(@Nullable NotchianChatComponent tooltipPrefix) {
            this.tooltipPrefix = tooltipPrefix;
            return this;
        }

        public Builder tooltipPrefix(@Nullable NotchianChatComponentLike tooltipPrefix) {
            this.tooltipPrefix = NotchianChatComponentLike.convert(tooltipPrefix);
            return this;
        }

        private Builder tooltipSuffix(@Nullable NotchianChatComponent tooltipSuffix) {
            this.tooltipSuffix = tooltipSuffix;
            return this;
        }

        public Builder tooltipSuffix(@Nullable NotchianChatComponentLike tooltipSuffix) {
            this.tooltipSuffix = NotchianChatComponentLike.convert(tooltipSuffix);
            return this;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode(callSuper = false)
    @Getter
    @ToString
    public static final class MatchPattern {

        public static MatchPattern NONE = MatchPattern.builder().build();
        public static MatchPattern ANY = MatchPattern.builder().add(MatchFlag.ANY).build();
        public static MatchPattern EMPTY = MatchPattern.builder().add(MatchFlag.EMPTY).build();
        public static MatchPattern NOT_EMPTY = MatchPattern.builder().add(MatchFlag.NOT_EMPTY).build();

        public static MatchPatternBuilder builder() {
            return new MatchPatternBuilderImpl();
        }

        private final @Nullable List<ItemPattern> patterns;

        public boolean isNone() {
            return patterns == null;
        }

        public boolean isAny() {
            return patterns != null && patterns.isEmpty();
        }

        public boolean isMatching(@Nullable NotchianItemStackLike item) {
            if (patterns == null) {
                return true;
            }
            NotchianItemStack itemStack = NotchianItemStackLike.convert(item);
            for (ItemPattern pattern : patterns) {
                if (isMatching(itemStack, pattern)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isMatching(@Nullable NotchianItemStack itemStack, @NotNull ItemPattern pattern) {
            if (pattern.getId() != 0) {
                return (
                    !isNull(itemStack) &&
                    pattern.getId() == itemStack.getItemId() &&
                    (pattern.getData() == -1 || pattern.getData() == itemStack.getDamage())
                );
            } else {
                return (pattern.getData() & getCosmeticEquipmentType(itemStack)) != 0;
            }
        }

        private int getCosmeticEquipmentType(@Nullable NotchianItemStack itemStack) {
            if (isNull(itemStack)) {
                return ItemPattern.ID0_EMPTY;
            }
            switch (itemStack.getItemId()) {
                case 256: // iron_shovel
                case 269: // wooden_shovel
                case 273: // stone_shovel
                case 277: // diamond_shovel
                case 284: // golden_shovel
                case 773: // emerald_shovel
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_SHOVEL;
                case 257: // iron_pickaxe
                case 270: // wooden_pickaxe
                case 274: // stone_pickaxe
                case 278: // diamond_pickaxe
                case 285: // golden_pickaxe
                case 774: // emerald_pickaxe
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_PICKAXE;
                case 258: // iron_axe
                case 271: // wooden_axe
                case 275: // stone_axe
                case 279: // diamond_axe
                case 286: // golden_axe
                case 775: // emerald_axe
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_AXE;
                case 267: // iron_sword
                case 268: // wooden_sword
                case 272: // stone_sword
                case 276: // diamond_sword
                case 283: // golden_sword
                case 772: // emerald_sword
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_SWORD;
                case 290: // wooden_hoe
                case 291: // stone_hoe
                case 292: // iron_hoe
                case 293: // diamond_hoe
                case 294: // golden_hoe
                case 776: // emerald_hoe
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_HOE;
                case 298: // leather_helmet
                case 302: // chainmail_helmet
                case 306: // iron_helmet
                case 310: // diamond_helmet
                case 314: // golden_helmet
                case 768: // emerald_helmet
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_HELMET;
                case 299: // leather_chestplate
                case 303: // chainmail_chestplate
                case 307: // iron_chestplate
                case 311: // diamond_chestplate
                case 315: // golden_chestplate
                case 769: // emerald_chestplate
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_CHESTPLATE;
                case 300: // leather_leggings
                case 304: // chainmail_leggings
                case 308: // iron_leggings
                case 312: // diamond_leggings
                case 316: // golden_leggings
                case 770: // emerald_leggings
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_LEGGINGS;
                case 301: // leather_boots
                case 305: // chainmail_boots
                case 309: // iron_boots
                case 313: // diamond_boots
                case 317: // golden_boots
                case 771: // emerald_boots
                    return ItemPattern.ID0_NOT_EMPTY | ItemPattern.ID0_BOOTS;
                default:
                    return ItemPattern.ID0_NOT_EMPTY;
            }
        }

        private boolean isNull(@Nullable NotchianItemStack itemStack) {
            return itemStack == null || itemStack.getItemId() <= 0;
        }
    }

    public interface MatchPatternBuilder {
        MatchPatternBuilder add(MatchFlag flag);

        MatchPatternBuilder add(MatchFlag... flags);

        MatchPatternBuilder add(Collection<MatchFlag> flags);

        default MatchPatternBuilder add(int itemId) {
            return add(itemId, (short) -1);
        }

        MatchPatternBuilder add(int itemId, short data);

        MatchPattern build();
    }

    public enum MatchFlag {
        ANY,
        EMPTY,
        NOT_EMPTY,

        SHOVEL,
        PICKAXE,
        AXE,
        SWORD,
        HOE,

        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;

        public static final Set<MatchFlag> TOOL = Collections.unmodifiableSet(
            EnumSet.of(MatchFlag.SHOVEL, MatchFlag.PICKAXE, MatchFlag.AXE, MatchFlag.SWORD, MatchFlag.HOE)
        );
        public static final Set<MatchFlag> ARMOR = Collections.unmodifiableSet(
            EnumSet.of(MatchFlag.HELMET, MatchFlag.CHESTPLATE, MatchFlag.LEGGINGS, MatchFlag.BOOTS)
        );
    }

    @RequiredArgsConstructor
    @Getter
    public enum Slot {
        // Vanilla
        MAIN_HAND(0, -1, 1, true),
        FEET(1, 0, 3, true),
        LEGS(2, 0, 2, true),
        CHEST(3, 0, 1, true),
        HEAD(4, 0, 0, true),
        OFF_HAND(5, 1, 1, true),

        // Custom
        CUSTOM_1(6, -1, 0, false),
        CUSTOM_2(7, 1, 0, false),
        CUSTOM_3(8, -1, 2, false),
        CUSTOM_4(9, 1, 2, false),
        CUSTOM_5(10, -1, 3, false),
        CUSTOM_6(11, 1, 3, false);

        private final int index;
        private final int posX;
        private final int posY;
        private final boolean vanilla;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Symbol {
        SWORD(0),
        BOOTS(1),
        LEGGINGS(2),
        CHESTPLATE(3),
        HEAD(4),
        SHIELD(5),
        SPIRAL(6),
        SQUARE(7),
        TRIANGLE(8),
        CIRCLE(9),
        OCTAGON(10),
        RHOMBUS(11),
        HELMET(12),
        SHOVEL(13),
        PICKAXE(14),
        AXE(15),
        HOE(16),
        BOW(17),
        FISHING_ROD(18),
        FLINT_AND_STEEL(19),
        SHEARS(20),
        ELYTRA(21),
        BLOCK(22),
        INGOT(23),
        POTION(24),
        DUST(25);

        private final int id;
    }
}
