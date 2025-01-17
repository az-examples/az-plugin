package fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.ChatPacketTransformers.registerChatPacketTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerCraftField;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerGetItemStackHandle;
import static fr.nathan818.azplugin.bukkit.compat.agent.EntityScaleTransformers.registerEntityScaleTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.NMSMaterialTransformers.registerNMSMaterialTransformer;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.ARMOR_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.TOOL_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CompatBridge1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CraftEntity1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.CraftItemStack1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.Entity1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EntityPlayer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EnumArmorMaterial1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.EnumToolMaterial1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.ItemStack1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketDataSerializer1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketDecoder1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketEncoder1_8_R3;
import static fr.nathan818.azplugin.bukkit.compat.v1_8_R3.agent.Dictionary1_8_R3.PacketPlayInChat1_8_R3;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.FLOAT_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;

import fr.nathan818.azplugin.bukkit.compat.agent.PacketRewriteTransformers;
import fr.nathan818.azplugin.bukkit.compat.material.NMSArmorMaterialDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.NMSToolMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import java.util.Locale;

public class BukkitAgentCompat1_8_R3 {

    public static void register(Agent agent) {
        registerGetItemStackHandle(agent, CompatBridge1_8_R3, CraftItemStack1_8_R3);
        registerCraftField(agent, CompatBridge1_8_R3, "getAZEntity", "setAZEntity", CraftEntity1_8_R3, "azEntity");
        registerChatPacketTransformer(agent, PacketPlayInChat1_8_R3, 100, 3, 3);
        registerNMSMaterialTransformer(
            agent,
            EnumToolMaterial1_8_R3,
            BukkitAgentCompat1_8_R3::initEnumToolMaterial,
            TOOL_MATERIALS,
            true
        );
        registerNMSMaterialTransformer(
            agent,
            EnumArmorMaterial1_8_R3,
            BukkitAgentCompat1_8_R3::initEnumArmorMaterial,
            ARMOR_MATERIALS,
            false
        );
        EntityTrackEventTransformers1_8_R3.register(agent);
        registerEntityScaleTransformer(agent, opts -> {
            opts.compatBridgeClass(CompatBridge1_8_R3);
            opts.nmsEntityClass(Entity1_8_R3);
            opts.craftEntityClass(CraftEntity1_8_R3);
        });
        PacketRewriteTransformers.register(agent, opts -> {
            opts.compatBridgeClass(CompatBridge1_8_R3);
            opts.nmsPacketDataSerializerClass(PacketDataSerializer1_8_R3);
            opts.writeItemStackMethod("a");
            opts.readItemStackMethod("i");
            opts.nmsPacketEncoderClass(PacketEncoder1_8_R3);
            opts.nmsPacketDecoderClass(PacketDecoder1_8_R3);
            opts.nmsEntityPlayerClass(EntityPlayer1_8_R3);
            opts.nmsItemStackClass(ItemStack1_8_R3);
        });
        ChunkRewriteTransformers1_8_R3.register(agent);
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
                    t(String.class), // enum name
                    INT_TYPE, // enum ordinal
                    INT_TYPE, // harvestLevel
                    INT_TYPE, // durability
                    FLOAT_TYPE, // digSpeed
                    FLOAT_TYPE, // damages
                    INT_TYPE // enchantability
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
                    t(String.class), // enum name
                    INT_TYPE, // enum ordinal
                    t(String.class), // name
                    INT_TYPE, // durabilityFactor
                    t(int[].class), // modifiers
                    INT_TYPE // enchantability
                )
            );
        };
    }
}
