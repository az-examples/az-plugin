package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.itemstack;

import fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.CompatBridge1_9_R2;
import fr.nathan818.azplugin.bukkit.item.NbtCompoundProxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.NonNull;
import net.minecraft.server.v1_9_R2.NBTBase;
import net.minecraft.server.v1_9_R2.NBTBase.NBTNumber;
import net.minecraft.server.v1_9_R2.NBTTagByte;
import net.minecraft.server.v1_9_R2.NBTTagByteArray;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagDouble;
import net.minecraft.server.v1_9_R2.NBTTagFloat;
import net.minecraft.server.v1_9_R2.NBTTagInt;
import net.minecraft.server.v1_9_R2.NBTTagIntArray;
import net.minecraft.server.v1_9_R2.NBTTagLong;
import net.minecraft.server.v1_9_R2.NBTTagShort;
import net.minecraft.server.v1_9_R2.NBTTagString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class NbtCompoundProxy1_9_R2 implements NbtCompoundProxy {

    private final @NotNull Map<String, NBTBase> map;
    private final boolean unmodifiable;

    public NbtCompoundProxy1_9_R2(@NonNull Map<String, NBTBase> map, boolean unmodifiable) {
        this.map = map;
        this.unmodifiable = unmodifiable;
    }

    public NbtCompoundProxy1_9_R2(@NonNull NBTTagCompound compound, boolean unmodifiable) {
        this(CompatBridge1_9_R2.getNbtCompoundMap(compound), unmodifiable);
    }

    private void tryWrite() {
        if (unmodifiable) {
            throw new UnsupportedOperationException("Unmodifiable NBT compound");
        }
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        tryWrite();
        map.clear();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull String> getKeys() {
        return Collections.unmodifiableCollection(map.keySet());
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        return map.containsKey(key);
    }

    @Override
    public boolean remove(@NotNull String key) {
        return map.remove(key) != null;
    }

    @Override
    public byte getByte(@NotNull String key, byte defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).f();
        }
        return defaultValue;
    }

    @Override
    public Byte getByte(@NotNull String key, @Nullable Byte defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).f();
        }
        return defaultValue;
    }

    @Override
    public void setByte(@NotNull String key, byte value) {
        tryWrite();
        map.put(key, new NBTTagByte(value));
    }

    @Override
    public short getShort(@NotNull String key, short defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).e();
        }
        return defaultValue;
    }

    @Override
    public Short getShort(@NotNull String key, @Nullable Short defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).e();
        }
        return defaultValue;
    }

    @Override
    public void setShort(@NotNull String key, short value) {
        tryWrite();
        map.put(key, new NBTTagShort(value));
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).d();
        }
        return defaultValue;
    }

    @Override
    public Integer getInt(@NotNull String key, @Nullable Integer defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).d();
        }
        return defaultValue;
    }

    @Override
    public void setInt(@NotNull String key, int value) {
        tryWrite();
        map.put(key, new NBTTagInt(value));
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).c();
        }
        return defaultValue;
    }

    @Override
    public Long getLong(@NotNull String key, @Nullable Long defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).c();
        }
        return defaultValue;
    }

    @Override
    public void setLong(@NotNull String key, long value) {
        tryWrite();
        map.put(key, new NBTTagLong(value));
    }

    @Override
    public float getFloat(@NotNull String key, float defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).h();
        }
        return defaultValue;
    }

    @Override
    public Float getFloat(@NotNull String key, @Nullable Float defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).h();
        }
        return defaultValue;
    }

    @Override
    public void setFloat(@NotNull String key, float value) {
        tryWrite();
        map.put(key, new NBTTagFloat(value));
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).g();
        }
        return defaultValue;
    }

    @Override
    public Double getDouble(@NotNull String key, @Nullable Double defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTNumber) {
            return ((NBTNumber) value).g();
        }
        return defaultValue;
    }

    @Override
    public void setDouble(@NotNull String key, double value) {
        tryWrite();
        map.put(key, new NBTTagDouble(value));
    }

    @Override
    public String getString(@NotNull String key, @Nullable String defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTTagString) {
            return ((NBTTagString) value).a_();
        }
        return defaultValue;
    }

    @Override
    public void setString(@NotNull String key, @NotNull String value) {
        tryWrite();
        map.put(key, new NBTTagString(value));
    }

    @Override
    public byte[] getByteArray(@NotNull String key, byte@Nullable[] defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTTagByteArray) {
            return ((NBTTagByteArray) value).c().clone();
        }
        return defaultValue;
    }

    @Override
    public void setByteArray(@NotNull String key, byte@NotNull[] value) {
        tryWrite();
        map.put(key, new NBTTagByteArray(value.clone()));
    }

    @Override
    public int[] getIntArray(@NotNull String key, int@Nullable[] defaultValue) {
        NBTBase value = map.get(key);
        if (value instanceof NBTTagIntArray) {
            return ((NBTTagIntArray) value).c().clone();
        }
        return defaultValue;
    }

    @Override
    public void setIntArray(@NotNull String key, int@NotNull[] value) {
        tryWrite();
        map.put(key, new NBTTagIntArray(value.clone()));
    }

    @Override
    public @Nullable NbtCompoundProxy getCompoundOrNull(@NotNull String key) {
        NBTBase value = map.get(key);
        if (value instanceof NBTTagCompound) {
            return new NbtCompoundProxy1_9_R2((NBTTagCompound) value, unmodifiable);
        }
        return null;
    }

    @Override
    public @NotNull NbtCompoundProxy createAndSetCompound(@NotNull String key) {
        tryWrite();
        NBTTagCompound compound = new NBTTagCompound();
        map.put(key, compound);
        return new NbtCompoundProxy1_9_R2(compound, unmodifiable);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
