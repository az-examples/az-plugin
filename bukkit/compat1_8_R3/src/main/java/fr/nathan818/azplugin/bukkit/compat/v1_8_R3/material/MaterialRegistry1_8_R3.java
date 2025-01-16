package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.material;

import static fr.nathan818.azplugin.bukkit.compat.util.ItemUtil.findMaterial;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.ItemHandler;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterBlockResult;
import fr.nathan818.azplugin.bukkit.compat.material.RegisterItemResult;
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

    public RegisterBlockResult registerBlock(@NotNull BlockDefinition definition) {
        Block1_8_R3 block = new Block1_8_R3(definition) {
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
        RegisterItemResult itemResult = null;
        if (itemDefinition != null) {
            BlockDefinitions.assertItemBlock(definition, itemDefinition);
            ItemBlock1_8_R3 item = new ItemBlock1_8_R3(
                block,
                itemDefinition,
                (ItemDefinition.ItemBlock) itemDefinition.getType()
            );
            itemResult = registerItem(itemDefinition, item, item.getHandler());
        }

        return new RegisterBlockResult(block.getHandler(), itemResult == null ? null : itemResult.getHandler());
    }

    public RegisterItemResult registerItem(@NotNull ItemDefinition definition) {
        if (definition.getType() instanceof ItemDefinition.Armor) {
            return registerArmor(definition, (ItemDefinition.Armor) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Sword) {
            return registerSword(definition, (ItemDefinition.Sword) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Spade) {
            return registerSpade(definition, (ItemDefinition.Spade) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Pickaxe) {
            return registerPickaxe(definition, (ItemDefinition.Pickaxe) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Axe) {
            return registerAxe(definition, (ItemDefinition.Axe) definition.getType());
        } else if (definition.getType() instanceof ItemDefinition.Hoe) {
            return registerHoe(definition, (ItemDefinition.Hoe) definition.getType());
        } else {
            throw new IllegalArgumentException("Unsupported item type: " + definition.getType().getClass());
        }
    }

    private RegisterItemResult registerArmor(ItemDefinition definition, ItemDefinition.Armor type) {
        ItemArmor.EnumArmorMaterial material = findMaterial(
            ItemArmor.EnumArmorMaterial.class,
            type.getMaterial(),
            false
        );
        Item item = new ItemArmor(material, 3, Conversions1_8_R3.toNmsArmorIndex(type.getSlot()));
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerSword(ItemDefinition definition, ItemDefinition.Sword type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        Item item = new ItemSword(material);
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerSpade(ItemDefinition definition, ItemDefinition.Spade type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        Item item = new ItemSpade(material);
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerPickaxe(ItemDefinition definition, ItemDefinition.Pickaxe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        Item item = new ItemPickaxe(material) {};
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerAxe(ItemDefinition definition, ItemDefinition.Axe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        // Note: Attack damages and attack speeds are not supported in 1.8
        Item item = new ItemAxe(material) {};
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerHoe(ItemDefinition definition, ItemDefinition.Hoe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), true);
        Item item = new ItemHoe(material);
        return registerItem(definition, item, type.getHandler().create(definition));
    }

    private RegisterItemResult registerItem(ItemDefinition definition, Item item, ItemHandler handler) {
        item.c(definition.getTranslationKey());
        Item.REGISTRY.a(definition.getId(), new MinecraftKey(definition.getMinecraftName()), item);
        return new RegisterItemResult(handler);
    }
}
