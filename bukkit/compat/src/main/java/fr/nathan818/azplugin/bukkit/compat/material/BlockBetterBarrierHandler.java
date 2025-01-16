package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.CollisionMode;
import fr.nathan818.azplugin.bukkit.compat.type.OptBoolean;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import fr.nathan818.azplugin.bukkit.compat.type.Rotation;
import fr.nathan818.azplugin.bukkit.compat.util.BlockPlaceUtil;
import fr.nathan818.azplugin.bukkit.compat.util.MathUtil;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockBetterBarrierHandler extends BlockHandler {

    private static final BlockState BARRIER = new BlockState(166, 0);

    private static final double PANE_LEN = 0.125D;
    private static final double WALL_LEN = 0.5D;
    private static final BoundingBox BBOX_EMPTY = BoundingBox.of(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static final BoundingBox BBOX_FULL = BoundingBox.of(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final BoundingBox BBOX_SLAB_BOTTOM = BoundingBox.of(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    private static final BoundingBox BBOX_SLAB_TOP = BoundingBox.of(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final BoundingBox BBOX_PANE_N = BoundingBox.of(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, PANE_LEN);
    private static final BoundingBox BBOX_PANE_S = BBOX_PANE_N.rotate(Rotation.CLOCKWISE_180);
    private static final BoundingBox BBOX_PANE_W = BBOX_PANE_N.rotate(Rotation.COUNTERCLOCKWISE_90);
    private static final BoundingBox BBOX_PANE_E = BBOX_PANE_N.rotate(Rotation.CLOCKWISE_90);
    private static final BoundingBox BBOX_PANE_OUT_N = BoundingBox.of(0.0D, 0.0D, -PANE_LEN, 1.0D, 1.0D, 0.0D);
    private static final BoundingBox BBOX_PANE_OUT_S = BBOX_PANE_OUT_N.rotate(Rotation.CLOCKWISE_180);
    private static final BoundingBox BBOX_PANE_OUT_W = BBOX_PANE_OUT_N.rotate(Rotation.COUNTERCLOCKWISE_90);
    private static final BoundingBox BBOX_PANE_OUT_E = BBOX_PANE_OUT_N.rotate(Rotation.CLOCKWISE_90);
    private static final BoundingBox BBOX_PANE_OUT_D = BoundingBox.of(0.0D, -PANE_LEN, 0.0D, 1.0D, 0.0D, 1.0D);
    private static final BoundingBox BBOX_PANE_OUT_U = BoundingBox.of(0.0D, 1.0D, 0.0D, 1.0D, 1.0D + PANE_LEN, 1.0D);
    private static final BoundingBox BBOX_WALL_N = BoundingBox.of(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, WALL_LEN);
    private static final BoundingBox BBOX_WALL_S = BBOX_WALL_N.rotate(Rotation.CLOCKWISE_180);
    private static final BoundingBox BBOX_WALL_W = BBOX_WALL_N.rotate(Rotation.COUNTERCLOCKWISE_90);
    private static final BoundingBox BBOX_WALL_E = BBOX_WALL_N.rotate(Rotation.CLOCKWISE_90);
    private static final BoundingBox BBOX_CORNER_NW = BoundingBox.of(0.0D, 0.0D, 0.0D, WALL_LEN, 1.0D, WALL_LEN);
    private static final BoundingBox BBOX_CORNER_NE = BBOX_CORNER_NW.rotate(Rotation.CLOCKWISE_90);
    private static final BoundingBox BBOX_CORNER_SW = BBOX_CORNER_NW.rotate(Rotation.COUNTERCLOCKWISE_90);
    private static final BoundingBox BBOX_CORNER_SE = BBOX_CORNER_NW.rotate(Rotation.CLOCKWISE_180);

    private static final Dimension[] STANDARD_DIMENSIONS_BY_DATA = {
        Dimension.FULL,
        null,
        null,
        null,
        Dimension.PANE_N,
        Dimension.PANE_S,
        Dimension.PANE_W,
        Dimension.PANE_E,
        Dimension.WALL_N,
        Dimension.WALL_S,
        Dimension.WALL_W,
        Dimension.WALL_E,
        Dimension.CORNER_NW,
        Dimension.CORNER_NE,
        Dimension.CORNER_SW,
        Dimension.CORNER_SE,
    };
    private static final Variant[][] VARIANTS_BY_BLOCK_INDEX = new Variant[3][16];
    private static final Map<Dimension, Map<Expansion, Variant>> VARIANTS_BY_PROPS = new EnumMap<>(Dimension.class);

    static {
        initVariants(BlockDefinitions.BETTER_BARRIER);
        initVariants(BlockDefinitions.BETTER_BARRIER2);
        initVariants(BlockDefinitions.BETTER_BARRIER3);
        for (Variant[] variants : VARIANTS_BY_BLOCK_INDEX) {
            for (Variant variant : variants) {
                VARIANTS_BY_PROPS.computeIfAbsent(variant.getDimension(), k -> new EnumMap<>(Expansion.class)).put(
                    variant.getExpansion(),
                    variant
                );
            }
        }
    }

    private static void initVariants(BlockDefinition definition) {
        int blockIndex = getBlockIndex(definition);
        Variant[] variants = VARIANTS_BY_BLOCK_INDEX[blockIndex];
        Expansion baseExpansion = Expansion.NONE;
        switch (blockIndex) {
            default:
                variants[1] = createVariant(definition, 1, Dimension.PANE_OUT_N, baseExpansion);
                variants[2] = createVariant(definition, 2, Dimension.SLAB_BOTTOM, baseExpansion);
                variants[3] = createVariant(definition, 3, Dimension.SLAB_TOP, baseExpansion);
                break;
            case 1:
                variants[1] = createVariant(definition, 1, Dimension.PANE_OUT_S, baseExpansion);
                variants[2] = createVariant(definition, 2, Dimension.PANE_OUT_W, baseExpansion);
                variants[3] = createVariant(definition, 3, Dimension.PANE_OUT_E, baseExpansion);
                baseExpansion = Expansion.UP;
                break;
            case 2:
                variants[1] = createVariant(definition, 1, Dimension.PANE_OUT_D, baseExpansion);
                variants[2] = createVariant(definition, 2, Dimension.PANE_OUT_U, baseExpansion);
                variants[3] = createVariant(definition, 3, Dimension.FULL, baseExpansion); // Note: Reserved for future use
                baseExpansion = Expansion.DOWN;
                break;
        }
        for (int blockData = 0; blockData < variants.length; blockData++) {
            Dimension defaultDimension = STANDARD_DIMENSIONS_BY_DATA[blockData];
            if (defaultDimension != null && variants[blockData] == null) {
                variants[blockData] = createVariant(definition, blockData, defaultDimension, baseExpansion);
            }
        }
    }

    private static Variant createVariant(
        BlockDefinition definition,
        int blockData,
        Dimension dimension,
        Expansion expansion
    ) {
        return new Variant(new BlockState(definition.getId(), blockData), dimension, expansion);
    }

    public static int getBlockIndex(BlockDefinition definition) {
        if (definition == BlockDefinitions.BETTER_BARRIER3) {
            return 2;
        } else if (definition == BlockDefinitions.BETTER_BARRIER2) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Variant getVariant(int blockIndex, int blockData) {
        if (blockIndex < 0 || blockIndex >= VARIANTS_BY_BLOCK_INDEX.length) {
            return VARIANTS_BY_BLOCK_INDEX[0][0];
        }
        if (blockData < 0 || blockData >= VARIANTS_BY_BLOCK_INDEX[blockIndex].length) {
            return VARIANTS_BY_BLOCK_INDEX[blockIndex][0];
        }
        return VARIANTS_BY_BLOCK_INDEX[blockIndex][blockData];
    }

    private static Variant getVariant(@NotNull Dimension dimension, @NotNull Expansion expansion) {
        return VARIANTS_BY_PROPS.get(dimension).get(expansion);
    }

    public static Dimension getDimension(int blockIndex, int blockData) {
        return getVariant(blockIndex, blockData).getDimension();
    }

    public static Expansion getExpansion(int blockIndex, int blockData) {
        return getVariant(blockIndex, blockData).getExpansion();
    }

    private final int blockIndex;

    public BlockBetterBarrierHandler(@NonNull BlockDefinition definition) {
        super(definition);
        blockIndex = getBlockIndex(definition);
    }

    @Override
    public @Nullable BlockState getFallbackState(@NotNull AZNetworkContext ctx, int blockData) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return BARRIER;
    }

    @Override
    public ItemStack getItemStack(World world, int x, int y, int z, int blockData) {
        ItemBetterBarrierHandler.Variant itemVariant = getVariant(blockIndex, blockData).getItemVariant();
        if (itemVariant == null) {
            return null;
        }
        return new ItemStack(BlockDefinitions.BETTER_BARRIER.getId(), 1, (short) itemVariant.getIndex());
    }

    @Override
    public List<ItemStack> getNaturalDrops(World world, int x, int y, int z, int blockData, float chance, int fortune) {
        return Collections.emptyList();
    }

    @Override
    public OptBoolean isFullCube(int blockData) {
        return OptBoolean.fromBoolean(getDimension(blockIndex, blockData) == Dimension.FULL);
    }

    @Override
    public OptBoolean isOpaqueCube(int blockData) {
        return OptBoolean.FALSE;
    }

    @Override
    public BoundingBox getBoundingBox(World world, int x, int y, int z, int blockData) {
        return BBOX_EMPTY;
    }

    @Override
    public BoundingBox getCollisionBoundingBox(World world, int x, int y, int z, int blockData) {
        return getVariant(blockIndex, blockData).getCollisionBbox();
    }

    @Override
    public RayTraceResult doCollisionRayTrace(
        World world,
        int blockX,
        int blockY,
        int blockZ,
        int blockData,
        double startX,
        double startY,
        double startZ,
        double endX,
        double endY,
        double endZ,
        CollisionMode collisionMode
    ) {
        BoundingBox bbox;
        if (collisionMode == CollisionMode.PHYSICS) {
            bbox = getCollisionBoundingBox(world, blockX, blockY, blockZ, blockData);
        } else {
            bbox = getBoundingBox(world, blockX, blockY, blockZ, blockData);
        }
        if (bbox == null) {
            return null;
        }
        return MathUtil.blockRayTrace(blockX, blockY, blockZ, startX, startY, startZ, endX, endY, endZ, bbox);
    }

    @Override
    public BlockState getPlaceState(
        World world,
        int x,
        int y,
        int z,
        BlockFace face,
        float hitX,
        float hitY,
        float hitZ,
        int itemData,
        Entity placer
    ) {
        ItemBetterBarrierHandler.Variant itemVariant = ItemBetterBarrierHandler.Variant.byItemIndex(itemData);
        Expansion expansion = Expansion.fromItem(itemVariant);
        Dimension dimension;
        switch (itemVariant) {
            default:
                dimension = onFullPlaced(face, hitX, hitY, hitZ);
                break;
            case SLAB:
                dimension = onSlabPlaced(face, hitX, hitY, hitZ);
                break;
            case PANE:
            case PANE_UP:
            case PANE_DOWN:
                dimension = onPanePlaced(face, hitX, hitY, hitZ);
                break;
            case PANE_OUT:
                dimension = onPaneOutPlaced(face, hitX, hitY, hitZ);
                break;
            case WALL:
            case WALL_UP:
            case WALL_DOWN:
                dimension = onWallPlaced(face, hitX, hitY, hitZ);
                break;
            case CORNER:
            case CORNER_UP:
            case CORNER_DOWN:
                dimension = onCornerPlaced(face, hitX, hitY, hitZ);
                break;
        }
        return getVariant(dimension, expansion).getBlockState();
    }

    private Dimension onFullPlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        return Dimension.FULL;
    }

    private Dimension onSlabPlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        switch (BlockPlaceUtil.getVerticalFacing(face, hitX, hitY, hitZ)) {
            default:
                return Dimension.SLAB_BOTTOM;
            case UP:
                return Dimension.SLAB_TOP;
        }
    }

    private Dimension onPanePlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        switch (BlockPlaceUtil.getHorizontalFacing(face, hitX, hitY, hitZ)) {
            default:
                return Dimension.PANE_N;
            case SOUTH:
                return Dimension.PANE_S;
            case WEST:
                return Dimension.PANE_W;
            case EAST:
                return Dimension.PANE_E;
        }
    }

    private Dimension onPaneOutPlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        switch (BlockPlaceUtil.getAnyFacing(face, hitX, hitY, hitZ, 3.0F / 16.0F)) {
            default:
                return Dimension.PANE_OUT_N;
            case SOUTH:
                return Dimension.PANE_OUT_S;
            case WEST:
                return Dimension.PANE_OUT_W;
            case EAST:
                return Dimension.PANE_OUT_E;
            case DOWN:
                return Dimension.PANE_OUT_D;
            case UP:
                return Dimension.PANE_OUT_U;
        }
    }

    private Dimension onWallPlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        switch (BlockPlaceUtil.getHorizontalFacing(face, hitX, hitY, hitZ)) {
            default:
                return Dimension.WALL_N;
            case SOUTH:
                return Dimension.WALL_S;
            case WEST:
                return Dimension.WALL_W;
            case EAST:
                return Dimension.WALL_E;
        }
    }

    private Dimension onCornerPlaced(BlockFace face, float hitX, float hitY, float hitZ) {
        switch (face) {
            default:
                return hitX < 0.5D ? Dimension.CORNER_SW : Dimension.CORNER_SE;
            case SOUTH:
                return hitX < 0.5D ? Dimension.CORNER_NW : Dimension.CORNER_NE;
            case WEST:
                return hitZ < 0.5D ? Dimension.CORNER_NE : Dimension.CORNER_SE;
            case EAST:
                return hitZ < 0.5D ? Dimension.CORNER_NW : Dimension.CORNER_SW;
            case UP:
            case DOWN:
                if (hitZ < 0.5D) {
                    return hitX < 0.5D ? Dimension.CORNER_NW : Dimension.CORNER_NE;
                } else {
                    return hitX < 0.5D ? Dimension.CORNER_SW : Dimension.CORNER_SE;
                }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static final class Variant {

        private final @NonNull BlockState blockState;
        private final @NonNull Dimension dimension;
        private final @NonNull Expansion expansion;

        @NotNull
        public BoundingBox getCollisionBbox() {
            return dimension.collisionBbox.get(expansion);
        }

        @Nullable
        public ItemBetterBarrierHandler.Variant getItemVariant() {
            return dimension.itemVariants.get(expansion);
        }
    }

    public enum Dimension {
        FULL("full", BBOX_FULL, ItemBetterBarrierHandler.Variant.FULL),
        SLAB_BOTTOM("slab_bottom", BBOX_SLAB_BOTTOM, ItemBetterBarrierHandler.Variant.SLAB),
        SLAB_TOP("slab_top", BBOX_SLAB_TOP, ItemBetterBarrierHandler.Variant.SLAB),
        PANE_N("pane_n", BBOX_PANE_N, ItemBetterBarrierHandler.Variant.PANE),
        PANE_S("pane_s", BBOX_PANE_S, ItemBetterBarrierHandler.Variant.PANE),
        PANE_W("pane_w", BBOX_PANE_W, ItemBetterBarrierHandler.Variant.PANE),
        PANE_E("pane_e", BBOX_PANE_E, ItemBetterBarrierHandler.Variant.PANE),
        PANE_OUT_N("pane_out_n", BBOX_PANE_OUT_N, ItemBetterBarrierHandler.Variant.PANE_OUT),
        PANE_OUT_S("pane_out_s", BBOX_PANE_OUT_S, ItemBetterBarrierHandler.Variant.PANE_OUT),
        PANE_OUT_W("pane_out_w", BBOX_PANE_OUT_W, ItemBetterBarrierHandler.Variant.PANE_OUT),
        PANE_OUT_E("pane_out_e", BBOX_PANE_OUT_E, ItemBetterBarrierHandler.Variant.PANE_OUT),
        PANE_OUT_D("pane_out_d", BBOX_PANE_OUT_D, ItemBetterBarrierHandler.Variant.PANE_OUT),
        PANE_OUT_U("pane_out_u", BBOX_PANE_OUT_U, ItemBetterBarrierHandler.Variant.PANE_OUT),
        WALL_N("wall_n", BBOX_WALL_N, ItemBetterBarrierHandler.Variant.WALL),
        WALL_S("wall_s", BBOX_WALL_S, ItemBetterBarrierHandler.Variant.WALL),
        WALL_W("wall_w", BBOX_WALL_W, ItemBetterBarrierHandler.Variant.WALL),
        WALL_E("wall_e", BBOX_WALL_E, ItemBetterBarrierHandler.Variant.WALL),
        CORNER_NW("corner_nw", BBOX_CORNER_NW, ItemBetterBarrierHandler.Variant.CORNER),
        CORNER_NE("corner_ne", BBOX_CORNER_NE, ItemBetterBarrierHandler.Variant.CORNER),
        CORNER_SW("corner_sw", BBOX_CORNER_SW, ItemBetterBarrierHandler.Variant.CORNER),
        CORNER_SE("corner_se", BBOX_CORNER_SE, ItemBetterBarrierHandler.Variant.CORNER);

        private final String name;
        private final EnumMap<Expansion, BoundingBox> collisionBbox = new EnumMap<>(Expansion.class);
        private final EnumMap<Expansion, ItemBetterBarrierHandler.Variant> itemVariants = new EnumMap<>(
            Expansion.class
        );

        Dimension(String name, BoundingBox bbox, ItemBetterBarrierHandler.Variant itemVariant) {
            this.name = name;

            collisionBbox.put(Expansion.NONE, bbox);
            collisionBbox.put(Expansion.UP, bbox.expand(0.0D, 1.0D, 0.0D));
            collisionBbox.put(Expansion.DOWN, bbox.expand(0.0D, -1.0D, 0.0D));

            itemVariants.put(Expansion.NONE, itemVariant);
            for (Expansion expansion : Expansion.values()) {
                if (expansion != Expansion.NONE) {
                    ItemBetterBarrierHandler.Variant itemVariantExpansion = ItemBetterBarrierHandler.Variant.byName(
                        itemVariant.getName() + '_' + expansion.getName()
                    );
                    if (itemVariantExpansion != null) {
                        itemVariants.put(expansion, itemVariantExpansion);
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum Expansion {
        NONE("none"),
        UP("up"),
        DOWN("down");

        @NotNull
        public static Expansion fromItem(@NotNull ItemBetterBarrierHandler.Variant itemVariant) {
            switch (itemVariant) {
                case FULL_UP:
                case PANE_UP:
                case WALL_UP:
                case CORNER_UP:
                    return Expansion.UP;
                case FULL_DOWN:
                case PANE_DOWN:
                case WALL_DOWN:
                case CORNER_DOWN:
                    return Expansion.DOWN;
                default:
                    return Expansion.NONE;
            }
        }

        private final String name;
    }
}
