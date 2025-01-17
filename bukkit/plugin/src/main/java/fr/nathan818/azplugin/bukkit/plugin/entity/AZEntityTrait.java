package fr.nathan818.azplugin.bukkit.plugin.entity;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;
import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;
import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.event.AZEntityModelChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZEntityNameTagChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZEntityOpacityChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZEntityScaleChangedEvent;
import fr.nathan818.azplugin.common.appearance.AZEntityModel;
import fr.nathan818.azplugin.common.appearance.AZEntityScale;
import fr.nathan818.azplugin.common.appearance.AZNameTag;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import fr.nathan818.azplugin.common.utils.java.CollectionsUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;
import pactify.client.api.plsp.packet.client.PLSPPacketPlayerMeta;

public abstract class AZEntityTrait implements AZEntity {

    private @Nullable AZEntityScale effectiveScale;
    private @Nullable AZEntityModel effectiveModel;
    private final EntityMetaScale scaleMeta = new EntityMetaScale();
    private final EntityMetaModel modelMeta = new EntityMetaModel();
    private final EntityMetaNameTag nameTagMeta = new EntityMetaNameTag(PLSPPacketAbstractMeta::setTag);
    private final EntityMetaNameTag nameSupTagMeta = new EntityMetaNameTag(PLSPPacketAbstractMeta::setSupTag);
    private final EntityMetaNameTag nameSubTagMeta = new EntityMetaNameTag(PLSPPacketAbstractMeta::setSubTag);
    private final EntityMetaOpacity opacityMeta = new EntityMetaOpacity();

    protected abstract AZEntity self();

    protected abstract @Nullable Player getBukkitPlayer();

    @Override
    public Iterable<? extends Player> getViewers(boolean includeSelf) {
        AZBukkit.platform().assertSync(this, "getViewers");
        Player bukkitPlayer;
        if (includeSelf && (bukkitPlayer = getBukkitPlayer()) != null) {
            return CollectionsUtil.mergeIterables(
                Collections.singleton(bukkitPlayer),
                compat().getViewers(getBukkitEntity())
            );
        } else {
            return compat().getViewers(getBukkitEntity());
        }
    }

    @Override
    public Iterable<? extends @NotNull Player> getViewers(
        boolean includeSelf,
        @NotNull Predicate<? super Player> filter
    ) {
        return CollectionsUtil.filterIterable(getViewers(includeSelf), filter);
    }

    @Override
    public boolean isViewer(@NotNull Player other) {
        AZBukkit.platform().assertSync(this, "isViewer");
        return compat().isViewer(getBukkitEntity(), other);
    }

    private EntityMetaNameTag getNameTagMeta(@NotNull AZNameTag.Slot slot) {
        switch (slot) {
            case MAIN:
                return nameTagMeta;
            case SUP:
                return nameSupTagMeta;
            case SUB:
                return nameSubTagMeta;
            default:
                throw new IllegalArgumentException("Unsupported slot: " + slot);
        }
    }

    @Override
    public @Nullable AZEntityScale getEffectiveScale() {
        return effectiveScale;
    }

    private void setEffectiveScale(@Nullable AZNetworkValue<AZEntityScale> netScale) {
        AZEntityScale scale;
        try {
            scale = (netScale == null) ? null : netScale.get(AZNetworkContext.effective());
        } catch (Exception ex) {
            log(Level.WARNING, "Exception getting effective scale for {0}", self(), ex);
            scale = null;
        }
        effectiveScale = scale;
        if (scale == null) {
            compat().setBboxScale(getBukkitEntity(), 1.0F, 1.0F);
        } else {
            compat().setBboxScale(getBukkitEntity(), scale.getBboxWidth(), scale.getBboxHeight());
        }
    }

    @Override
    public @Nullable AZEntityModel getEffectiveModel() {
        return effectiveModel;
    }

    private void setEffectiveModel(@Nullable AZNetworkValue<AZEntityModel> netModel) {
        AZEntityModel model;
        try {
            model = (netModel == null) ? null : netModel.get(AZNetworkContext.effective());
        } catch (Exception ex) {
            log(Level.WARNING, "Exception getting effective model for {0}", self(), ex);
            model = null;
        }
        this.effectiveModel = model;
    }

    @Override
    public @Nullable AZNetworkValue<AZEntityScale> getScale() {
        return scaleMeta.get();
    }

    @Override
    public void setScale(@Nullable AZNetworkValue<AZEntityScale> scale, boolean flush) {
        AZBukkit.platform().assertSync(this, "setScale");
        AZNetworkValue<AZEntityScale> oldScale = scaleMeta.get();
        if (!scaleMeta.set(scale)) {
            return;
        }
        setEffectiveScale(scale);
        Bukkit.getPluginManager().callEvent(new AZEntityScaleChangedEvent(self(), oldScale, scale));
        if (flush) {
            flushMeta(scaleMeta, getViewers(true), false);
        }
    }

    @Override
    public void flushScale(@NotNull Iterable<? extends @NotNull Player> recipients) {
        AZBukkit.platform().assertSync(this, "flushScale");
        flushMeta(scaleMeta, recipients, true);
    }

    @Override
    public @Nullable AZNetworkValue<AZEntityModel> getModel() {
        return modelMeta.get();
    }

    @Override
    public void setModel(@Nullable AZNetworkValue<AZEntityModel> model, boolean flush) {
        AZBukkit.platform().assertSync(this, "setModel");
        AZNetworkValue<AZEntityModel> oldModel = modelMeta.get();
        if (!modelMeta.set(model)) {
            return;
        }
        setEffectiveModel(model);
        Bukkit.getPluginManager().callEvent(new AZEntityModelChangedEvent(self(), oldModel, model));
        if (flush) {
            flushMeta(modelMeta, getViewers(true), false);
        }
    }

    @Override
    public void flushModel(@NotNull Iterable<? extends @NotNull Player> recipients) {
        AZBukkit.platform().assertSync(this, "flushModel");
        flushMeta(modelMeta, recipients, true);
    }

    @Override
    public @Nullable AZNetworkValue<AZNameTag> getNameTag(@NotNull AZNameTag.Slot slot) {
        return getNameTagMeta(slot).get();
    }

    @Override
    public void setNameTag(@NotNull AZNameTag.Slot slot, @Nullable AZNetworkValue<AZNameTag> tag, boolean flush) {
        AZBukkit.platform().assertSync(this, "setNameTag");
        EntityMetaNameTag nameTagMeta = getNameTagMeta(slot);
        AZNetworkValue<AZNameTag> oldTag = nameTagMeta.get();
        if (!nameTagMeta.set(tag)) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new AZEntityNameTagChangedEvent(self(), slot, oldTag, tag));
        if (flush) {
            flushMeta(nameTagMeta, getViewers(true), false);
        }
    }

    @Override
    public void flushNameTag(@NotNull AZNameTag.Slot slot, @NotNull Iterable<? extends @NotNull Player> recipients) {
        AZBukkit.platform().assertSync(this, "flushNameTag");
        flushMeta(getNameTagMeta(slot), recipients, true);
    }

    @Override
    public @Nullable AZNetworkValue<Float> getOpacity() {
        return opacityMeta.get();
    }

    @Override
    public void setOpacity(@Nullable AZNetworkValue<Float> opacity, boolean flush) {
        AZBukkit.platform().assertSync(this, "setOpacity");
        AZNetworkValue<Float> oldOpacity = opacityMeta.get();
        if (!opacityMeta.set(opacity)) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new AZEntityOpacityChangedEvent(self(), oldOpacity, opacity));
        if (flush) {
            flushMeta(opacityMeta, getViewers(true), false);
        }
    }

    @Override
    public void flushOpacity(@NotNull Iterable<? extends @NotNull Player> recipients) {
        AZBukkit.platform().assertSync(this, "flushOpacity");
        flushMeta(opacityMeta, recipients, true);
    }

    @Override
    public void flushAllMetadata(@NotNull Iterable<? extends @NotNull Player> recipients, boolean onTrackBegin) {
        flushAllMetadataInternal(recipients, onTrackBegin, null);
    }

    protected final void flushAllMetadataInternal(
        @NotNull Iterable<? extends @NotNull Player> recipients,
        boolean onTrackBegin,
        @Nullable SendPacketFunction customSendFunction
    ) {
        AZBukkit.platform().assertSync(this, "flushAllMetadata");
        AZNetworkValue<AZEntityScale> scale = scaleMeta.get();
        AZNetworkValue<AZEntityModel> model = modelMeta.get();
        AZNetworkValue<AZNameTag> nameTag = nameTagMeta.get();
        AZNetworkValue<AZNameTag> nameSupTag = nameSupTagMeta.get();
        AZNetworkValue<AZNameTag> nameSubTag = nameSubTagMeta.get();
        AZNetworkValue<Float> opacity = opacityMeta.get();
        if (
            customSendFunction == null &&
            onTrackBegin &&
            scaleMeta.isDefault(scale) &&
            modelMeta.isDefault(model) &&
            nameTagMeta.isDefault(nameTag) &&
            nameSupTagMeta.isDefault(nameSupTag) &&
            nameSubTagMeta.isDefault(nameSubTag) &&
            opacityMeta.isDefault(opacity)
        ) {
            // Everything is defaulted, no need to send anything
            return;
        }
        sendNetworkValue(recipients, true, PLSPPacketEntityMeta.SINCE_PROTOCOL_VERSION, (recipient, ctx, isSelf) -> {
            PLSPPacketAbstractMeta packet = createMetaPacket(isSelf);
            scaleMeta.apply(self(), packet, scale, ctx, isSelf, onTrackBegin);
            modelMeta.apply(self(), packet, model, ctx, isSelf, onTrackBegin);
            nameTagMeta.apply(self(), packet, nameTag, ctx, isSelf, onTrackBegin);
            nameSupTagMeta.apply(self(), packet, nameSupTag, ctx, isSelf, onTrackBegin);
            nameSubTagMeta.apply(self(), packet, nameSubTag, ctx, isSelf, onTrackBegin);
            opacityMeta.apply(self(), packet, opacity, ctx, isSelf, onTrackBegin);
            if (!isMetaPacketEmpty(packet)) {
                recipient.sendPacket(packet);
            }
            if (customSendFunction != null) {
                customSendFunction.sendPacket(recipient, ctx, isSelf);
            }
        });
    }

    private <A, P> void flushMeta(
        EntityMeta<A, P, ? super PLSPPacketAbstractMeta> meta,
        Iterable<? extends @NotNull Player> recipients,
        boolean filterViewers
    ) {
        AZNetworkValue<A> netValue = meta.get();
        sendNetworkValue(
            recipients,
            filterViewers,
            PLSPPacketEntityMeta.SINCE_PROTOCOL_VERSION,
            (recipient, ctx, isSelf) -> {
                PLSPPacketAbstractMeta packet = createMetaPacket(isSelf);
                meta.apply(self(), packet, netValue, ctx, isSelf, false);
                recipient.sendPacket(packet);
            }
        );
    }

    private PLSPPacketAbstractMeta createMetaPacket(boolean isSelf) {
        if (isSelf) {
            // When sending to self, use UUID bypass the BungeeCord entity ID remapping
            //noinspection DataFlowIssue (bukkitPlayer is non-null when isSelf is true)
            return new PLSPPacketPlayerMeta(getBukkitPlayer().getUniqueId());
        } else {
            return new PLSPPacketEntityMeta(getBukkitEntity().getEntityId());
        }
    }

    private static boolean isMetaPacketEmpty(PLSPPacketAbstractMeta packet) {
        return (
            packet.getScale() == null &&
            packet.getModel() == null &&
            packet.getTag() == null &&
            packet.getSupTag() == null &&
            packet.getSubTag() == null &&
            packet.getOpacity() == null
        );
    }

    protected void sendNetworkValue(
        Iterable<? extends @NotNull Player> recipients,
        boolean filterViewers,
        int minProtocolVersion,
        SendPacketFunction sendFunction
    ) {
        if (recipients instanceof Collection && ((Collection<?>) recipients).isEmpty()) {
            return; // fast-path
        }
        for (Player bukkitRecipient : recipients) {
            boolean isSelf = (bukkitRecipient == getBukkitPlayer());
            if (filterViewers && !isSelf && !isViewer(bukkitRecipient)) {
                continue;
            }
            AZPlayer azRecipient = az(bukkitRecipient);
            if (azRecipient == null || !azRecipient.hasAZLauncher(minProtocolVersion)) {
                continue;
            }
            azRecipient.executeInNetworkThread(() -> {
                if (azRecipient.isClosed()) {
                    return;
                }
                sendFunction.sendPacket(azRecipient, azRecipient.getNetworkContext(), isSelf);
            });
        }
    }

    protected interface SendPacketFunction {
        void sendPacket(AZPlayer recipient, AZNetworkContext ctx, boolean isSelf);
    }
}
