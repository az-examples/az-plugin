package fr.nathan818.azplugin.bukkit.compat.type;

import lombok.Data;

@Data
public final class BlockPos {

    private final int x;
    private final int y;
    private final int z;

    @Override
    public String toString() {
        return "BlockPos[" + x + ", " + y + ", " + z + "]";
    }
}
