package fr.nathan818.azplugin.bukkit.compat.material;

import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;

import fr.nathan818.azplugin.bukkit.compat.type.Axis;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import fr.nathan818.azplugin.bukkit.compat.type.OptBoolean;
import fr.nathan818.azplugin.bukkit.compat.type.Rotation;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.util.Random;
import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockColoredPortalHandler extends BlockHandler {

    private static final BlockState PORTAL_X = new BlockState(90, 1);
    private static final BlockState PORTAL_Z = new BlockState(90, 2);

    private static final BoundingBox BBOX_X = BoundingBox.of(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    private static final BoundingBox BBOX_Z = BoundingBox.of(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);

    public static boolean isSecond(BlockDefinition definition) {
        return (definition == BlockDefinitions.COLORED_PORTAL2);
    }

    public static DyeColor getColor(boolean isSecond, int blockData) {
        return DyeColor.byItemIndex((isSecond ? 8 : 0) + (blockData / 2));
    }

    public static Axis getAxis(boolean isSecond, int blockData) {
        return ((blockData & 1) == 0) ? Axis.X : Axis.Z;
    }

    public static BlockState getState(DyeColor color, Axis axis) {
        int axisData = (axis == Axis.Z) ? 1 : 0;
        if (color.getItemIndex() < 8) {
            return new BlockState(BlockDefinitions.COLORED_PORTAL.getId(), (color.getItemIndex() << 1) | axisData);
        } else {
            return new BlockState(
                BlockDefinitions.COLORED_PORTAL2.getId(),
                ((color.getItemIndex() - 8) << 1) | axisData
            );
        }
    }

    private final boolean isSecond;

    public BlockColoredPortalHandler(@NonNull BlockDefinition definition) {
        super(definition);
        isSecond = isSecond(definition);
    }

    @Override
    public @Nullable BlockState getFallbackState(@NotNull AZNetworkContext ctx, int blockData) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return getAxis(isSecond, blockData) == Axis.Z ? PORTAL_Z : PORTAL_X;
    }

    @Override
    public ItemStack getItemStack(World world, int x, int y, int z, int blockData) {
        return new ItemStack(
            BlockDefinitions.COLORED_PORTAL.getId(),
            1,
            (short) getColor(isSecond, blockData).getItemIndex()
        );
    }

    @Override
    public int getDroppedAmount(Random random) {
        return 0;
    }

    @Override
    public OptBoolean isFullCube(int blockData) {
        return OptBoolean.FALSE;
    }

    @Override
    public OptBoolean isOpaqueCube(int blockData) {
        return OptBoolean.FALSE;
    }

    @Override
    public BoundingBox getBoundingBox(World world, int x, int y, int z, int blockData) {
        return getAxis(isSecond, blockData) == Axis.Z ? BBOX_Z : BBOX_X;
    }

    @Override
    public BoundingBox getCollisionBoundingBox(World world, int x, int y, int z, int blockData) {
        return null;
    }

    @Override
    public BlockState rotate(int blockData, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                if ((blockData & 1) == 0) {
                    return new BlockState(definition.getId(), blockData | 1);
                } else {
                    return new BlockState(definition.getId(), blockData & ~1);
                }
            default:
                return new BlockState(definition.getId(), blockData);
        }
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
        BlockFace direction = (placer != null) ? compat().getEntityDirection(placer) : BlockFace.NORTH;
        Axis axis = (direction != null && direction.getModX() != 0) ? Axis.Z : Axis.X;
        DyeColor color = DyeColor.byItemIndex(itemData);
        return getState(color, axis);
    }
}
