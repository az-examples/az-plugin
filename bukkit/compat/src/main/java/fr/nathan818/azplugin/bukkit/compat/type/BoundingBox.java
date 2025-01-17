package fr.nathan818.azplugin.bukkit.compat.type;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.util.NumberConversions;

@Getter
@EqualsAndHashCode
public final class BoundingBox {

    public static BoundingBox of(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new BoundingBox(x1, y1, z1, x2, y2, z2);
    }

    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    /**
     * This field is used by implementations for performance reasons.
     * <p>
     * Each BoundingBox should only be used by one implementation at a time.
     *
     * @deprecated internal use
     */
    @Deprecated
    @Getter(AccessLevel.NONE)
    public Object nmsCache;

    BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(y1, "y1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(y2, "y2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public BoundingBox rotate(Rotation rotation) {
        switch (rotation) {
            case NONE:
                return this;
            case CLOCKWISE_90:
                return of(1.0D - maxZ, minY, minX, 1.0D - minZ, maxY, maxX);
            case CLOCKWISE_180:
                return of(1.0D - maxX, minY, 1.0D - maxZ, 1.0D - minX, maxY, 1.0D - minZ);
            case COUNTERCLOCKWISE_90:
                return of(minZ, minY, 1.0D - maxX, maxZ, maxY, 1.0D - minX);
            default:
                throw new IllegalArgumentException("Unknown rotation: " + rotation);
        }
    }

    public BoundingBox expand(double x, double y, double z) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;
        if (x < 0.0D) {
            minX += x;
        } else if (x > 0.0D) {
            maxX += x;
        }
        if (y < 0.0D) {
            minY += y;
        } else if (y > 0.0D) {
            maxY += y;
        }
        if (z < 0.0D) {
            minZ += z;
        } else if (z > 0.0D) {
            maxZ += z;
        }
        return of(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return (
            "BoundingBox[" +
            getMinX() +
            ", " +
            getMinY() +
            ", " +
            getMinZ() +
            " -> " +
            getMaxX() +
            ", " +
            getMaxY() +
            ", " +
            getMaxZ() +
            "]"
        );
    }
}
