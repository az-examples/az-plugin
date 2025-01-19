package fr.nathan818.azplugin.bukkit.item;

import java.util.Collection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public interface NbtCompoundProxy {
    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    int[] EMPTY_INT_ARRAY = new int[0];

    @UnmodifiableView
    @NotNull
    Collection<@NotNull String> getKeys();

    boolean hasKey(@NotNull String key);

    boolean isEmpty();

    int size();

    void clear();

    boolean remove(@NotNull String key);

    default boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    default boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return getByte(key, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    @Contract("_, !null -> !null")
    default Boolean getBoolean(@NotNull String key, @Nullable Boolean defaultValue) {
        Byte b = getByte(key, defaultValue == null ? null : (byte) (defaultValue ? 1 : 0));
        return b == null ? null : b != 0;
    }

    default void setBoolean(@NotNull String key, boolean value) {
        setByte(key, (byte) (value ? 1 : 0));
    }

    default byte getByte(@NotNull String key) {
        return getByte(key, (byte) 0);
    }

    byte getByte(@NotNull String key, byte defaultValue);

    @Contract("_, !null -> !null")
    Byte getByte(@NotNull String key, @Nullable Byte defaultValue);

    void setByte(@NotNull String key, byte value);

    default short getShort(@NotNull String key) {
        return getShort(key, (short) 0);
    }

    short getShort(@NotNull String key, short defaultValue);

    @Contract("_, !null -> !null")
    Short getShort(@NotNull String key, @Nullable Short defaultValue);

    void setShort(@NotNull String key, short value);

    default int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    int getInt(@NotNull String key, int defaultValue);

    @Contract("_, !null -> !null")
    Integer getInt(@NotNull String key, @Nullable Integer defaultValue);

    void setInt(@NotNull String key, int value);

    default long getLong(@NotNull String key) {
        return getLong(key, 0L);
    }

    long getLong(@NotNull String key, long defaultValue);

    @Contract("_, !null -> !null")
    Long getLong(@NotNull String key, @Nullable Long defaultValue);

    void setLong(@NotNull String key, long value);

    default float getFloat(@NotNull String key) {
        return getFloat(key, 0.0F);
    }

    float getFloat(@NotNull String key, float defaultValue);

    @Contract("_, !null -> !null")
    Float getFloat(@NotNull String key, @Nullable Float defaultValue);

    void setFloat(@NotNull String key, float value);

    default double getDouble(@NotNull String key) {
        return getDouble(key, 0.0D);
    }

    double getDouble(@NotNull String key, double defaultValue);

    @Contract("_, !null -> !null")
    Double getDouble(@NotNull String key, @Nullable Double defaultValue);

    void setDouble(@NotNull String key, double value);

    default @NotNull String getString(@NotNull String key) {
        return getString(key, "");
    }

    @Contract("_, !null -> !null")
    String getString(@NotNull String key, @Nullable String defaultValue);

    void setString(@NotNull String key, @NotNull String value);

    default byte@NotNull[] getByteArray(@NotNull String key) {
        return getByteArray(key, EMPTY_BYTE_ARRAY);
    }

    @Contract("_, !null -> !null")
    byte[] getByteArray(@NotNull String key, byte@Nullable[] defaultValue);

    void setByteArray(@NotNull String key, byte@NotNull[] value);

    default int@NotNull[] getIntArray(@NotNull String key) {
        return getIntArray(key, EMPTY_INT_ARRAY);
    }

    @Contract("_, !null -> !null")
    int[] getIntArray(@NotNull String key, int@Nullable[] defaultValue);

    void setIntArray(@NotNull String key, int@NotNull[] value);

    @NotNull
    default NbtCompoundProxy getCompoundOrCreate(@NotNull String key) {
        NbtCompoundProxy compound = getCompoundOrNull(key);
        if (compound == null) {
            compound = createAndSetCompound(key);
        }
        return compound;
    }

    @Nullable
    NbtCompoundProxy getCompoundOrNull(@NotNull String key);

    @NotNull
    NbtCompoundProxy createAndSetCompound(@NotNull String key);
}
