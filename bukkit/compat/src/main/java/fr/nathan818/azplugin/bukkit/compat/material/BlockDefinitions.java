package fr.nathan818.azplugin.bukkit.compat.material;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class BlockDefinitions {

    public static final BlockDefinition COLORED_PORTAL = BlockDefinition.builder()
        .sinceProtocolVersion(1)
        .id(3072)
        .bukkitName("COLORED_PORTAL")
        .minecraftName("colored_portal")
        .translationKey("coloredPortal")
        .fullBlock(false)
        .lightOpacity(0)
        .translucent(true)
        .lightValue(11)
        .useNeighborBrightness(true)
        .strength(-1.0F)
        .durability(0.0F)
        .enableStats(true)
        .isTicking(false)
        .isTileEntity(false)
        .soundType(BlockDefinition.SoundType.GLASS)
        .material(BlockDefinition.Material.PORTAL)
        .materialColor(BlockDefinition.MaterialColor.AIR)
        .frictionFactor(0.6F)
        .pushReaction(BlockDefinition.PushReaction.BLOCK)
        .variantCount(16)
        .handler(BlockColoredPortalHandler::new)
        .itemBlock(t -> t.hasSubtypes(true).handler(ItemColoredPortalHandler::new))
        .build();

    public static final BlockDefinition COLORED_PORTAL2 = COLORED_PORTAL.toBuilder()
        .id(3073)
        .bukkitName("COLORED_PORTAL2")
        .minecraftName("colored_portal2")
        .item(null)
        .build();

    public static final BlockDefinition BETTER_BARRIER = BlockDefinition.builder()
        .sinceProtocolVersion(8)
        .id(3076)
        .bukkitName("BETTER_BARRIER")
        .minecraftName("better_barrier")
        .translationKey("barrier")
        .fullBlock(false)
        .lightOpacity(0)
        .translucent(true)
        .lightValue(0)
        .useNeighborBrightness(true)
        .strength(-1.0F)
        .durability(18000004.0F)
        .enableStats(false)
        .isTicking(false)
        .isTileEntity(false)
        .soundType(BlockDefinition.SoundType.STONE)
        .material(BlockDefinition.Material.BANNER)
        .materialColor(BlockDefinition.MaterialColor.AIR)
        .frictionFactor(0.6F)
        .pushReaction(BlockDefinition.PushReaction.BLOCK)
        .variantCount(16)
        .handler(BlockBetterBarrierHandler::new)
        .itemBlock(t -> t.hasSubtypes(true).handler(ItemBetterBarrierHandler::new))
        .build();

    public static final BlockDefinition BETTER_BARRIER2 = BETTER_BARRIER.toBuilder()
        .id(3077)
        .bukkitName("BETTER_BARRIER2")
        .minecraftName("better_barrier2")
        .item(null)
        .build();

    public static final BlockDefinition BETTER_BARRIER3 = BETTER_BARRIER.toBuilder()
        .id(3078)
        .bukkitName("BETTER_BARRIER3")
        .minecraftName("better_barrier3")
        .item(null)
        .build();

    public static final BlockDefinition STAINED_OBSIDIAN = BlockDefinition.builder()
        .sinceProtocolVersion(14)
        .id(3079)
        .bukkitName("STAINED_OBSIDIAN")
        .minecraftName("stained_obsidian")
        .translationKey("stainedObsidian")
        .fullBlock(true)
        .lightOpacity(255)
        .translucent(false)
        .lightValue(0)
        .useNeighborBrightness(false)
        .strength(50.0F)
        .durability(6000.0F)
        .enableStats(true)
        .isTicking(false)
        .isTileEntity(false)
        .soundType(BlockDefinition.SoundType.STONE)
        .material(BlockDefinition.Material.STONE)
        .materialColor(BlockDefinition.MaterialColor.BLACK)
        .frictionFactor(0.6F)
        .pushReaction(BlockDefinition.PushReaction.BLOCK)
        .variantCount(16)
        .handler(BlockStainedObsidianHandler::new)
        .itemBlock(t -> t.hasSubtypes(true).handler(ItemStainedObsidianHandler::new))
        .build();

    public static final List<BlockDefinition> BLOCKS = Arrays.asList(
        COLORED_PORTAL,
        COLORED_PORTAL2,
        BETTER_BARRIER,
        BETTER_BARRIER2,
        BETTER_BARRIER3,
        STAINED_OBSIDIAN
    );

    public static final int AZ_BLOCKS_MIN_ID = 3072;
    public static final int AZ_BLOCKS_MAX_ID = 3840;
    public static final int AZ_BLOCKSTATES_OFFSET = 2816;

    public static boolean isAZBlock(int blockId) {
        return blockId >= AZ_BLOCKS_MIN_ID && blockId < AZ_BLOCKS_MAX_ID;
    }

    public static int computeBlockStateId(int blockId, int data) {
        if (isAZBlock(blockId)) {
            return ((blockId - AZ_BLOCKSTATES_OFFSET) << 4) | data;
        }
        return (blockId << 4) | data;
    }

    public static void assertItemBlock(@NotNull BlockDefinition block, @NotNull ItemDefinition item) {
        if (!(item.getType() instanceof ItemDefinition.ItemBlock)) {
            throw new IllegalArgumentException("BlockDefinition.item must be an ItemBlock");
        }
        if (item.getId() != block.getId()) {
            throw new IllegalArgumentException("BlockDefinition.item.id must match it's block");
        }
        if (!item.getBukkitName().equals(block.getBukkitName())) {
            throw new IllegalArgumentException("BlockDefinition.item.bukkitName must match it's block");
        }
        if (!item.getMinecraftName().equals(block.getMinecraftName())) {
            throw new IllegalArgumentException("BlockDefinition.item.minecraftName must match it's block");
        }
    }
}
