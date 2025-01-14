package fr.nathan818.azplugin.bukkit.compat.material;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
public final class BlockDefinition {

    private final int id;
    private final @NonNull String bukkitName;
    private final @NonNull String minecraftName;
    private final @NonNull String translationKey;

    private final boolean fullBlock;
    private final int lightOpacity;
    private final boolean translucent;
    private final int lightValue;
    private final boolean useNeighborBrightness;
    private final float strength;
    private final float durability;
    private final boolean enableStats;
    private final boolean isTicking;
    private final boolean isTileEntity;
    private final @NonNull SoundType soundType;
    private final @NonNull Material material;
    private final @NonNull MaterialColor materialColor;
    private final float frictionFactor;
    private final @NonNull PushReaction pushReaction;
    private final int variantCount;
    private final @NonNull BlockHandler.Constructor handler;
    private final @Nullable ItemDefinition item;

    public enum SoundType {
        WOOD,
        GRAVEL,
        GRASS,
        STONE,
        METAL,
        GLASS,
        CLOTH,
        SAND,
        SNOW,
        LADDER,
        ANVIL,
        SLIME,
    }

    public enum Material {
        AIR,
        GRASS,
        EARTH,
        WOOD,
        STONE,
        ORE,
        HEAVY,
        WATER,
        LAVA,
        LEAVES,
        PLANT,
        REPLACEABLE_PLANT,
        SPONGE,
        CLOTH,
        FIRE,
        SAND,
        ORIENTABLE,
        WOOL,
        SHATTERABLE,
        BUILDABLE_GLASS,
        TNT,
        CORAL,
        ICE,
        SNOW_LAYER,
        PACKED_ICE,
        SNOW_BLOCK,
        CACTUS,
        CLAY,
        PUMPKIN,
        DRAGON_EGG,
        PORTAL,
        CAKE,
        WEB,
        PISTON,
        BANNER,
    }

    public enum MaterialColor {
        AIR,
        GRASS,
        SAND,
        CLOTH,
        TNT,
        ICE,
        IRON,
        FOLIAGE,
        WHITE,
        CLAY,
        DIRT,
        STONE,
        WATER,
        WOOD,
        QUARTZ,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE,
        YELLOW,
        LIME,
        PINK,
        GRAY,
        SILVER,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK,
        GOLD,
        DIAMOND,
        LAPIS,
        EMERALD,
        OBSIDIAN,
        NETHERRACK,
    }

    public enum PushReaction {
        NORMAL,
        DESTROY,
        BLOCK,
    }

    public static class Builder {

        public Builder itemBlock(Consumer<? super ItemDefinition.ItemBlock.Builder> typeConsumer) {
            ItemDefinition.Builder itemBuilder = ItemDefinition.builder();
            itemBuilder.id(id);
            itemBuilder.bukkitName(bukkitName);
            itemBuilder.minecraftName(minecraftName);
            itemBuilder.translationKey(translationKey);
            ItemDefinition.ItemBlock.Builder typeBuilder = ItemDefinition.ItemBlock.builder();
            typeConsumer.accept(typeBuilder);
            itemBuilder.type(typeBuilder.build());
            return item(itemBuilder.build());
        }
    }
}
