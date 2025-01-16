package fr.nathan818.azplugin.bukkit.plugin;

import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;
import static fr.nathan818.azplugin.common.AZPlatform.log;

import com.google.gson.Gson;
import fr.nathan818.azplugin.bukkit.AZBukkit;
import fr.nathan818.azplugin.bukkit.AZBukkitAPI;
import fr.nathan818.azplugin.bukkit.AZBukkitPlatform;
import fr.nathan818.azplugin.bukkit.agent.Main;
import fr.nathan818.azplugin.bukkit.compat.BukkitCompat;
import fr.nathan818.azplugin.bukkit.item.AZBukkitItemStack;
import fr.nathan818.azplugin.bukkit.plugin.entity.ClientManager;
import fr.nathan818.azplugin.bukkit.plugin.entity.EntityManager;
import fr.nathan818.azplugin.bukkit.plugin.material.MaterialManager;
import fr.nathan818.azplugin.common.item.NotchianItemStackLike;
import fr.nathan818.azplugin.common.network.AZPacketBuffer;
import fr.nathan818.azplugin.common.utils.agent.PluginSupport;
import java.io.Reader;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public class AZPlugin extends JavaPlugin implements AZBukkitPlatform {

    private final Gson gson = new Gson();
    private final AZBukkitAPI api = new AZBukkitAPIImpl(this);
    final ClientManager clientManager = new ClientManager(this);
    final MaterialManager materialManager = new MaterialManager(this);
    final ConfManager confManager = new ConfManager(this);
    final EntityManager entityManager = new EntityManager(this);

    @Override
    public void onLoad() {
        try {
            PluginSupport.assertAgentLoaded(Main.class);
            AZBukkit.init(this);
            BukkitCompat compat = compat();
            if (compat == null) {
                String version = Bukkit.getVersion() + " / " + Bukkit.getBukkitVersion();
                throw new IllegalStateException("Unsupported server version: " + version);
            }
            log(Level.INFO, "Using: {0}", compat.getClass().getSimpleName());
            compat.onLoad();
            materialManager.registerMaterials(); // Custom blocks and items need to be registered early
            log(Level.INFO, "Loaded!");
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex, Bukkit::shutdown);
        }
    }

    @Override
    public void onEnable() {
        clientManager.register();
        materialManager.register();
        confManager.register();
        entityManager.register();
        log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        entityManager.unregister();
        confManager.unregister();
        materialManager.unregister();
        clientManager.unregister();
        log(Level.INFO, "Disabled!");
    }

    @Override
    public @NotNull AZBukkitAPI getAPI() {
        return api;
    }

    @Override
    public @Nullable Object parseJson(@NotNull Reader reader) {
        return gson.fromJson(reader, Object.class);
    }

    @Override
    public @Nullable NotchianItemStack readNotchianItemStack(@NotNull AZPacketBuffer buf) {
        return AZBukkitItemStack.mirrorOf(compat().readItemStack(buf));
    }

    @Override
    public void writeNotchianItemStack(@NotNull AZPacketBuffer buf, @Nullable NotchianItemStack itemStack) {
        compat().writeItemStack(buf, AZBukkitItemStack.mirrorBukkitItemStack(NotchianItemStackLike.of(itemStack)));
    }

    @Override
    public @Nullable NotchianNbtTagCompound readNotchianNbtTagCompound(@NotNull AZPacketBuffer buf) {
        return compat().readNotchianNbtTagCompound(buf);
    }

    @Override
    public @NotNull ItemStack asCraftCopy(@NotNull ItemStack item) {
        return compat().asCraftCopy(item);
    }

    @Override
    public @Nullable ItemStack createItemStack(
        int itemId,
        int count,
        int damage,
        @Nullable NotchianNbtTagCompound tag
    ) {
        if (itemId <= 0) {
            return null;
        }
        return compat().createItemStack(itemId, count, damage, tag);
    }

    @Override
    public @Nullable NotchianNbtTagCompound getItemStackTag(@NotNull ItemStack itemStack) {
        return compat().getItemStackTag(itemStack);
    }

    @Override
    public int getActiveContainerWindowId(@NotNull Player bukkitPlayer) {
        return compat().getActiveContainerWindowId(bukkitPlayer);
    }

    @Override
    public void closeInventoryServerSide(@NotNull Player bukkitPlayer) {
        compat().closeInventoryServerSide(bukkitPlayer);
    }
}
