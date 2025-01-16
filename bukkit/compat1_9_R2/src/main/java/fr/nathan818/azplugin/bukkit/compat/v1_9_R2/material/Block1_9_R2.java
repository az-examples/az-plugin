package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.material;

import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_BLOCK_STATE;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_BOUNDING_BOX;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_DROPS_LIST;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_AMOUNT;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_DATA;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_ID;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_ITEM_STACK;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_MATERIAL_COLOR;
import static fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants.DEFAULT_RAY_TRACE_RESULT;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getBlockFace;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getRotation;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getWorld;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getX;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getY;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2.getZ;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockHandler;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.CollisionMode;
import fr.nathan818.azplugin.bukkit.compat.type.OptBoolean;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import net.minecraft.server.v1_9_R2.AxisAlignedBB;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.BlockStateInteger;
import net.minecraft.server.v1_9_R2.BlockStateList;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EnumBlockRotation;
import net.minecraft.server.v1_9_R2.EnumDirection;
import net.minecraft.server.v1_9_R2.EnumPistonReaction;
import net.minecraft.server.v1_9_R2.IBlockAccess;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.IBlockState;
import net.minecraft.server.v1_9_R2.Item;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.MaterialMapColor;
import net.minecraft.server.v1_9_R2.MovingObjectPosition;
import net.minecraft.server.v1_9_R2.Vec3D;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class Block1_9_R2 extends Block {

    private final @Getter BlockHandler handler;
    private final MaterialMapColor materialColor;
    private final EnumPistonReaction pushReaction;
    private IBlockState<Integer> variantProperty;
    private int variantCount;

    public Block1_9_R2(BlockDefinition definition) {
        super(Conversions1_9_R2.toNmsMaterial(definition.getMaterial()));
        c(definition.getTranslationKey());
        handler = definition.getHandler().create(definition);
        l = definition.isFullBlock();
        m = definition.getLightOpacity();
        n = definition.isTranslucent();
        o = definition.getLightValue();
        p = definition.isUseNeighborBrightness();
        strength = definition.getStrength();
        durability = definition.getDurability();
        s = definition.isEnableStats();
        t = definition.isTicking();
        isTileEntity = definition.isTileEntity();
        stepSound = Conversions1_9_R2.toNmsSoundType(definition.getSoundType());
        materialColor = Conversions1_9_R2.toNmsMaterialColor(definition.getMaterialColor());
        frictionFactor = definition.getFrictionFactor();
        pushReaction = Conversions1_9_R2.toNmsPushReaction(definition.getPushReaction());
    }

    public abstract BlockDefinition getDefinition();

    @Override
    public MaterialMapColor r(IBlockData blockState) {
        BlockDefinition.MaterialColor ret = handler.getMaterialColor(toLegacyData(blockState));
        if (ret != DEFAULT_MATERIAL_COLOR) {
            return Conversions1_9_R2.toNmsMaterialColor(ret);
        }
        return materialColor;
    }

    @Override
    public EnumPistonReaction h(IBlockData blockState) {
        return pushReaction;
    }

    @Override
    public @Nullable ItemStack a(World world, BlockPosition blockPosition, IBlockData blockState) {
        org.bukkit.inventory.ItemStack ret = handler.getItemStack(
            getWorld(world),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            toLegacyData(blockState)
        );
        if (ret != DEFAULT_ITEM_STACK) {
            return CraftItemStack.asNMSCopy(ret);
        }
        return super.a(world, blockPosition, blockState);
    }

    @Override
    public @Nullable Item getDropType(IBlockData blockState, Random random, int i) {
        int ret = handler.getDroppedItemId(toLegacyData(blockState));
        if (ret != DEFAULT_ITEM_ID) {
            return ret == 0 ? null : Item.getById(ret);
        }
        return super.getDropType(blockState, random, i);
    }

    @Override
    public int getDropData(IBlockData blockState) {
        int ret = handler.getDroppedItemData(toLegacyData(blockState));
        if (ret != DEFAULT_ITEM_DATA) {
            return ret;
        }
        return super.getDropData(blockState);
    }

    @Override
    public int a(Random random) {
        int ret = handler.getDroppedAmount(random);
        if (ret != DEFAULT_ITEM_AMOUNT) {
            return ret;
        }
        return super.a(random);
    }

    @Override
    public void dropNaturally(
        World world,
        BlockPosition blockPosition,
        IBlockData blockState,
        float chance,
        int fortune
    ) {
        List<org.bukkit.inventory.ItemStack> drops = handler.getNaturalDrops(
            getWorld(world),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            toLegacyData(blockState),
            chance,
            fortune
        );
        if (drops != DEFAULT_DROPS_LIST) {
            if (drops != null) {
                for (org.bukkit.inventory.ItemStack drop : drops) {
                    Block.a(world, blockPosition, CraftItemStack.asNMSCopy(drop));
                }
            }
            return;
        }
        super.dropNaturally(world, blockPosition, blockState, chance, fortune);
    }

    @Override
    public boolean c(IBlockData blockState) {
        OptBoolean ret = handler.isFullCube(toLegacyData(blockState));
        if (ret != OptBoolean.DEFAULT) {
            return ret.toBoolean();
        }
        return super.c(blockState);
    }

    @Override
    public boolean b(IBlockData blockState) {
        if (handler != null) { // Can be called during initialization
            OptBoolean ret = handler.isOpaqueCube(toLegacyData(blockState));
            if (ret != OptBoolean.DEFAULT) {
                return ret.toBoolean();
            }
        }
        return super.b(blockState);
    }

    @Override
    public AxisAlignedBB a(IBlockData blockState, IBlockAccess blockAccess, BlockPosition blockPosition) {
        BoundingBox ret = handler.getBoundingBox(
            getWorld(blockAccess),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            toLegacyData(blockState)
        );
        if (ret != DEFAULT_BOUNDING_BOX) {
            return Conversions1_9_R2.toNmsBoundingBox(ret);
        }
        return super.a(blockState, blockAccess, blockPosition);
    }

    @Override
    public @Nullable AxisAlignedBB a(IBlockData blockState, World world, BlockPosition blockPosition) {
        BoundingBox ret = handler.getCollisionBoundingBox(
            getWorld(world),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            toLegacyData(blockState)
        );
        if (ret != DEFAULT_BOUNDING_BOX) {
            return Conversions1_9_R2.toNmsBoundingBox(ret);
        }
        return super.a(blockState, world, blockPosition);
    }

    @Override
    public @Nullable MovingObjectPosition a(
        IBlockData blockState,
        World world,
        BlockPosition blockPosition,
        Vec3D start,
        Vec3D end
    ) {
        // Note: Assume PHYSICS mode since it's impossible to determine it from the method signature.
        //       Should use a modified spigot to correctly support this.
        //       This is only needed to fully support the BetterBarrier with server-side ray-traced interactions.
        CollisionMode collisionMode = CollisionMode.PHYSICS;
        RayTraceResult ret = handler.doCollisionRayTrace(
            getWorld(world),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            toLegacyData(blockState),
            getX(start),
            getY(start),
            getZ(start),
            getX(end),
            getY(end),
            getZ(end),
            collisionMode
        );
        if (ret != DEFAULT_RAY_TRACE_RESULT) {
            return Conversions1_9_R2.toNmsRayTraceResult(ret);
        }
        return super.a(blockState, world, blockPosition, start, end);
    }

    @Override
    public IBlockData a(IBlockData blockState, EnumBlockRotation rotation) {
        BlockState ret = handler.rotate(toLegacyData(blockState), getRotation(rotation));
        if (ret != DEFAULT_BLOCK_STATE) {
            return Conversions1_9_R2.toNmsBlockState(ret);
        }
        return super.a(blockState, rotation);
    }

    @Override
    public IBlockData getPlacedState(
        World world,
        BlockPosition blockPosition,
        EnumDirection direction,
        float hitX,
        float hitY,
        float hitZ,
        int itemData,
        EntityLiving placer
    ) {
        BlockState ret = handler.getPlaceState(
            getWorld(world),
            getX(blockPosition),
            getY(blockPosition),
            getZ(blockPosition),
            getBlockFace(direction),
            hitX,
            hitY,
            hitZ,
            itemData,
            (placer != null) ? placer.getBukkitEntity() : null
        );
        if (ret != DEFAULT_BLOCK_STATE) {
            return Conversions1_9_R2.toNmsBlockState(ret);
        }
        return super.getPlacedState(world, blockPosition, direction, hitX, hitY, hitZ, itemData, placer);
    }

    @Override
    public IBlockData fromLegacyData(int data) {
        if (variantProperty == null) {
            return getBlockData();
        }
        return getBlockData().set(variantProperty, filterLegacyData(data));
    }

    @Override
    public int toLegacyData(IBlockData blockState) {
        if (variantProperty == null) {
            return 0;
        }
        return blockState.get(variantProperty);
    }

    private int filterLegacyData(int data) {
        if (data < 0 || data >= variantCount) {
            return 0;
        }
        return data;
    }

    @Override
    protected BlockStateList getStateList() {
        if (getDefinition().getVariantCount() <= 1) {
            variantCount = 1;
            variantProperty = null;
            return super.getStateList();
        }
        if (variantProperty == null) {
            variantCount = getDefinition().getVariantCount();
            variantProperty = BlockStateInteger.of("variant", 0, variantCount - 1);
        }
        return new BlockStateList(this, variantProperty);
    }
}
