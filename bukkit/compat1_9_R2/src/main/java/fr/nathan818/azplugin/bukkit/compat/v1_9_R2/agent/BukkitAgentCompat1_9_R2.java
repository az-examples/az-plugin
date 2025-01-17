package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.agent.ChatPacketTransformers.registerChatPacketTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerCraftField;
import static fr.nathan818.azplugin.bukkit.compat.agent.CraftBukkitTransformers.registerGetItemStackHandle;
import static fr.nathan818.azplugin.bukkit.compat.agent.EntityScaleTransformers.registerEntityScaleTransformer;
import static fr.nathan818.azplugin.bukkit.compat.agent.NMSMaterialTransformers.registerNMSMaterialTransformer;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.ARMOR_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.TOOL_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CompatBridge1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CraftEntity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.CraftItemStack1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.Entity1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EntityPlayer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EnumArmorMaterial1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.EnumToolMaterial1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.ItemAxe1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.ItemStack1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketDataSerializer1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketDecoder1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketEncoder1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.PacketPlayInChat1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.SoundEffect1_9_R2;
import static fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent.Dictionary1_9_R2.SoundEffects1_9_R2;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.FLOAT_TYPE;
import static org.objectweb.asm.Type.INT_TYPE;

import fr.nathan818.azplugin.bukkit.compat.agent.PacketRewriteTransformers;
import fr.nathan818.azplugin.bukkit.compat.material.NMSArmorMaterialDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.NMSToolMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import java.util.Locale;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BukkitAgentCompat1_9_R2 {

    public static void register(Agent agent) {
        registerGetItemStackHandle(agent, CompatBridge1_9_R2, CraftItemStack1_9_R2);
        registerCraftField(agent, CompatBridge1_9_R2, "getAZEntity", "setAZEntity", CraftEntity1_9_R2, "azEntity");
        registerChatPacketTransformer(agent, PacketPlayInChat1_9_R2, 100, 3, 3);
        registerNMSMaterialTransformer(
            agent,
            EnumToolMaterial1_9_R2,
            BukkitAgentCompat1_9_R2::initEnumToolMaterial,
            TOOL_MATERIALS,
            false
        );
        registerNMSMaterialTransformer(
            agent,
            EnumArmorMaterial1_9_R2,
            BukkitAgentCompat1_9_R2::initEnumArmorMaterial,
            ARMOR_MATERIALS,
            false
        );
        agent.addTransformer(ItemAxe1_9_R2, RemoveFinalFromStaticFieldsTransformer::new);
        EntityTrackEventTransformers1_9_R2.register(agent);
        registerEntityScaleTransformer(agent, opts -> {
            opts.compatBridgeClass(CompatBridge1_9_R2);
            opts.nmsEntityClass(Entity1_9_R2);
            opts.craftEntityClass(CraftEntity1_9_R2);
        });
        PacketRewriteTransformers.register(agent, opts -> {
            opts.compatBridgeClass(CompatBridge1_9_R2);
            opts.nmsPacketDataSerializerClass(PacketDataSerializer1_9_R2);
            opts.writeItemStackMethod("a");
            opts.readItemStackMethod("k");
            opts.nmsPacketEncoderClass(PacketEncoder1_9_R2);
            opts.nmsPacketDecoderClass(PacketDecoder1_9_R2);
            opts.nmsEntityPlayerClass(EntityPlayer1_9_R2);
            opts.nmsItemStackClass(ItemStack1_9_R2);
        });
        ChunkRewriteTransformers1_9_R2.register(agent);
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
            Type soundEffectsType = t(SoundEffects1_9_R2);
            Type soundEffectType = t(SoundEffect1_9_R2);

            mg.newInstance(type);
            mg.dup();
            mg.push(name);
            mg.push(ordinal);
            mg.push(material.getName().toLowerCase(Locale.ROOT));
            mg.push(material.getDurabilityFactor());
            ASMUtil.createArray(mg, material.getModifiers());
            mg.push(material.getEnchantability());
            mg.getStatic(soundEffectsType, "o", soundEffectType);
            mg.push(material.getToughness());
            mg.invokeConstructor(
                type,
                ASMUtil.createConstructor(
                    t(String.class), // enum name
                    INT_TYPE, // enum ordinal
                    t(String.class), // name
                    INT_TYPE, // durabilityFactor
                    t(int[].class), // modifiers
                    INT_TYPE, // enchantability
                    soundEffectType, // equipSound
                    FLOAT_TYPE // toughness
                )
            );
        };
    }

    private static class RemoveFinalFromStaticFieldsTransformer extends AZClassVisitor {

        public RemoveFinalFromStaticFieldsTransformer(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if ((access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) != 0) {
                access &= ~Opcodes.ACC_FINAL;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }
    }
}
