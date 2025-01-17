package fr.nathan818.azplugin.bukkit.compat.v1_8_R3;

import fr.nathan818.azplugin.bukkit.compat.proxy.NbtCompoundProxy;
import java.util.Collection;
import java.util.Collections;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTBase.NBTNumber;
import net.minecraft.server.v1_8_R3.NBTTagByteArray;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class NbtCompoundProxy1_8_R3 implements NbtCompoundProxy {

    private final NBTTagCompound handle;
    private final boolean unmodifiable;

    public NbtCompoundProxy1_8_R3(@NonNull NBTTagCompound handle, boolean unmodifiable) {
        this.handle = handle;
        this.unmodifiable = unmodifiable;
    }

    private void tryWrite() {
        if (unmodifiable) {
            throw new UnsupportedOperationException("Unmodifiable NBT compound");
        }
    }

    @Override
    public boolean isEmpty() {
        return handle.isEmpty();
    }

    @Override
    public int size() {
        return handle.c().size();
    }

    @Override
    public void clear() {
        tryWrite();
        handle.c().clear();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull String> getKeys() {
        return Collections.unmodifiableCollection(handle.c());
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        return handle.c().contains(key);
    }

    @Override
    public boolean remove(@NotNull String key) {
        return handle.c().remove(key);
    }

    @Override
    public byte getByte(@NotNull String key, byte defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).f();
        }
        return defaultValue;
    }

    @Override
    public Byte getByte(@NotNull String key, @Nullable Byte defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).f();
        }
        return defaultValue;
    }

    @Override
    public void setByte(@NotNull String key, byte value) {
        tryWrite();
        handle.setByte(key, value);
    }

    @Override
    public short getShort(@NotNull String key, short defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).e();
        }
        return defaultValue;
    }

    @Override
    public Short getShort(@NotNull String key, @Nullable Short defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).e();
        }
        return defaultValue;
    }

    @Override
    public void setShort(@NotNull String key, short value) {
        tryWrite();
        handle.setShort(key, value);
    }

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).d();
        }
        return defaultValue;
    }

    @Override
    public Integer getInt(@NotNull String key, @Nullable Integer defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).d();
        }
        return defaultValue;
    }

    @Override
    public void setInt(@NotNull String key, int value) {
        tryWrite();
        handle.setInt(key, value);
    }

    @Override
    public long getLong(@NotNull String key, long defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).c();
        }
        return defaultValue;
    }

    @Override
    public Long getLong(@NotNull String key, @Nullable Long defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).c();
        }
        return defaultValue;
    }

    @Override
    public void setLong(@NotNull String key, long value) {
        tryWrite();
        handle.setLong(key, value);
    }

    @Override
    public float getFloat(@NotNull String key, float defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).h();
        }
        return defaultValue;
    }

    @Override
    public Float getFloat(@NotNull String key, @Nullable Float defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).h();
        }
        return defaultValue;
    }

    @Override
    public void setFloat(@NotNull String key, float value) {
        tryWrite();
        handle.setFloat(key, value);
    }

    @Override
    public double getDouble(@NotNull String key, double defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).g();
        }
        return defaultValue;
    }

    @Override
    public Double getDouble(@NotNull String key, @Nullable Double defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTNumber) {
            return ((NBTNumber) value).g();
        }
        return defaultValue;
    }

    @Override
    public void setDouble(@NotNull String key, double value) {
        tryWrite();
        handle.setDouble(key, value);
    }

    @Override
    public String getString(@NotNull String key, @Nullable String defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTTagString) {
            return ((NBTTagString) value).a_();
        }
        return defaultValue;
    }

    @Override
    public void setString(@NotNull String key, @NotNull String value) {
        tryWrite();
        handle.setString(key, value);
    }

    @Override
    public byte[] getByteArray(@NotNull String key, byte@Nullable[] defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTTagByteArray) {
            return ((NBTTagByteArray) value).c().clone();
        }
        return defaultValue;
    }

    @Override
    public void setByteArray(@NotNull String key, byte@NotNull[] value) {
        tryWrite();
        handle.setByteArray(key, value.clone());
    }

    @Override
    public int[] getIntArray(@NotNull String key, int@Nullable[] defaultValue) {
        NBTBase value = handle.get(key);
        if (handle.get(key) instanceof NBTTagIntArray) {
            return ((NBTTagIntArray) value).c().clone();
        }
        return defaultValue;
    }

    @Override
    public void setIntArray(@NotNull String key, int@NotNull[] value) {
        tryWrite();
        handle.setIntArray(key, value.clone());
    }

    @Override
    public @Nullable NbtCompoundProxy getCompoundOrNull(@NotNull String key) {
        NBTBase value = handle.get(key);
        if (value instanceof NBTTagCompound) {
            return new NbtCompoundProxy1_8_R3((NBTTagCompound) value, unmodifiable);
        }
        return null;
    }

    @Override
    public @NotNull NbtCompoundProxy createAndSetCompound(@NotNull String key) {
        tryWrite();
        NBTTagCompound compound = new NBTTagCompound();
        handle.set(key, compound);
        return new NbtCompoundProxy1_8_R3(compound, unmodifiable);
    }

    @Override
    public String toString() {
        return handle.toString();
    }
}
