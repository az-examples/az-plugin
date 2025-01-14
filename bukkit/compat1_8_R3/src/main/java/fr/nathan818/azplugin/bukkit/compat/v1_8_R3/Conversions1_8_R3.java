package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.type.BlockPos;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.EquipmentSlot;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.IBlockAccess;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.Material;
import net.minecraft.server.v1_8_R3.MaterialMapColor;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Conversions1_8_R3 {

    @Nullable
    public static org.bukkit.World getWorld(@Nullable IBlockAccess blockAccess) {
        return blockAccess instanceof World ? ((World) blockAccess).getWorld() : null;
    }

    @Nullable
    public static org.bukkit.World getWorld(@Nullable World world) {
        return world != null ? world.getWorld() : null;
    }

    public static int getX(@Nullable BlockPosition blockPosition) {
        return blockPosition != null ? blockPosition.getX() : 0;
    }

    public static int getY(@Nullable BlockPosition blockPosition) {
        return blockPosition != null ? blockPosition.getY() : 0;
    }

    public static int getZ(@Nullable BlockPosition blockPosition) {
        return blockPosition != null ? blockPosition.getZ() : 0;
    }

    public static double getX(@Nullable Vec3D vec) {
        return vec != null ? vec.a : 0;
    }

    public static double getY(@Nullable Vec3D vec) {
        return vec != null ? vec.b : 0;
    }

    public static double getZ(@Nullable Vec3D vec) {
        return vec != null ? vec.c : 0;
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
        return new Vector(vec.a, vec.b, vec.c);
    }

    public static Block.StepSound toNmsSoundType(BlockDefinition.SoundType soundType) {
        switch (soundType) {
            case WOOD:
                return Block.f;
            case GRAVEL:
                return Block.g;
            case GRASS:
                return Block.h;
            case STONE:
                return Block.e;
            case METAL:
                return Block.j;
            case GLASS:
                return Block.k;
            case CLOTH:
                return Block.l;
            case SAND:
                return Block.m;
            case SNOW:
                return Block.n;
            case LADDER:
                return Block.o;
            case ANVIL:
                return Block.p;
            case SLIME:
                return Block.q;
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

    public static int toNmsPushReaction(BlockDefinition.PushReaction pushReaction) {
        switch (pushReaction) {
            case NORMAL:
                return 0;
            case DESTROY:
                return 1;
            case BLOCK:
                return 2;
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

    public static int toNmsArmorIndex(@Nullable EquipmentSlot slot) {
        if (slot == null) {
            return -1;
        }
        switch (slot) {
            case FEET:
                return 3;
            case LEGS:
                return 2;
            case CHEST:
                return 1;
            case HEAD:
                return 0;
        }
        throw new IllegalArgumentException("Unsupported equipment slot: " + slot);
    }

    @Nullable
    public static IBlockData toNmsBlockState(@Nullable BlockState blockState) {
        if (blockState == null) {
            return null;
        }
        return Block.getById(blockState.getId()).fromLegacyData(blockState.getData());
    }
}
