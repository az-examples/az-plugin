package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.ChatPacketTransformers.registerChatPacketTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerCraftField;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerGetItemStackHandle;
import static fr.nathan818.azplugin.bukkit.compat.agent.EntityScaleTransformers.registerEntityScaleTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.NMSMaterialTransformers.registerNMSMaterialTransformer;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.ARMOR_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.TOOL_MATERIALS;

import fr.nathan818.azplugin.bukkit.compat.material.NMSArmorMaterialDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.NMSToolMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import java.util.Locale;
import org.objectweb.asm.Type;

public class BukkitAgentCompat1_8_R3 {

    public static final String COMPAT_BRIDGE1_8_R3 =
        "fr/nathan818/azplugin/bukkit/compat/v1_8_R3/agent/CompatBridge1_8_R3";

    public static void register(Agent agent) {
        registerGetItemStackHandle(
            agent,
            COMPAT_BRIDGE1_8_R3,
            "org/bukkit/craftbukkit/v1_8_R3/inventory/CraftItemStack"
        );
        registerCraftField(
            agent,
            COMPAT_BRIDGE1_8_R3,
            "getAZEntity",
            "setAZEntity",
            "org/bukkit/craftbukkit/v1_8_R3/entity/CraftEntity",
            "azEntity"
        );
        registerChatPacketTransformer(agent, "net/minecraft/server/v1_8_R3/PacketPlayInChat", 100, 3, 3);
        registerNMSMaterialTransformer(
            agent,
            "net/minecraft/server/v1_8_R3/Item$EnumToolMaterial",
            BukkitAgentCompat1_8_R3::initEnumToolMaterial,
            TOOL_MATERIALS,
            true
        );
        registerNMSMaterialTransformer(
            agent,
            "net/minecraft/server/v1_8_R3/ItemArmor$EnumArmorMaterial",
            BukkitAgentCompat1_8_R3::initEnumArmorMaterial,
            ARMOR_MATERIALS,
            false
        );
        EntityTrackEventTransformers1_8_R3.register(agent);
        registerEntityScaleTransformer(agent, opts -> {
            opts.compatBridgeClass(COMPAT_BRIDGE1_8_R3);
            opts.nmsEntityClass("net/minecraft/server/v1_8_R3/Entity");
            opts.craftEntityClass("org/bukkit/craftbukkit/v1_8_R3/entity/CraftEntity");
        });
    }

    private static AddEnumConstantTransformer.InitializerGenerator initEnumToolMaterial(
        NMSToolMaterialDefinition material
    ) {
        return (mg, type, name, ordinal) -> {
            mg.newInstance(type);
            mg.dup();
            mg.push(name);
            mg.push(ordinal);
            mg.push(material.getHarvestLevel());
            mg.push(material.getDurability());
            mg.push(material.getDigSpeed());
            mg.push(material.getDamages());
            mg.push(material.getEnchantability());
            mg.invokeConstructor(
                type,
                ASMUtil.createConstructor(
                    Type.getType(String.class), // enum name
                    Type.INT_TYPE, // enum ordinal
                    Type.INT_TYPE, // harvestLevel
                    Type.INT_TYPE, // durability
                    Type.FLOAT_TYPE, // digSpeed
                    Type.FLOAT_TYPE, // damages
                    Type.INT_TYPE // enchantability
                )
            );
        };
    }

    private static AddEnumConstantTransformer.InitializerGenerator initEnumArmorMaterial(
        NMSArmorMaterialDefinition material
    ) {
        return (mg, type, name, ordinal) -> {
            mg.newInstance(type);
            mg.dup();
            mg.push(name);
            mg.push(ordinal);
            mg.push(material.getName().toLowerCase(Locale.ROOT));
            mg.push(material.getDurabilityFactor());
            ASMUtil.createArray(mg, material.getModifiers());
            mg.push(material.getEnchantability());
            mg.invokeConstructor(
                type,
                ASMUtil.createConstructor(
                    Type.getType(String.class), // enum name
                    Type.INT_TYPE, // enum ordinal
                    Type.getType(String.class), // name
                    Type.INT_TYPE, // durabilityFactor
                    Type.getType(int[].class), // modifiers
                    Type.INT_TYPE // enchantability
                )
            );
        };
    }
}
