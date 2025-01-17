package fr.nathan818.azplugin.bukkit.plugin.entity;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.bukkit.compat.network.PlayerConnection;
import fr.nathan818.azplugin.bukkit.entity.AZEntity;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.event.AZPlayerConfFlagChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZPlayerConfIntChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZPlayerCosmeticEquipmentChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZPlayerVignetteChangedEvent;
import fr.nathan818.azplugin.bukkit.event.AZPlayerWorldEnvChangedEvent;
import fr.nathan818.azplugin.bukkit.plugin.AZPlugin;
import fr.nathan818.azplugin.common.AZConstants;
import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment;
import fr.nathan818.azplugin.common.appearance.AZWorldEnv;
import fr.nathan818.azplugin.common.gui.AZVignette;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import fr.nathan818.azplugin.common.utils.AZClientAbstract;
import fr.nathan818.azplugin.common.utils.PendingTask;
import fr.nathan818.azplugin.common.utils.PendingTask.CallablePendingTask;
import fr.nathan818.azplugin.common.utils.PendingTask.RunnablePendingTask;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractCosmeticEquipment;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityCosmeticEquipment;
import pactify.client.api.plsp.packet.client.PLSPPacketPlayerCosmeticEquipment;

public class AZPlayerImpl extends AZClientAbstract implements AZPlayer {

    private final @Getter AZPlugin plugin;
    private final @Getter Player bukkitPlayer;

    private PlayerConnection playerConnection;
    private Queue<PendingTask> pendingTasks = new ArrayDeque<>();
    private final PlayerSyncQueue changesSyncQueue = new PlayerSyncQueue(this);

    @Delegate(types = AZEntity.class)
    private final AZEntityTrait entityTrait;

    private @Getter @Setter int@NonNull[] rewriteBlockOutPalette = new int[0];

    private final Map<AZCosmeticEquipment.Slot, EntityMetaCosmeticEquipment> cosmeticEquipmentsMeta = new EnumMap<>(
        AZCosmeticEquipment.Slot.class
    );

    public AZPlayerImpl(AZPlugin plugin, Player bukkitPlayer, int mcProtocolVersion, int azProtocolVersion) {
        super(mcProtocolVersion, azProtocolVersion);
        this.plugin = plugin;
        this.bukkitPlayer = bukkitPlayer;
        this.entityTrait = createAZEntityTrait();
    }

    void setPlayerConnection(PlayerConnection playerConnection) {
        synchronized (stateLock) {
            if (isClosed()) {
                // Connection is already closed, ignore
                return;
            }

            // Ensure player connection is not already set
            if (this.playerConnection != null) {
                throw new IllegalStateException("Player connection already set");
            }
            this.playerConnection = playerConnection;

            // Execute pending tasks, and remove the queue
            Queue<PendingTask> pendingTasks = this.pendingTasks;
            this.pendingTasks = null;
            playerConnection.executeInNetworkThread(() -> {
                if (hasAZLauncher()) {
                    sendInitialPackets();
                }
                for (PendingTask task : pendingTasks) {
                    task.execute();
                }
            });
        }
    }

    @Override
    protected boolean isReady() {
        if (playerConnection != null) {
            return true;
        }
        synchronized (stateLock) {
            return playerConnection != null;
        }
    }

    @Override
    public boolean markClosed() {
        synchronized (stateLock) {
            if (!super.markClosed()) {
                return false;
            }

            // Cancel pending tasks, and remove the queue
            Queue<PendingTask> pendingTasks = this.pendingTasks;
            this.pendingTasks = null;
            if (pendingTasks != null) {
                for (PendingTask task : pendingTasks) {
                    task.cancel();
                }
            }

            return true;
        }
    }

    @Override
    public String getName() {
        return bukkitPlayer.getName();
    }

    @Override
    public Entity getBukkitEntity() {
        return bukkitPlayer;
    }

    @Override
    public boolean isValid() {
        return !isClosed();
    }

    @Override
    public boolean isJoined() {
        return isReady();
    }

    @Override
    public boolean isInNetworkThread() {
        PlayerConnection playerConnection = this.playerConnection;
        return playerConnection != null && playerConnection.isInNetworkThread();
    }

    @Override
    public void executeInNetworkThread(@NotNull Runnable task) {
        PlayerConnection playerConnection = this.playerConnection;
        if (playerConnection != null) {
            try {
                playerConnection.executeInNetworkThread(() -> {
                    try {
                        task.run();
                    } catch (Exception ex) {
                        catchTaskException(task, ex);
                    }
                });
            } catch (Exception ex) {
                catchTaskException(task, ex);
            }
            return;
        }

        synchronized (stateLock) {
            if (isClosed()) {
                return;
            }
            Queue<PendingTask> pendingTasks = this.pendingTasks;
            if (pendingTasks != null) {
                pendingTasks.add(
                    new RunnablePendingTask(() -> {
                        try {
                            task.run();
                        } catch (Exception ex) {
                            catchTaskException(task, ex);
                        }
                    })
                );
                return;
            }
        }
        executeInNetworkThread(task);
    }

    @Override
    public @NotNull <T> CompletableFuture<T> executeInNetworkThread(@NotNull Callable<? extends T> task) {
        PlayerConnection playerConnection = this.playerConnection;
        if (playerConnection != null) {
            CompletableFuture<T> callback = new CompletableFuture<>();
            try {
                playerConnection.executeInNetworkThread(task, callback);
            } catch (Exception ex) {
                callback.completeExceptionally(ex);
            }
            return callback;
        }

        synchronized (stateLock) {
            if (isClosed()) {
                CompletableFuture<T> callback = new CompletableFuture<>();
                callback.cancel(false);
                return callback;
            }
            Queue<PendingTask> pendingTasks = this.pendingTasks;
            if (pendingTasks != null) {
                CompletableFuture<T> callback = new CompletableFuture<>();
                pendingTasks.add(new CallablePendingTask<>(task, callback));
                return callback;
            }
        }
        return executeInNetworkThread(task);
    }

    @Override
    protected @NotNull AZPacketBuffer createNetworkPacketBuffer() {
        return playerConnection.createNetworkPacketBuffer(this);
    }

    @Override
    protected void sendPluginMessage(
        @NotNull String channel,
        @NotNull AZPacketBuffer buf,
        @Nullable Consumer<? super @Nullable Throwable> callback
    ) throws Exception {
        playerConnection.sendPluginMessage(channel, buf, callback);
    }

    @Override
    protected void onConfFlagChanged(String key, boolean oldValue, boolean newValue) {
        changesSyncQueue.onChange(
            key,
            k -> AZConstants.getDefaultConfFlag(k, getAZProtocolVersion()),
            this::getConfFlag,
            (k, o, n) -> Bukkit.getPluginManager().callEvent(new AZPlayerConfFlagChangedEvent(this, k, n))
        );
    }

    @Override
    protected void onConfIntChanged(String key, int oldValue, int newValue) {
        changesSyncQueue.onChange(
            key,
            k -> AZConstants.getDefaultConfInt(k, getAZProtocolVersion()),
            this::getConfInt,
            (k, o, n) -> Bukkit.getPluginManager().callEvent(new AZPlayerConfIntChangedEvent(this, k, o, n))
        );
    }

    @Override
    protected void onVignetteChanged(@Nullable AZVignette oldValue, @Nullable AZVignette newValue) {
        changesSyncQueue.onChange(
            "vignette",
            k -> null,
            k -> getVignette(),
            (k, o, n) -> Bukkit.getPluginManager().callEvent(new AZPlayerVignetteChangedEvent(this, o, n))
        );
    }

    @Override
    protected void onWorldEnvChanged(@Nullable AZWorldEnv oldValue, @Nullable AZWorldEnv newValue) {
        changesSyncQueue.onChange(
            "worldEnv",
            k -> null,
            k -> getWorldEnv(),
            (k, o, n) -> Bukkit.getPluginManager().callEvent(new AZPlayerWorldEnvChangedEvent(this, o, n))
        );
    }

    @Override
    public @Nullable AZNetworkValue<AZCosmeticEquipment> getCosmeticEquipment(@NotNull AZCosmeticEquipment.Slot slot) {
        EntityMetaCosmeticEquipment cosmeticEquipmentMeta = cosmeticEquipmentsMeta.get(slot);
        return cosmeticEquipmentMeta == null ? null : cosmeticEquipmentMeta.get();
    }

    @Override
    public void setCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @Nullable AZNetworkValue<AZCosmeticEquipment> equipment,
        boolean flush
    ) {
        AZBukkit.platform().assertSync(this, "setCosmeticEquipment");
        EntityMetaCosmeticEquipment cosmeticEquipmentMeta = cosmeticEquipmentsMeta.get(slot);
        if (cosmeticEquipmentMeta == null) {
            if (equipment == null) {
                return;
            }
            cosmeticEquipmentMeta = new EntityMetaCosmeticEquipment(slot);
            cosmeticEquipmentsMeta.put(slot, cosmeticEquipmentMeta);
        }
        AZNetworkValue<AZCosmeticEquipment> oldEquipment = cosmeticEquipmentMeta.get();
        if (!cosmeticEquipmentMeta.set(equipment)) {
            return;
        }
        Bukkit.getPluginManager()
            .callEvent(new AZPlayerCosmeticEquipmentChangedEvent(this, slot, oldEquipment, equipment));
        if (flush) {
            flushCosmeticEquipment(cosmeticEquipmentMeta, getViewers(true), false);
        }
    }

    @Override
    public void flushCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @NotNull Iterable<? extends @NotNull Player> recipients
    ) {
        AZBukkit.platform().assertSync(this, "flushCosmeticEquipment");
        flushCosmeticEquipment(cosmeticEquipmentsMeta.get(slot), recipients, true);
    }

    private <A, P> void flushCosmeticEquipment(
        EntityMeta<A, P, ? super PLSPPacketAbstractCosmeticEquipment> meta,
        Iterable<? extends @NotNull Player> recipients,
        boolean filterViewers
    ) {
        AZNetworkValue<A> netValue = meta.get();
        entityTrait.sendNetworkValue(
            recipients,
            filterViewers,
            PLSPPacketEntityCosmeticEquipment.SINCE_PROTOCOL_VERSION,
            (recipient, ctx, isSelf) -> {
                PLSPPacketAbstractCosmeticEquipment packet = createCosmeticEquipmentPacket(isSelf);
                meta.apply(this, packet, netValue, ctx, isSelf, false);
                recipient.sendPacket(packet);
            }
        );
    }

    private PLSPPacketAbstractCosmeticEquipment createCosmeticEquipmentPacket(boolean isSelf) {
        if (isSelf) {
            // When sending to self, use UUID bypass the BungeeCord entity ID remapping
            return new PLSPPacketPlayerCosmeticEquipment(bukkitPlayer.getUniqueId());
        } else {
            return new PLSPPacketEntityCosmeticEquipment(getBukkitEntity().getEntityId());
        }
    }

    private AZEntityTrait createAZEntityTrait() {
        return new AZEntityTrait() {
            @Override
            protected AZEntity self() {
                return AZPlayerImpl.this;
            }

            @Override
            public Entity getBukkitEntity() {
                return bukkitPlayer;
            }

            @Override
            protected Player getBukkitPlayer() {
                return bukkitPlayer;
            }

            @Override
            public void flushAllMetadata(
                @NotNull Iterable<? extends @NotNull Player> recipients,
                boolean onTrackBegin
            ) {
                // TODO(low): Refactor to add a proper way to register metadata, etc.
                //            I'm not a fan of this flushAllMetadataInternal mechanism.
                Map<AZCosmeticEquipment.Slot, AZNetworkValue<AZCosmeticEquipment>> cosmeticEquipments = new EnumMap<>(
                    AZCosmeticEquipment.Slot.class
                );
                cosmeticEquipmentsMeta.forEach((slot, meta) -> {
                    AZNetworkValue<AZCosmeticEquipment> value = meta.get();
                    if (onTrackBegin && meta.isDefault(value)) {
                        return;
                    }
                    cosmeticEquipments.put(slot, value);
                });
                if (cosmeticEquipments.isEmpty()) {
                    super.flushAllMetadata(recipients, onTrackBegin);
                    return;
                }
                flushAllMetadataInternal(recipients, onTrackBegin, (recipient, ctx, isSelf) -> {
                    cosmeticEquipments.forEach((key, value) -> {
                        EntityMetaCosmeticEquipment meta = cosmeticEquipmentsMeta.get(key);
                        PLSPPacketAbstractCosmeticEquipment packet = createCosmeticEquipmentPacket(isSelf);
                        if (meta.apply(self(), packet, value, ctx, isSelf, onTrackBegin)) {
                            recipient.sendPacket(packet);
                        }
                    });
                });
            }
        };
    }

    @Override
    public String toString() {
        return "AZPlayer[" + bukkitPlayer.getName() + "]";
    }

    private void catchTaskException(@NotNull Runnable task, Exception ex) {
        log(Level.WARNING, "Network-thread task failed for {0}: {1}", getName(), task, ex);
    }
}
