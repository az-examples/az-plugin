package fr.nathan818.azplugin.bukkit.compat.v1_9_R2.agent;

import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.ARMOR_MATERIALS;
import static fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions.TOOL_MATERIALS;
import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.agent.NMSAgentUtil;
import fr.nathan818.azplugin.bukkit.compat.material.NMSArmorMaterialDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.NMSToolMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.util.Locale;
import java.util.logging.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class BukkitAgentCompat1_9_R2 {

    public static void register(Agent agent) {
        String compatBridge = "fr/nathan818/azplugin/bukkit/compat/v1_9_R2/agent/CompatBridge1_9_R2";
        NMSAgentUtil.registerGetItemStackHandle(
            agent,
            compatBridge,
            "org/bukkit/craftbukkit/v1_9_R2/inventory/CraftItemStack"
        );
        NMSAgentUtil.registerCraftField(
            agent,
            compatBridge,
            "getAZEntity",
            "setAZEntity",
            "org/bukkit/craftbukkit/v1_9_R2/entity/CraftEntity",
            "azEntity"
        );
        NMSAgentUtil.registerChatPacketTransformer(agent, "net/minecraft/server/v1_9_R2/PacketPlayInChat", 100, 3, 3);
        NMSAgentUtil.registerMaterialEnumTransformer(
            agent,
            "net/minecraft/server/v1_9_R2/Item$EnumToolMaterial",
            BukkitAgentCompat1_9_R2::initEnumToolMaterial,
            TOOL_MATERIALS,
            false
        );
        NMSAgentUtil.registerMaterialEnumTransformer(
            agent,
            "net/minecraft/server/v1_9_R2/ItemArmor$EnumArmorMaterial",
            BukkitAgentCompat1_9_R2::initEnumArmorMaterial,
            ARMOR_MATERIALS,
            false
        );
        agent.addTransformer(
            "net/minecraft/server/v1_9_R2/ItemAxe",
            BukkitAgentCompat1_9_R2::removeFinalFromStaticFields
        );
        agent.addTransformer(
            "net/minecraft/server/v1_9_R2/EntityTrackerEntry",
            BukkitAgentCompat1_9_R2::insertEntityTrackEventCalls
        );
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
            Type soundEffectsType = Type.getObjectType("net/minecraft/server/v1_9_R2/SoundEffects");
            Type soundEffectType = Type.getObjectType("net/minecraft/server/v1_9_R2/SoundEffect");

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
                    Type.getType(String.class), // enum name
                    Type.INT_TYPE, // enum ordinal
                    Type.getType(String.class), // name
                    Type.INT_TYPE, // durabilityFactor
                    Type.getType(int[].class), // modifiers
                    Type.INT_TYPE, // enchantability
                    soundEffectType, // equipSound
                    Type.FLOAT_TYPE // toughness
                )
            );
        };
    }

    private static byte[] removeFinalFromStaticFields(String className, byte[] bytes) {
        ClassRewriter crw = new ClassRewriter(bytes);
        crw.rewrite((api, cv) ->
            new ClassVisitor(api, cv) {
                @Override
                public FieldVisitor visitField(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    Object value
                ) {
                    if ((access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) != 0) {
                        access &= ~Opcodes.ACC_FINAL;
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }
            }
        );
        return crw.getBytes();
    }

    private static byte[] insertEntityTrackEventCalls(String className, byte[] bytes) {
        ClassRewriter crw = new ClassRewriter(bytes);
        EntityTrackEventClassTransformer1_9_R2 tr = crw.rewrite(
            EntityTrackEventClassTransformer1_9_R2::new,
            ClassRewriter.DEFAULT_PARSING_OPTIONS | ClassReader.EXPAND_FRAMES,
            ClassRewriter.DEFAULT_WRITER_FLAGS
        );
        if (tr.isInserted()) {
            log(Level.INFO, "Successfully inserted EntityTrackBeginEvent calls in {0}", className);
        }
        return crw.getBytes();
    }
}
