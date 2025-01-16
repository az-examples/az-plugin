package fr.nathan818.azplugin.bukkit.compat.material;

import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_BLOCK_STATE;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_BOUNDING_BOX;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_DROPS_LIST;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_AMOUNT;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_DATA;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_ID;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_STACK;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_MATERIAL_COLOR;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_RAY_TRACE_RESULT;

import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.CollisionMode;
import fr.nathan818.azplugin.bukkit.compat.type.OptBoolean;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import fr.nathan818.azplugin.bukkit.compat.type.Rotation;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.util.List;
import java.util.Random;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class BlockHandler {

    private static final BlockState STONE = new BlockState(1, 0);

    protected final @NonNull BlockDefinition definition;

    public @Nullable BlockState getFallbackState(@NotNull AZNetworkContext ctx, int blockData) {
        if (ctx.getAZProtocolVersion() >= definition.getSinceProtocolVersion()) {
            return null;
        }
        return STONE;
    }

    @Nullable
    public BlockDefinition.MaterialColor getMaterialColor(int blockData) {
        return DEFAULT_MATERIAL_COLOR;
    }

    public ItemStack getItemStack(World world, int x, int y, int z, int blockData) {
        return DEFAULT_ITEM_STACK;
    }

    public int getDroppedItemId(int blockData) {
        return DEFAULT_ITEM_ID;
    }

    public int getDroppedItemData(int blockData) {
        return DEFAULT_ITEM_DATA;
    }

    public int getDroppedAmount(Random random) {
        return DEFAULT_ITEM_AMOUNT;
    }

    public List<ItemStack> getNaturalDrops(World world, int x, int y, int z, int blockData, float chance, int fortune) {
        return DEFAULT_DROPS_LIST;
    }

    public OptBoolean isFullCube(int blockData) {
        return OptBoolean.DEFAULT;
    }

    public OptBoolean isOpaqueCube(int blockData) {
        return OptBoolean.DEFAULT;
    }

    @Nullable
    public BoundingBox getBoundingBox(World world, int x, int y, int z, int blockData) {
        return DEFAULT_BOUNDING_BOX;
    }

    @Nullable
    public BoundingBox getCollisionBoundingBox(World world, int x, int y, int z, int blockData) {
        return DEFAULT_BOUNDING_BOX;
    }

    @Nullable
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
        return DEFAULT_RAY_TRACE_RESULT;
    }

    public BlockState rotate(int blockData, Rotation rotation) {
        return DEFAULT_BLOCK_STATE;
    }

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
        return DEFAULT_BLOCK_STATE;
    }

    public interface Constructor {
        @NotNull
        BlockHandler create(@NotNull BlockDefinition definition);
    }
}
