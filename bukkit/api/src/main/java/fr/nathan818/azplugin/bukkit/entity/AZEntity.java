package fr.nathan818.azplugin.bukkit.entity;

import fr.nathan818.azplugin.common.appearance.AZEntityModel;
import fr.nathan818.azplugin.common.appearance.AZEntityScale;
import fr.nathan818.azplugin.common.appearance.AZNameTag;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import java.util.function.Predicate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AZEntity {
    Entity getBukkitEntity();

    default boolean isValid() {
        return getBukkitEntity().isValid();
    }

    Iterable<? extends Player> getViewers(boolean includeSelf);

    Iterable<? extends @NotNull Player> getViewers(boolean includeSelf, @NotNull Predicate<? super Player> filter);

    boolean isViewer(@NotNull Player other);

    @Nullable
    AZNetworkValue<AZEntityScale> getScale();

    default void setScale(@Nullable AZEntityScale scale) {
        setScale(AZNetworkValue.fixed(scale), true);
    }

    default void setScale(@Nullable AZEntityScale scale, boolean flush) {
        setScale(AZNetworkValue.fixed(scale), flush);
    }

    default void setScale(@Nullable AZNetworkValue<AZEntityScale> scale) {
        setScale(scale, true);
    }

    void setScale(@Nullable AZNetworkValue<AZEntityScale> scale, boolean flush);

    void flushScale(@NotNull Iterable<? extends @NotNull Player> recipients);

    @Nullable
    AZNetworkValue<AZEntityModel> getModel();

    default void setModel(@Nullable AZEntityModel model) {
        setModel(AZNetworkValue.fixed(model), true);
    }

    default void setModel(@Nullable AZEntityModel model, boolean flush) {
        setModel(AZNetworkValue.fixed(model), flush);
    }

    default void setModel(@Nullable AZNetworkValue<AZEntityModel> model) {
        setModel(model, true);
    }

    void setModel(@Nullable AZNetworkValue<AZEntityModel> model, boolean flush);

    void flushModel(@NotNull Iterable<? extends @NotNull Player> recipients);

    @Nullable
    AZNetworkValue<AZNameTag> getNameTag(@NotNull AZNameTag.Slot slot);

    default void setNameTag(@NotNull AZNameTag.Slot slot, @Nullable AZNameTag tag) {
        setNameTag(slot, AZNetworkValue.fixed(tag), true);
    }

    default void setNameTag(@NotNull AZNameTag.Slot slot, @Nullable AZNameTag tag, boolean flush) {
        setNameTag(slot, AZNetworkValue.fixed(tag), flush);
    }

    default void setNameTag(@NotNull AZNameTag.Slot slot, @Nullable AZNetworkValue<AZNameTag> tag) {
        setNameTag(slot, tag, true);
    }

    void setNameTag(@NotNull AZNameTag.Slot slot, @Nullable AZNetworkValue<AZNameTag> tag, boolean flush);

    void flushNameTag(@NotNull AZNameTag.Slot slot, @NotNull Iterable<? extends @NotNull Player> recipients);

    @Nullable
    AZNetworkValue<Float> getOpacity();

    default void setOpacity(@Nullable Float opacity) {
        setOpacity(AZNetworkValue.fixed(opacity), true);
    }

    default void setOpacity(@Nullable Float opacity, boolean flush) {
        setOpacity(AZNetworkValue.fixed(opacity), flush);
    }

    default void setOpacity(@Nullable AZNetworkValue<Float> opacity) {
        setOpacity(opacity, true);
    }

    void setOpacity(@Nullable AZNetworkValue<Float> opacity, boolean flush);

    void flushOpacity(@NotNull Iterable<? extends @NotNull Player> recipients);

    void flushAllMetadata(@NotNull Iterable<? extends @NotNull Player> recipients, boolean onTrackBegin);
}
