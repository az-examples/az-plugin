package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.material;

import fr.nathan818.azplugin.bukkit.compat.material.ItemBlockHandler;
import fr.nathan818.azplugin.bukkit.compat.material.ItemDefinition;
import fr.nathan818.azplugin.bukkit.compat.util.HandlerConstants;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.ItemBlock;
import net.minecraft.server.v1_8_R3.ItemStack;

@Getter
public class ItemBlock1_8_R3 extends ItemBlock {

    private final ItemBlockHandler handler;

    public ItemBlock1_8_R3(Block block, ItemDefinition definition, ItemDefinition.ItemBlock type) {
        super(block);
        this.handler = type.getHandler().create(definition);
        if (type.isHasSubtypes()) {
            setMaxDurability(0);
            a(true);
        }
    }

    @Override
    public int filterData(int itemData) {
        int ret = handler.filterData(itemData);
        if (ret != HandlerConstants.DEFAULT_ITEM_DATA) {
            return ret;
        }
        return super.filterData(itemData);
    }

    @Override
    public String e_(ItemStack itemStack) {
        String ret = handler.getTranslationKey(itemStack.getData());
        if (ret != HandlerConstants.DEFAULT_TRANSLATION_KEY) {
            return "tile." + ret;
        }
        return super.e_(itemStack);
    }
}
