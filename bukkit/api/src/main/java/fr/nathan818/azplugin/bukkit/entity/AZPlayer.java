package fr.nathan818.azplugin.bukkit.entity;

import fr.nathan818.azplugin.bukkit.chat.AZBungeeChatComponent;
import fr.nathan818.azplugin.bukkit.item.AZBukkitItemStack;
import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.appearance.AZCosmeticEquipment;
import fr.nathan818.azplugin.common.network.AZNetworkValue;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AZPlayer extends AZClient, AZEntity {
    Player getBukkitPlayer();

    @Override
    default Entity getBukkitEntity() {
        return getBukkitPlayer();
    }

    @Override
    default boolean isValid() {
        return !isClosed();
    }

    boolean isJoined();

    default boolean sendMessage(@NotNull UUID id, @NotNull BaseComponent[] message) {
        return sendMessage(id, AZBungeeChatComponent.copyOf(message));
    }

    default boolean replaceMessage(@NotNull UUID id, @NotNull BaseComponent[] message) {
        return replaceMessage(id, AZBungeeChatComponent.copyOf(message));
    }

    default boolean appendMessage(@NotNull UUID id, @NotNull BaseComponent[] message) {
        return appendMessage(id, AZBungeeChatComponent.copyOf(message));
    }

    default boolean prependMessage(@NotNull UUID id, @NotNull BaseComponent[] message) {
        return prependMessage(id, AZBungeeChatComponent.copyOf(message));
    }

    default boolean openBook(@NotNull ItemStack book) {
        return openBook(AZBukkitItemStack.copyOf(book));
    }

    InventoryView openMenuInventory(@NotNull Inventory inventory);

    void openMenuInventory(@NotNull InventoryView inventory);

    void closeInventory();

    @Nullable
    AZNetworkValue<AZCosmeticEquipment> getCosmeticEquipment(@NotNull AZCosmeticEquipment.Slot slot);

    default void setCosmeticEquipment(@NotNull AZCosmeticEquipment.Slot slot, @Nullable AZCosmeticEquipment equipment) {
        setCosmeticEquipment(slot, AZNetworkValue.fixed(equipment), true);
    }

    default void setCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @Nullable AZCosmeticEquipment equipment,
        boolean flush
    ) {
        setCosmeticEquipment(slot, AZNetworkValue.fixed(equipment), flush);
    }

    default void setCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @Nullable AZNetworkValue<AZCosmeticEquipment> equipment
    ) {
        setCosmeticEquipment(slot, equipment, true);
    }

    void setCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @Nullable AZNetworkValue<AZCosmeticEquipment> equipment,
        boolean flush
    );

    void flushCosmeticEquipment(
        @NotNull AZCosmeticEquipment.Slot slot,
        @NotNull Iterable<? extends @NotNull Player> recipients
    );
}
