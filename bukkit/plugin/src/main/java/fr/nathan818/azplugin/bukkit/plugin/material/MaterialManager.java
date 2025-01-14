package fr.nathan818.azplugin.bukkit.plugin.material;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;
import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;

import fr.nathan818.azplugin.bukkit.AZMaterial;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinitions;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.plugin.AZPlugin;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.packet.client.PLSPPacketAdditionalContent;

@RequiredArgsConstructor
public class MaterialManager implements Listener {

    private final AZPlugin plugin;
    private final StainedObsidianListener stainedObsidianListener = new StainedObsidianListener();
    private short[] additionalItemsAndBlocks;

    public void registerMaterials() {
        Set<Integer> itemsAndBlocksIdsSet = new LinkedHashSet<>();

        for (BlockDefinition blockDefinition : BlockDefinitions.BLOCKS) {
            compat().registerBlock(blockDefinition);
            itemsAndBlocksIdsSet.add(blockDefinition.getId());
        }

        for (ItemDefinition itemDefinition : ItemDefinitions.ITEMS) {
            compat().registerItem(itemDefinition);
            itemsAndBlocksIdsSet.add(itemDefinition.getId());
        }

        additionalItemsAndBlocks = new short[itemsAndBlocksIdsSet.size()];
        int i = 0;
        for (Integer id : itemsAndBlocksIdsSet) {
            additionalItemsAndBlocks[i++] = id.shortValue();
        }
    }

    public void register() {
        registerCustomRecipes();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(stainedObsidianListener, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(stainedObsidianListener);
        HandlerList.unregisterAll(this);
        // TODO(low): Unregister custom recipes?
    }

    private void registerCustomRecipes() {
        // Emerald armor
        addRecipe(AZMaterial.EMERALD_HELMET, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_HELMET))
                .shape("XXX", "X X")
                .setIngredient('X', Material.EMERALD)
        );
        addRecipe(AZMaterial.EMERALD_CHESTPLATE, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_CHESTPLATE))
                .shape("X X", "XXX", "XXX")
                .setIngredient('X', Material.EMERALD)
        );
        addRecipe(AZMaterial.EMERALD_LEGGINGS, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_LEGGINGS))
                .shape("XXX", "X X", "X X")
                .setIngredient('X', Material.EMERALD)
        );
        addRecipe(AZMaterial.EMERALD_BOOTS, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_BOOTS))
                .shape("X X", "X X")
                .setIngredient('X', Material.EMERALD)
        );

        // Emerald tools
        addRecipe(AZMaterial.EMERALD_SWORD, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_SWORD))
                .shape("X", "X", "#")
                .setIngredient('X', Material.EMERALD)
                .setIngredient('#', Material.STICK)
        );
        addRecipe(AZMaterial.EMERALD_SPADE, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_SPADE))
                .shape("X", "#", "#")
                .setIngredient('X', Material.EMERALD)
                .setIngredient('#', Material.STICK)
        );
        addRecipe(AZMaterial.EMERALD_PICKAXE, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_PICKAXE))
                .shape("XXX", " # ", " # ")
                .setIngredient('X', Material.EMERALD)
                .setIngredient('#', Material.STICK)
        );
        addRecipe(AZMaterial.EMERALD_AXE, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_AXE))
                .shape("XX", "X#", " #")
                .setIngredient('X', Material.EMERALD)
                .setIngredient('#', Material.STICK)
        );
        addRecipe(AZMaterial.EMERALD_HOE, () ->
            new ShapedRecipe(new ItemStack(AZMaterial.EMERALD_HOE))
                .shape("XX", " #", " #")
                .setIngredient('X', Material.EMERALD)
                .setIngredient('#', Material.STICK)
        );

        // Stained obsidian
        for (DyeColor dyeColor : DyeColor.values()) {
            addRecipe(AZMaterial.STAINED_OBSIDIAN, () ->
                new ShapelessRecipe(new ItemStack(AZMaterial.STAINED_OBSIDIAN, 1, dyeColor.getWoolData()))
                    .addIngredient(Material.OBSIDIAN)
                    .addIngredient(Material.INK_SACK, dyeColor.getDyeData())
            );
        }
    }

    private void addRecipe(@Nullable Material material, Supplier<? extends Recipe> recipe) {
        // TODO: Add config to enable/disable custom recipes
        if (material != null) {
            plugin.getServer().addRecipe(recipe.get());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        AZPlayer azPlayer = az(event.getPlayer());
        if (azPlayer == null) {
            return;
        }
        azPlayer.sendPacket(new PLSPPacketAdditionalContent(additionalItemsAndBlocks));
    }
}
