package fr.nathan818.azplugin.bukkit.compat.type;

import lombok.EqualsAndHashCode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
public final class RayTraceResult {

    private final @NotNull Vector hitPosition;

    private final @Nullable BlockPos hitBlock;
    private final @Nullable BlockFace hitBlockFace;
    private final @Nullable Entity hitEntity;

    private RayTraceResult(
        @NotNull Vector hitPosition,
        @Nullable BlockPos hitBlock,
        @Nullable BlockFace hitBlockFace,
        @Nullable Entity hitEntity
    ) {
        this.hitPosition = hitPosition.clone();
        this.hitBlock = hitBlock;
        this.hitBlockFace = hitBlockFace;
        this.hitEntity = hitEntity;
    }

    public RayTraceResult(@NotNull Vector hitPosition) {
        this(hitPosition, null, null, null);
    }

    public RayTraceResult(@NotNull Vector hitPosition, BlockPos hitBlock, BlockFace hitBlockFace) {
        this(hitPosition, hitBlock, hitBlockFace, null);
    }

    public RayTraceResult(@NotNull Vector hitPosition, BlockFace hitBlockFace) {
        this(hitPosition, null, hitBlockFace, null);
    }

    public RayTraceResult(@NotNull Vector hitPosition, Entity hitEntity) {
        this(hitPosition, null, null, hitEntity);
    }

    public RayTraceResult(@NotNull Vector hitPosition, Entity hitEntity, BlockFace hitBlockFace) {
        this(hitPosition, null, hitBlockFace, hitEntity);
    }

    public Vector getHitPosition() {
        return hitPosition.clone();
    }

    public double getHitPositionX() {
        return hitPosition.getX();
    }

    public double getHitPositionY() {
        return hitPosition.getY();
    }

    public double getHitPositionZ() {
        return hitPosition.getZ();
    }

    @Nullable
    public BlockPos getHitBlock() {
        return hitBlock;
    }

    @Nullable
    public BlockFace getHitBlockFace() {
        return hitBlockFace;
    }

    @Nullable
    public Entity getHitEntity() {
        return hitEntity;
    }

    @Override
    public String toString() {
        return (
            "RayTraceResult [hitPosition=" +
            hitPosition +
            ", hitBlock=" +
            hitBlock +
            ", hitBlockFace=" +
            hitBlockFace +
            ", hitEntity=" +
            hitEntity +
            "]"
        );
    }
}
