package fr.nathan818.azplugin.bukkit.compat.type;

import org.bukkit.block.BlockFace;

public enum Axis {
    X,
    Y,
    Z;

    public static Axis ofBlockFace(BlockFace face) {
        switch (face) {
            case NORTH:
            case SOUTH:
                return Z;
            case EAST:
            case WEST:
                return X;
            case UP:
            case DOWN:
                return Y;
            default:
                throw new IllegalArgumentException("Unsupported direction: " + face);
        }
    }

    public boolean isHorizontal() {
        return this != Y;
    }

    public boolean isVertical() {
        return this == Y;
    }
}
