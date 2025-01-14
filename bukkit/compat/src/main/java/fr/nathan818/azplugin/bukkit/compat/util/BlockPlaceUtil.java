package fr.nathan818.azplugin.bukkit.compat.util;

import fr.nathan818.azplugin.bukkit.compat.type.Axis;
import lombok.experimental.UtilityClass;
import org.bukkit.block.BlockFace;

@UtilityClass
public class BlockPlaceUtil {

    public static BlockFace getAnyFacing(BlockFace hitFace, float hitX, float hitY, float hitZ, float borderThreshold) {
        float sideDist;
        switch (hitFace) {
            default:
                sideDist = hitZ;
                break;
            case SOUTH:
                sideDist = 1.0F - hitZ;
                break;
            case WEST:
                sideDist = hitX;
                break;
            case EAST:
                sideDist = 1.0F - hitX;
                break;
            case DOWN:
                sideDist = hitY;
                break;
            case UP:
                sideDist = 1.0F - hitY;
                break;
        }
        if (sideDist > borderThreshold) {
            return hitFace.getOppositeFace();
        }

        switch (Axis.ofBlockFace(hitFace)) {
            default:
                return getAnyFacing0(
                    hitZ,
                    hitY,
                    borderThreshold,
                    hitFace,
                    BlockFace.NORTH,
                    BlockFace.SOUTH,
                    BlockFace.DOWN,
                    BlockFace.UP
                );
            case Y:
                return getAnyFacing0(
                    hitX,
                    hitZ,
                    borderThreshold,
                    hitFace,
                    BlockFace.WEST,
                    BlockFace.EAST,
                    BlockFace.NORTH,
                    BlockFace.SOUTH
                );
            case Z:
                return getAnyFacing0(
                    hitX,
                    hitY,
                    borderThreshold,
                    hitFace,
                    BlockFace.WEST,
                    BlockFace.EAST,
                    BlockFace.DOWN,
                    BlockFace.UP
                );
        }
    }

    private static BlockFace getAnyFacing0(
        float hitX,
        float hitZ,
        float borderThreshold,
        BlockFace hitFace,
        BlockFace facingX1,
        BlockFace facingX2,
        BlockFace facingZ1,
        BlockFace facingZ2
    ) {
        if (
            hitX >= borderThreshold &&
            hitX < 1.0F - borderThreshold &&
            hitZ >= borderThreshold &&
            hitZ < 1.0F - borderThreshold
        ) {
            return hitFace.getOppositeFace();
        }

        float x1Dist = Math.abs(hitX);
        float x2Dist = Math.abs(hitX - 1.0F);
        float z1Dist = Math.abs(hitZ);
        float z2Dist = Math.abs(hitZ - 1.0F);
        float minDist = Math.min(Math.min(x1Dist, x2Dist), Math.min(z1Dist, z2Dist));
        if (minDist == x1Dist) {
            return facingX1;
        } else if (minDist == x2Dist) {
            return facingX2;
        } else if (minDist == z1Dist) {
            return facingZ1;
        } else {
            return facingZ2;
        }
    }

    public static BlockFace getHorizontalFacing(BlockFace hitFace, float hitX, float hitY, float hitZ) {
        if (Axis.ofBlockFace(hitFace).isHorizontal()) {
            return hitFace.getOppositeFace();
        }

        double northDist = Math.abs(hitZ);
        double southDist = Math.abs(hitZ - 1.0);
        double westDist = Math.abs(hitX);
        double eastDist = Math.abs(hitX - 1.0);
        double minDist = Math.min(Math.min(northDist, southDist), Math.min(westDist, eastDist));
        if (minDist == northDist) {
            return BlockFace.NORTH;
        } else if (minDist == southDist) {
            return BlockFace.SOUTH;
        } else if (minDist == westDist) {
            return BlockFace.WEST;
        } else {
            return BlockFace.EAST;
        }
    }

    public static BlockFace getVerticalFacing(BlockFace hitFace, float hitX, float hitY, float hitZ) {
        if (Axis.ofBlockFace(hitFace).isVertical()) {
            return hitFace.getOppositeFace();
        }

        if (hitY < 0.5D) {
            return BlockFace.DOWN;
        } else {
            return BlockFace.UP;
        }
    }
}
