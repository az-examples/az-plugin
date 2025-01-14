package fr.nathan818.azplugin.bukkit.compat.util;

import fr.nathan818.azplugin.bukkit.compat.type.BlockPos;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import lombok.experimental.UtilityClass;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class MathUtil {

    public static int floor(double value) {
        int intValue = (int) value;
        return value < (double) intValue ? intValue - 1 : intValue;
    }

    public static RayTraceResult blockRayTrace(
        int blockX,
        int blockY,
        int blockZ,
        double startX,
        double startY,
        double startZ,
        double endX,
        double endY,
        double endZ,
        @NotNull BoundingBox bbox
    ) {
        // See Spigot 1.9.4, Block.a(BlockPosition, Vec3D, Vec3D, AxisAlignedBB): MovingObjectPosition
        Vector start = new Vector(startX - blockX, startY - blockY, startZ - blockZ);
        Vector end = new Vector(endX - blockX, endY - blockY, endZ - blockZ);
        Vector hitPosition = getIntersectionWithXPlane(bbox, bbox.getMinX(), start, end);
        BlockFace hitFace = BlockFace.WEST;
        Vector intersect = getIntersectionWithXPlane(bbox, bbox.getMaxX(), start, end);
        if (intersect != null && isCloser(start, hitPosition, intersect)) {
            hitPosition = intersect;
            hitFace = BlockFace.EAST;
        }
        intersect = getIntersectionWithYPlane(bbox, bbox.getMinY(), start, end);
        if (intersect != null && isCloser(start, hitPosition, intersect)) {
            hitPosition = intersect;
            hitFace = BlockFace.DOWN;
        }
        intersect = getIntersectionWithYPlane(bbox, bbox.getMaxY(), start, end);
        if (intersect != null && isCloser(start, hitPosition, intersect)) {
            hitPosition = intersect;
            hitFace = BlockFace.UP;
        }
        intersect = getIntersectionWithZPlane(bbox, bbox.getMinZ(), start, end);
        if (intersect != null && isCloser(start, hitPosition, intersect)) {
            hitPosition = intersect;
            hitFace = BlockFace.NORTH;
        }
        intersect = getIntersectionWithZPlane(bbox, bbox.getMaxZ(), start, end);
        if (intersect != null && isCloser(start, hitPosition, intersect)) {
            hitPosition = intersect;
            hitFace = BlockFace.SOUTH;
        }
        if (hitPosition == null) {
            return null;
        }
        return new RayTraceResult(
            new Vector(hitPosition.getX() + blockX, hitPosition.getY() + blockY, hitPosition.getZ() + blockZ),
            new BlockPos(blockX, blockY, blockZ),
            hitFace
        );
    }

    private static boolean isCloser(Vector start, @Nullable Vector currentClosest, Vector candidate) {
        return currentClosest == null || start.distanceSquared(candidate) < start.distanceSquared(currentClosest);
    }

    private static @Nullable Vector getIntersectionWithXPlane(BoundingBox bbox, double x, Vector start, Vector end) {
        Vector intersect = calculateIntersectionWithXPlane(start, end, x);
        return (intersect != null && isWithinYZBounds(bbox, intersect)) ? intersect : null;
    }

    private static @Nullable Vector getIntersectionWithYPlane(BoundingBox bbox, double y, Vector start, Vector end) {
        Vector intersect = calculateIntersectionWithYPlane(start, end, y);
        return (intersect != null && isWithinXZBounds(bbox, intersect)) ? intersect : null;
    }

    private static @Nullable Vector getIntersectionWithZPlane(BoundingBox bbox, double z, Vector start, Vector end) {
        Vector intersect = calculateIntersectionWithZPlane(start, end, z);
        return (intersect != null && isWithinXYBounds(bbox, intersect)) ? intersect : null;
    }

    private static boolean isWithinYZBounds(BoundingBox bbox, Vector point) {
        return (
            point.getY() >= bbox.getMinY() &&
            point.getY() <= bbox.getMaxY() &&
            point.getZ() >= bbox.getMinZ() &&
            point.getZ() <= bbox.getMaxZ()
        );
    }

    private static boolean isWithinXZBounds(BoundingBox bbox, Vector point) {
        return (
            point.getX() >= bbox.getMinX() &&
            point.getX() <= bbox.getMaxX() &&
            point.getZ() >= bbox.getMinZ() &&
            point.getZ() <= bbox.getMaxZ()
        );
    }

    private static boolean isWithinXYBounds(BoundingBox bbox, Vector point) {
        return (
            point.getX() >= bbox.getMinX() &&
            point.getX() <= bbox.getMaxX() &&
            point.getY() >= bbox.getMinY() &&
            point.getY() <= bbox.getMaxY()
        );
    }

    private static @Nullable Vector calculateIntersectionWithXPlane(Vector start, Vector end, double x) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double deltaZ = end.getZ() - start.getZ();
        if (deltaX * deltaX < (double) 1.0E-7F) {
            return null;
        }
        double t = (x - start.getX()) / deltaX;
        if (t < 0.0D || t > 1.0D) {
            return null;
        }
        return new Vector(start.getX() + deltaX * t, start.getY() + deltaY * t, start.getZ() + deltaZ * t);
    }

    private static @Nullable Vector calculateIntersectionWithYPlane(Vector start, Vector end, double y) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double deltaZ = end.getZ() - start.getZ();
        if (deltaY * deltaY < (double) 1.0E-7F) {
            return null;
        }
        double t = (y - start.getY()) / deltaY;
        if (t < 0.0D || t > 1.0D) {
            return null;
        }
        return new Vector(start.getX() + deltaX * t, start.getY() + deltaY * t, start.getZ() + deltaZ * t);
    }

    private static @Nullable Vector calculateIntersectionWithZPlane(Vector start, Vector end, double z) {
        double deltaX = end.getX() - start.getX();
        double deltaY = end.getY() - start.getY();
        double deltaZ = end.getZ() - start.getZ();
        if (deltaZ * deltaZ < (double) 1.0E-7F) {
            return null;
        }
        double t = (z - start.getZ()) / deltaZ;
        if (t < 0.0D || t > 1.0D) {
            return null;
        }
        return new Vector(start.getX() + deltaX * t, start.getY() + deltaY * t, start.getZ() + deltaZ * t);
    }
}
