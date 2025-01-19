package fr.nathan818.azplugin.bukkit.compat.v1_9_R2;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.type.BlockPos;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.EquipmentSlot;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import fr.nathan818.azplugin.bukkit.compat.type.Rotation;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_9_R2.AxisAlignedBB;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.EnumBlockRotation;
import net.minecraft.server.v1_9_R2.EnumDirection;
import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.EnumPistonReaction;
import net.minecraft.server.v1_9_R2.IBlockAccess;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.Material;
import net.minecraft.server.v1_9_R2.MaterialMapColor;
import net.minecraft.server.v1_9_R2.MovingObjectPosition;
import net.minecraft.server.v1_9_R2.SoundEffectType;
import net.minecraft.server.v1_9_R2.Vec3D;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Conversions1_9_R2 {

    @Nullable
    public static org.bukkit.World getWorld(@Nullable IBlockAccess blockAccess) {
        return blockAccess instanceof World ? ((World) blockAccess).getWorld() : null;
    }

    @Nullable
    public static org.bukkit.World getWorld(@Nullable World world) {
        return world == null ? null : world.getWorld();
    }

    public static int getX(@Nullable BlockPosition blockPosition) {
        return blockPosition == null ? 0 : blockPosition.getX();
    }

    public static int getY(@Nullable BlockPosition blockPosition) {
        return blockPosition == null ? 0 : blockPosition.getY();
    }

    public static int getZ(@Nullable BlockPosition blockPosition) {
        return blockPosition == null ? 0 : blockPosition.getZ();
    }

    public static double getX(@Nullable Vec3D vec) {
        return vec == null ? 0 : vec.x;
    }

    public static double getY(@Nullable Vec3D vec) {
        return vec == null ? 0 : vec.y;
    }

    public static double getZ(@Nullable Vec3D vec) {
        return vec == null ? 0 : vec.z;
    }

    @Nullable
    public static Rotation getRotation(@Nullable EnumBlockRotation rotation) {
        if (rotation == null) {
            return null;
        }
        switch (rotation) {
            case NONE:
                return Rotation.NONE;
            case CLOCKWISE_90:
                return Rotation.CLOCKWISE_90;
            case CLOCKWISE_180:
                return Rotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90:
                return Rotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("Unknown rotation: " + rotation);
        }
    }

    @Nullable
    public static BlockFace getBlockFace(@Nullable EnumDirection direction) {
        if (direction == null) {
            return null;
        }
        switch (direction) {
            case NORTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.EAST;
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case UP:
                return BlockFace.UP;
            case DOWN:
                return BlockFace.DOWN;
            default:
                throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }

    @Nullable
    public static RayTraceResult getRayTraceResult(@Nullable MovingObjectPosition rayTraceResult) {
        if (rayTraceResult == null) {
            return null;
        }
        switch (rayTraceResult.type) {
            case ENTITY: {
                Entity hitEntity = rayTraceResult.entity.getBukkitEntity();
                return new RayTraceResult(
                    getVector(rayTraceResult.pos),
                    hitEntity,
                    getBlockFace(rayTraceResult.direction)
                );
            }
            case BLOCK: {
                return new RayTraceResult(
                    getVector(rayTraceResult.pos),
                    getBlockPos(rayTraceResult.a()),
                    getBlockFace(rayTraceResult.direction)
                );
            }
            case MISS:
                return new RayTraceResult(getVector(rayTraceResult.pos));
            default:
                return null;
        }
    }

    @Nullable
    private static BlockPos getBlockPos(@Nullable BlockPosition blockPosition) {
        if (blockPosition == null) {
            return null;
        }
        return new BlockPos(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    @Nullable
    private static Vector getVector(@Nullable Vec3D vec) {
        if (vec == null) {
            return null;
        }
        return new Vector(vec.x, vec.y, vec.z);
    }

    public static SoundEffectType toNmsSoundType(BlockDefinition.SoundType soundType) {
        switch (soundType) {
            case WOOD:
                return SoundEffectType.a;
            case GRAVEL:
                return SoundEffectType.b;
            case GRASS:
                return SoundEffectType.c;
            case STONE:
                return SoundEffectType.d;
            case METAL:
                return SoundEffectType.e;
            case GLASS:
                return SoundEffectType.f;
            case CLOTH:
                return SoundEffectType.g;
            case SAND:
                return SoundEffectType.h;
            case SNOW:
                return SoundEffectType.i;
            case LADDER:
                return SoundEffectType.j;
            case ANVIL:
                return SoundEffectType.k;
            case SLIME:
                return SoundEffectType.l;
            default:
                throw new IllegalArgumentException("Unknown sound type: " + soundType);
        }
    }

    public static Material toNmsMaterial(BlockDefinition.Material material) {
        switch (material) {
            case AIR:
                return Material.AIR;
            case GRASS:
                return Material.GRASS;
            case EARTH:
                return Material.EARTH;
            case WOOD:
                return Material.WOOD;
            case STONE:
                return Material.STONE;
            case ORE:
                return Material.ORE;
            case HEAVY:
                return Material.HEAVY;
            case WATER:
                return Material.WATER;
            case LAVA:
                return Material.LAVA;
            case LEAVES:
                return Material.LEAVES;
            case PLANT:
                return Material.PLANT;
            case REPLACEABLE_PLANT:
                return Material.REPLACEABLE_PLANT;
            case SPONGE:
                return Material.SPONGE;
            case CLOTH:
                return Material.CLOTH;
            case FIRE:
                return Material.FIRE;
            case SAND:
                return Material.SAND;
            case ORIENTABLE:
                return Material.ORIENTABLE;
            case WOOL:
                return Material.WOOL;
            case SHATTERABLE:
                return Material.SHATTERABLE;
            case BUILDABLE_GLASS:
                return Material.BUILDABLE_GLASS;
            case TNT:
                return Material.TNT;
            case CORAL:
                return Material.CORAL;
            case ICE:
                return Material.ICE;
            case SNOW_LAYER:
                return Material.SNOW_LAYER;
            case PACKED_ICE:
                return Material.PACKED_ICE;
            case SNOW_BLOCK:
                return Material.SNOW_BLOCK;
            case CACTUS:
                return Material.CACTUS;
            case CLAY:
                return Material.CLAY;
            case PUMPKIN:
                return Material.PUMPKIN;
            case DRAGON_EGG:
                return Material.DRAGON_EGG;
            case PORTAL:
                return Material.PORTAL;
            case CAKE:
                return Material.CAKE;
            case WEB:
                return Material.WEB;
            case PISTON:
                return Material.PISTON;
            case BANNER:
                return Material.BANNER;
            default:
                throw new IllegalArgumentException("Unknown material: " + material);
        }
    }

    public static MaterialMapColor toNmsMaterialColor(BlockDefinition.MaterialColor materialColor) {
        switch (materialColor) {
            case AIR:
                return MaterialMapColor.b;
            case GRASS:
                return MaterialMapColor.c;
            case SAND:
                return MaterialMapColor.d;
            case CLOTH:
                return MaterialMapColor.e;
            case TNT:
                return MaterialMapColor.f;
            case ICE:
                return MaterialMapColor.g;
            case IRON:
                return MaterialMapColor.h;
            case FOLIAGE:
                return MaterialMapColor.i;
            case WHITE:
                return MaterialMapColor.j;
            case CLAY:
                return MaterialMapColor.k;
            case DIRT:
                return MaterialMapColor.l;
            case STONE:
                return MaterialMapColor.m;
            case WATER:
                return MaterialMapColor.n;
            case WOOD:
                return MaterialMapColor.o;
            case QUARTZ:
                return MaterialMapColor.p;
            case ORANGE:
                return MaterialMapColor.q;
            case MAGENTA:
                return MaterialMapColor.r;
            case LIGHT_BLUE:
                return MaterialMapColor.s;
            case YELLOW:
                return MaterialMapColor.t;
            case LIME:
                return MaterialMapColor.u;
            case PINK:
                return MaterialMapColor.v;
            case GRAY:
                return MaterialMapColor.w;
            case SILVER:
                return MaterialMapColor.x;
            case CYAN:
                return MaterialMapColor.y;
            case PURPLE:
                return MaterialMapColor.z;
            case BLUE:
                return MaterialMapColor.A;
            case BROWN:
                return MaterialMapColor.B;
            case GREEN:
                return MaterialMapColor.C;
            case RED:
                return MaterialMapColor.D;
            case BLACK:
                return MaterialMapColor.E;
            case GOLD:
                return MaterialMapColor.F;
            case DIAMOND:
                return MaterialMapColor.G;
            case LAPIS:
                return MaterialMapColor.H;
            case EMERALD:
                return MaterialMapColor.I;
            case OBSIDIAN:
                return MaterialMapColor.J;
            case NETHERRACK:
                return MaterialMapColor.K;
            default:
                throw new IllegalArgumentException("Unknown material color: " + materialColor);
        }
    }

    public static EnumPistonReaction toNmsPushReaction(BlockDefinition.PushReaction pushReaction) {
        switch (pushReaction) {
            case NORMAL:
                return EnumPistonReaction.NORMAL;
            case DESTROY:
                return EnumPistonReaction.DESTROY;
            case BLOCK:
                return EnumPistonReaction.BLOCK;
            default:
                throw new IllegalArgumentException("Unknown push reaction: " + pushReaction);
        }
    }

    @Nullable
    public static AxisAlignedBB toNmsBoundingBox(@Nullable BoundingBox bbox) {
        if (bbox == null) {
            return null;
        }
        AxisAlignedBB nmsBoundingBox = (AxisAlignedBB) bbox.nmsCache;
        if (nmsBoundingBox == null) {
            bbox.nmsCache =
                nmsBoundingBox = new AxisAlignedBB(
                    bbox.getMinX(),
                    bbox.getMinY(),
                    bbox.getMinZ(),
                    bbox.getMaxX(),
                    bbox.getMaxY(),
                    bbox.getMaxZ()
                );
        }
        return nmsBoundingBox;
    }

    @Nullable
    public static MovingObjectPosition toNmsRayTraceResult(@Nullable RayTraceResult rayTraceResult) {
        if (rayTraceResult == null) {
            return null;
        }
        MovingObjectPosition ret = new MovingObjectPosition(
            MovingObjectPosition.EnumMovingObjectType.MISS,
            new Vec3D(
                rayTraceResult.getHitPositionX(),
                rayTraceResult.getHitPositionY(),
                rayTraceResult.getHitPositionZ()
            ),
            toNmsDirection(rayTraceResult.getHitBlockFace()),
            toNmsBlockPosition(rayTraceResult.getHitBlock())
        );
        if (rayTraceResult.getHitBlock() != null) {
            ret.type = MovingObjectPosition.EnumMovingObjectType.BLOCK;
        } else if (rayTraceResult.getHitEntity() != null) {
            ret.type = MovingObjectPosition.EnumMovingObjectType.ENTITY;
            ret.entity = ((CraftEntity) rayTraceResult.getHitEntity()).getHandle();
        }
        return ret;
    }

    @Nullable
    private static BlockPosition toNmsBlockPosition(@Nullable BlockPos blockPos) {
        if (blockPos == null) {
            return null;
        }
        return new BlockPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private static EnumDirection toNmsDirection(@Nullable BlockFace blockFace) {
        if (blockFace == null) {
            return null;
        }
        switch (blockFace) {
            case NORTH:
                return EnumDirection.NORTH;
            case EAST:
                return EnumDirection.EAST;
            case SOUTH:
                return EnumDirection.SOUTH;
            case WEST:
                return EnumDirection.WEST;
            case UP:
                return EnumDirection.UP;
            case DOWN:
                return EnumDirection.DOWN;
            default:
                throw new IllegalArgumentException("Unsupported direction: " + blockFace);
        }
    }

    @Nullable
    public static EnumItemSlot toNmsEquipmentSlot(@Nullable EquipmentSlot slot) {
        if (slot == null) {
            return null;
        }
        switch (slot) {
            case MAIN_HAND:
                return EnumItemSlot.MAINHAND;
            case OFF_HAND:
                return EnumItemSlot.OFFHAND;
            case FEET:
                return EnumItemSlot.FEET;
            case LEGS:
                return EnumItemSlot.LEGS;
            case CHEST:
                return EnumItemSlot.CHEST;
            case HEAD:
                return EnumItemSlot.HEAD;
            default:
                throw new IllegalArgumentException("Unknown equipment slot: " + slot);
        }
    }

    @Nullable
    public static IBlockData toNmsBlockState(@Nullable BlockState blockState) {
        if (blockState == null) {
            return null;
        }
        return Block.getById(blockState.getId()).fromLegacyData(blockState.getData());
    }
}
