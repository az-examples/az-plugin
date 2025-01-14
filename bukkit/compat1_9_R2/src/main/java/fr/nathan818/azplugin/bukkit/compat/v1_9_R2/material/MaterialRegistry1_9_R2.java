package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.material;

import static fr.nathan818.azplugin.bukkit.compat.util.ItemUtil.findMaterial;
import static fr.nathan818.azplugin.bukkit.compat.util.ReflectionUtil.setArrayConstant;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinitions;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.util.ReflectionUtil.Cancellable;
import fr.nathan818.azplugin.bukkit.compat.v1_9_R2.Conversions1_9_R2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.IBlockData;
import net.minecraft.server.v1_9_R2.Item;
import net.minecraft.server.v1_9_R2.ItemArmor;
import net.minecraft.server.v1_9_R2.ItemAxe;
import net.minecraft.server.v1_9_R2.ItemHoe;
import net.minecraft.server.v1_9_R2.ItemPickaxe;
import net.minecraft.server.v1_9_R2.ItemSpade;
import net.minecraft.server.v1_9_R2.ItemSword;
import net.minecraft.server.v1_9_R2.MinecraftKey;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialRegistry1_9_R2 {

    public static final MaterialRegistry1_9_R2 INSTANCE = new MaterialRegistry1_9_R2();

    public void registerBlock(@NotNull BlockDefinition definition) {
        Block block = new Block1_9_R2(definition) {
            @Override
            public BlockDefinition getDefinition() {
                return definition;
            }
        };
        Block.REGISTRY.a(definition.getId(), new MinecraftKey(definition.getMinecraftName()), block);
        for (IBlockData blockState : block.t().a()) {
            int blockStateId = BlockDefinitions.computeBlockStateId(definition.getId(), block.toLegacyData(blockState));
            Block.REGISTRY_ID.a(blockState, blockStateId);
        }

        ItemDefinition itemDefinition = definition.getItem();
        if (itemDefinition != null) {
            BlockDefinitions.assertItemBlock(definition, itemDefinition);
            Item item = new ItemBlock1_9_R2(block, itemDefinition, (ItemDefinition.ItemBlock) itemDefinition.getType());
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
        return new ItemArmor(material, 3, Conversions1_9_R2.toNmsEquipmentSlot(type.getSlot()));
    }

    private Item createSword(ItemDefinition definition, ItemDefinition.Sword type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), false);
        return new ItemSword(material);
    }

    private Item createSpade(ItemDefinition definition, ItemDefinition.Spade type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), false);
        return new ItemSpade(material);
    }

    private Item createPickaxe(ItemDefinition definition, ItemDefinition.Pickaxe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), false);
        return new ItemPickaxe(material) {};
    }

    private Item createAxe(ItemDefinition definition, ItemDefinition.Axe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), false);
        try (
            Cancellable f = setArrayConstant(ItemAxe.class, "f", material.ordinal(), type.getAttackDamage());
            Cancellable n = setArrayConstant(ItemAxe.class, "n", material.ordinal(), type.getAttackSpeed())
        ) {
            return new ItemAxe(material) {};
        }
    }

    private Item createHoe(ItemDefinition definition, ItemDefinition.Hoe type) {
        Item.EnumToolMaterial material = findMaterial(Item.EnumToolMaterial.class, type.getMaterial(), false);
        return new ItemHoe(material);
    }
}
