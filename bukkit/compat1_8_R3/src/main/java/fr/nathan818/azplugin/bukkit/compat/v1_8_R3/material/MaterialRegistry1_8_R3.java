package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.material;

import static fr.nathan818.azplugin.bukkit.compat.util.ItemUtil.findMaterial;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.v1_8_R3.Conversions1_8_R3;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemAxe;
import net.minecraft.server.v1_8_R3.ItemHoe;
import net.minecraft.server.v1_8_R3.ItemPickaxe;
import net.minecraft.server.v1_8_R3.ItemSpade;
import net.minecraft.server.v1_8_R3.ItemSword;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialRegistry1_8_R3 {

    public static final MaterialRegistry1_8_R3 INSTANCE = new MaterialRegistry1_8_R3();

    public void registerBlock(@NotNull BlockDefinition definition) {
        Block block = new Block1_8_R3(definition) {
            @Override
            public BlockDefinition getDefinition() {
                return definition;
            }
        };
        Block.REGISTRY.a(definition.getId(), new MinecraftKey(definition.getMinecraftName()), block);
        for (IBlockData blockState : block.P().a()) {
            int blockStateId = BlockDefinitions.computeBlockStateId(definition.getId(), block.toLegacyData(blockState));
            Block.d.a(blockState, blockStateId);
        }

        ItemDefinition itemDefinition = definition.getItem();
        if (itemDefinition != null) {
            BlockDefinitions.assertItemBlock(definition, itemDefinition);
            Item item = new ItemBlock1_8_R3(block, itemDefinition, (ItemDefinition.ItemBlock) itemDefinition.getType());
            registerItem(itemDefinition, item);
        }
    }

    public void registerItem(@NotNull ItemDefinition definition) {
        Item item;
        if (definition.getType() instanceof ItemDefinition.Armor) {
            item = createArmor(definition, (ItemDefinition.Armor) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Sword) {
            item = createSword(definition, (ItemDefinition.Sword) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Spade) {
            item = createSpade(definition, (ItemDefinition.Spade) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Pickaxe) {
            item = createPickaxe(definition, (ItemDefinition.Pickaxe) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Axe) {
            item = createAxe(definition, (ItemDefinition.Axe) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Hoe) {
            item = createHoe(definition, (ItemDefinition.Hoe) definition.getType());
        } else {
            throw new IllegalArgumentException("Unsupported item type: " + definition.getType().getClass());
        }
        registerItem(definition, item);
    }

    private void registerItem(ItemDefinition definition, Item item) {
        item.c(definition.getTranslationKey());
        Item.REGISTRY.a(definition.getId(), new MinecraftKey(definition.getMinecraftName()), item);
    }

    private Item createArmor(ItemDefinition definition, ItemDefinition.Armor type) {
        ItemArmor.EnumArmorMaterial material = findMaterial(
            ItemArmor.EnumArmorMaterial.class,
            type.getMaterial(),
            false
        );
        return new ItemArmor(material, 3, Conversions1_8_R3.toNmsArmorIndex(type.getSlot()));
    }

    private Item createSword(ItemDefinition definition, ItemDefinition.Sword type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        return new ItemSword(material);
    }

    private Item createSpade(ItemDefinition definition, ItemDefinition.Spade type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        return new ItemSpade(material);
    }

    private Item createPickaxe(ItemDefinition definition, ItemDefinition.Pickaxe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        return new ItemPickaxe(material) {};
    }

    private Item createAxe(ItemDefinition definition, ItemDefinition.Axe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        // Note: Attack damages and attack speeds are not supported in 1.8
        return new ItemAxe(material) {};
    }

    private Item createHoe(ItemDefinition definition, ItemDefinition.Hoe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        return new ItemHoe(material);
    }
}
