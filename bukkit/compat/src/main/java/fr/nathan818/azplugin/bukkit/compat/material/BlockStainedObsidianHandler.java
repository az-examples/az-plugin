package fr.nathan818.azplugin.bukkit.compat.material;

import fr.nathan818.azplugin.bukkit.compat.type.DyeColor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BlockStainedObsidianHandler extends BlockHandler {

    public BlockStainedObsidianHandler(@NonNull BlockDefinition definition) {
        super(definition);
    }

    @Override
    public @Nullable BlockDefinition.MaterialColor getMaterialColor(int blockData) {
        return DyeColor.byBlockIndex(blockData).getMaterialColor();
    }

    @Override
    public ItemStack getItemStack(World world, int x, int y, int z, int blockData) {
        return new ItemStack(definition.getId(), 1, (short) DyeColor.byBlockIndex(blockData).getItemIndex());
    }

    @Override
    public int getDroppedItemId(int blockData) {
        return Material.OBSIDIAN.getId();
    }

    @Override
    public int getDroppedItemData(int blockData) {
        return 0;
    }
}
