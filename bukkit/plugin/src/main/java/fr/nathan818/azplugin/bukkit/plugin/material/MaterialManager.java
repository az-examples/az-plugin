package fr.nathan818.azplugin.bukkit.plugin.material;

import static fr.nathan818.azplugin.bukkit.AZBukkitShortcuts.az;
import static fr.nathan818.azplugin.bukkit.compat.BukkitCompat.compat;

import fr.nathan818.azplugin.bukkit.AZMaterial;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.BlockHandler;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.ItemHandler;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterBlockResult;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterItemResult;
import fr.nathan818.azplugin.bukkit.compat.network.BlockRewriter;
import fr.nathan818.azplugin.bukkit.compat.network.ItemStackRewriter;
import fr.nathan818.azplugin.bukkit.compat.proxy.ItemStackProxy;
import fr.nathan818.azplugin.bukkit.compat.proxy.NbtCompoundProxy;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.ItemData;
import fr.nathan818.azplugin.bukkit.entity.AZPlayer;
import fr.nathan818.azplugin.bukkit.plugin.AZPlugin;
import fr.nathan818.azplugin.bukkit.plugin.entity.AZPlayerImpl;
import fr.nathan818.azplugin.common.AZClient;
import fr.nathan818.azplugin.common.network.AZNetworkContext;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import lombok.NonNull;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.plsp.packet.client.PLSPPacketAdditionalContent;

@RequiredArgsConstructor
public class MaterialManager implements Listener, BlockRewriter, ItemStackRewriter {

    private static final int[] EMPTY_PALETTE = new int[0];

    private final AZPlugin plugin;
    private final StainedObsidianListener stainedObsidianListener = new StainedObsidianListener();

    private final Set<Short> additionalItemsAndBlocks = new LinkedHashSet<>();
    private final Map<Integer, BlockHandler> blockHandlers = new HashMap<>();
    private final Map<Integer, ItemHandler> itemHandlers = new HashMap<>();

    public void registerMaterials() {
        compat().setBlockRewriter(this);
        compat().setItemStackRewriter(this);
        for (BlockDefinition blockDefinition : BlockDefinitions.BLOCKS) {
            registerBlock(blockDefinition);
        }
        for (ItemDefinition itemDefinition : ItemDefinitions.ITEMS) {
            registerItem(itemDefinition);
        }
    }

    private void registerBlock(BlockDefinition blockDefinition) {
        RegisterBlockResult result = compat().registerBlock(blockDefinition);
        if (result != null) {
            additionalItemsAndBlocks.add((short) blockDefinition.getId());
            blockHandlers.put(blockDefinition.getId(), result.getHandler());
            ItemHandler itemHandler = result.getItemHandler();
            if (itemHandler != null) {
                itemHandlers.put(blockDefinition.getId(), itemHandler);
            }
        }
    }

    private void registerItem(ItemDefinition itemDefinition) {
        RegisterItemResult result = compat().registerItem(itemDefinition);
        if (result != null) {
            additionalItemsAndBlocks.add((short) itemDefinition.getId());
            itemHandlers.put(itemDefinition.getId(), result.getHandler());
        }
    }

    public void register() {
        registerCustomRecipes();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(stainedObsidianListener, plugin);
    }

    public void unregister() {
        additionalItemsAndBlocks.clear();
        itemHandlers.clear();
        blockHandlers.clear();
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
        // TODO(low): Add config to enable/disable custom recipes
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
        if (azPlayer instanceof AZPlayerImpl) {
            ((AZPlayerImpl) azPlayer).setRewriteBlockOutPalette(createRewriteBlockOutPalette(azPlayer));
        }
        azPlayer.sendPacket(new PLSPPacketAdditionalContent(toShortArray(additionalItemsAndBlocks)));
    }

    private int@NonNull[] createRewriteBlockOutPalette(@NotNull AZPlayer azPlayer) {
        int[] palette = new int[8192];
        for (int blockStateId = 0; blockStateId < palette.length; ++blockStateId) {
            int blockId = BlockDefinitions.getBlockId(blockStateId);
            int blockData = BlockDefinitions.getBlockData(blockStateId);
            BlockHandler handler = blockHandlers.get(blockId);
            if (handler != null) {
                BlockState fallback = handler.getFallbackState(azPlayer.getNetworkContext(), blockData);
                if (fallback != null) {
                    palette[blockStateId] = BlockDefinitions.computeBlockStateId(fallback.getId(), fallback.getData());
                    continue;
                }
            }
            palette[blockStateId] = blockStateId;
        }
        return palette;
    }

    @Override
    public int@NotNull[] getRewriteBlockOutPalette(@NotNull AZNetworkContext ctx) {
        AZClient azClient = ctx.getViewer();
        if (azClient instanceof AZPlayerImpl) {
            return ((AZPlayerImpl) azClient).getRewriteBlockOutPalette();
        }
        return EMPTY_PALETTE;
    }

    @Override
    public void rewriteItemStackOut(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        int itemId = itemStack.getTypeId();
        ItemHandler handler = itemHandlers.get(itemId);
        if (handler == null) {
            return;
        }

        ItemData fallback = handler.applyFallbackItem(ctx, itemStack);
        if (fallback == null) {
            return;
        }

        NbtCompoundProxy tag = itemStack.getTagForWrite();
        tag.setShort("AZPlugin.ItemId", (short) itemId);
        if (fallback.getId() != itemStack.getTypeId()) {
            tag.setShort("AZPlugin.OrigId", (short) itemStack.getTypeId());
            itemStack.setTypeId(fallback.getId());
        }
        if (fallback.getData() != itemStack.getDurability()) {
            tag.setShort("AZPlugin.OrigData", (short) itemStack.getDurability());
            itemStack.setDurability(fallback.getData());
        }
        // TODO(low): Also rewrite item name
    }

    @Override
    public void rewriteItemStackIn(@NotNull AZNetworkContext ctx, @NotNull ItemStackProxy itemStack) {
        NbtCompoundProxy tag = itemStack.getTagForRead();
        if (tag == null) {
            return;
        }

        int azItemId = tag.getShort("AZPlugin.ItemId", (short) 0);
        if (azItemId == 0) {
            return;
        }

        ItemData orig = new ItemData(
            tag.getInt("AZ.OrigId", itemStack.getTypeId()),
            tag.getInt("AZ.OrigData", itemStack.getDurability())
        );
        ItemHandler handler;
        if ((handler = itemHandlers.get(azItemId)) != null) {
            ItemData reverted = handler.revertFallbackItem(ctx, orig);
            if (reverted != null) {
                itemStack.setTypeId(reverted.getId());
                itemStack.setDurability(reverted.getData());
            }
        }

        tag = itemStack.getTagForWrite();
        tag.remove("AZPlugin.ItemId");
        tag.remove("AZPlugin.OrigId");
        tag.remove("AZPlugin.OrigData");
        if (tag.isEmpty()) {
            itemStack.removeTag();
        }
    }

    private static short[] toShortArray(Set<Short> set) {
        short[] array = new short[set.size()];
        int i = 0;
        for (short value : set) {
            array[i++] = value;
        }
        return array;
    }
}
