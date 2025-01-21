package fr.nathan818.azplugin.common.utils;

import static fr.nathan818.azplugin.common.AZConstants.assertConfFlagExists;
import static fr.nathan818.azplugin.common.AZConstants.assertConfIntExists;
import static fr.nathan818.azplugin.common.AZConstants.getDefaultConfFlag;
import static fr.nathan818.azplugin.common.AZConstants.getDefaultConfInt;
import static fr.nathan818.azplugin.common.AZConstants.isConfFlagSupported;
import static fr.nathan818.azplugin.common.AZConstants.isConfIntSupported;
import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.appearance.AZWorldEnv;
import fr.nathan818.azplugin.common.gui.AZVignette;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;
import pactify.client.api.plsp.PLSPProtocol;
import pactify.client.api.plsp.packet.client.PLSPPacketConf;
import pactify.client.api.plsp.packet.client.PLSPPacketConfFlag;
import pactify.client.api.plsp.packet.client.PLSPPacketConfInt;
import pactify.client.api.plsp.packet.client.PLSPPacketReset;
import pactify.client.api.plsp.packet.client.PLSPPacketVignette;
import pactify.client.api.plsp.packet.client.PLSPPacketWorldEnv;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AZClientAbstract implements AZClient {

    public static final String PLSP_CHANNEL = "PLSP";

    protected final int mcProtocolVersion;
    protected final int azProtocolVersion;
    protected final @Getter AZNetworkContext networkContext = () -> this;

    protected final Object stateLock = new Object();

    @Getter
    protected volatile boolean closed;

    private final ClientMapValues<String, Boolean> confFlags = ClientMapValues.<String, Boolean>builder(this)
        .getter((map, key) ->
            map.getOrDefault(key, getDefaultConfFlag(key, getAZProtocolVersion(), getMCProtocolVersion()))
        )
        .packetFactory(PLSPPacketConfFlag::new)
        .listener(this::onConfFlagChanged)
        .build();
    private final ClientMapValues<String, Integer> confInts = ClientMapValues.<String, Integer>builder(this)
        .getter((map, key) ->
            map.getOrDefault(key, getDefaultConfInt(key, getAZProtocolVersion(), getMCProtocolVersion()))
        )
        .packetFactory(PLSPPacketConfInt::new)
        .listener(this::onConfIntChanged)
        .build();
    private final ClientValue<AZVignette> vignette = ClientValue.<AZVignette>builder(this)
        .packetFactory(v ->
            (v == null)
                ? new PLSPPacketVignette()
                : new PLSPPacketVignette(true, v.getRedFloat(), v.getGreenFloat(), v.getBlueFloat())
        )
        .listener(this::onVignetteChanged)
        .build();
    private final ClientValue<AZWorldEnv> worldEnv = ClientValue.<AZWorldEnv>builder(this)
        .mapper(v -> v == null || v.isEmpty() ? null : v)
        .packetFactory(v ->
            (v == null)
                ? new PLSPPacketWorldEnv("", "default")
                : new PLSPPacketWorldEnv(
                    v.getName() == null ? "" : v.getName(),
                    v.getType() == null ? "default" : v.getType().name()
                )
        )
        .listener(this::onWorldEnvChanged)
        .build();

    @Override
    public int getMCProtocolVersion() {
        return mcProtocolVersion;
    }

    @Override
    public int getAZProtocolVersion() {
        return azProtocolVersion;
    }

    protected abstract boolean isReady();

    protected void sendInitialPackets() {
        assertInNetworkThread();
        if (!isReady()) {
            throw new IllegalStateException("Cannot send initial packets before the client is ready!");
        }
        if (Thread.holdsLock(stateLock)) {
            throw new IllegalStateException("Cannot send initial packets while holding the stateLock!");
        }
        sendPacket(new PLSPPacketReset());
        PLSPPacketConf confPacket = new PLSPPacketConf();
        confPacket.setFlags(confFlags.mapInitial(PLSPPacketConfFlag::new));
        confPacket.setInts(confInts.mapInitial(PLSPPacketConfInt::new));
        if (confPacket.getFlags() != null || confPacket.getInts() != null) {
            sendPacket(confPacket);
        }
        vignette.sendInitial();
        worldEnv.sendInitial();
    }

    protected void sendPacketIfReady(
        Supplier<? extends @Nullable PLSPPacket<PLSPPacketHandler.ClientHandler>> packetFactory
    ) {
        if (isReady()) {
            executeInNetworkThread(() -> {
                PLSPPacket<PLSPPacketHandler.ClientHandler> packet = packetFactory.get();
                if (packet != null) {
                    sendPacket(packet);
                }
            });
        }
    }

    public boolean markClosed() {
        synchronized (stateLock) {
            if (closed) {
                return false;
            }
            closed = true;
            // TODO: timeout all pending queries (from the network thread)
            return true;
        }
    }

    @Override
    public boolean sendPacket(@NotNull PLSPPacket<PLSPPacketHandler.ClientHandler> packet) {
        if (azProtocolVersion < packet.getSinceProtocolVersion()) {
            return false;
        }
        if (isInNetworkThread()) {
            sendPacketNow(packet, null);
        } else {
            executeInNetworkThread(() -> sendPacketNow(packet, null));
        }
        return true;
    }

    @Override
    public <
        Req extends PLSPPacket<PLSPPacketHandler.ClientHandler>, Res extends PLSPPacket<PLSPPacketHandler.ServerHandler>
    > @NotNull CompletableFuture<Res> sendQueryPacket(
        @NotNull Req packet,
        @NotNull Class<Res> responseClass,
        @Nullable BiPredicate<Req, Res> responseMatcher,
        @Nullable Duration timeout
    ) {
        throw new UnsupportedOperationException("Not implemented!"); // TODO
    }

    protected boolean sendPacketNow(
        @NotNull PLSPPacket<PLSPPacketHandler.ClientHandler> packet,
        @Nullable Consumer<? super @Nullable Throwable> callback
    ) {
        try {
            if (isClosed()) {
                return false;
            }

            try (AZPacketBuffer buf = createNetworkPacketBuffer()) {
                PLSPProtocol.writeClientPacketId(buf, packet.getClass());
                packet.write(buf);
                sendPluginMessage(PLSP_CHANNEL, buf, callback);
                return true;
            }
        } catch (Exception ex) {
            log(Level.WARNING, "Failed to send PLSP packet to {0}", getName(), ex);
            return false;
        }
    }

    @NotNull
    protected abstract AZPacketBuffer createNetworkPacketBuffer();

    protected abstract void sendPluginMessage(
        @NotNull String channel,
        @NotNull AZPacketBuffer buf,
        @Nullable Consumer<? super @Nullable Throwable> callback
    ) throws Exception;

    @Override
    public boolean getConfFlag(@NotNull String key) {
        assertConfFlagExists(key);
        return confFlags.get(key);
    }

    @Override
    public int getConfInt(@NotNull String key) {
        assertConfIntExists(key);
        return confInts.get(key);
    }

    @Override
    public boolean setConfFlag(@NotNull String key, boolean value) {
        assertConfFlagExists(key);
        if (!isConfFlagSupported(key, getAZProtocolVersion())) {
            return false;
        }
        confFlags.set(key, value);
        return true;
    }

    @Override
    public boolean setConfInt(@NotNull String key, int value) {
        assertConfIntExists(key);
        if (!isConfIntSupported(key, getAZProtocolVersion())) {
            return false;
        }
        confInts.set(key, value);
        return true;
    }

    protected void onConfFlagChanged(String key, boolean oldValue, boolean newValue) {}

    protected void onConfIntChanged(String key, int oldValue, int newValue) {}

    @Override
    public @Nullable AZVignette getVignette() {
        return vignette.get();
    }

    @Override
    public boolean setVignette(@Nullable AZVignette vignette) {
        if (!hasAZLauncher(PLSPPacketVignette.SINCE_PROTOCOL_VERSION)) {
            return false;
        }
        this.vignette.set(vignette);
        return true;
    }

    protected void onVignetteChanged(@Nullable AZVignette oldValue, @Nullable AZVignette newValue) {}

    @Override
    public @Nullable AZWorldEnv getWorldEnv() {
        return worldEnv.get();
    }

    @Override
    public boolean setWorldEnv(@Nullable AZWorldEnv worldEnv) {
        if (!hasAZLauncher(PLSPPacketWorldEnv.SINCE_PROTOCOL_VERSION)) {
            return false;
        }
        this.worldEnv.set(worldEnv);
        return true;
    }

    protected void onWorldEnvChanged(@Nullable AZWorldEnv oldValue, @Nullable AZWorldEnv newValue) {}
}
