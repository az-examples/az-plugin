package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.bukkit.compat.material.BukkitMaterialDefinitions.MATERIALS;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.asMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.createConstructor;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.defineConstantGetter;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.INT_TYPE;

import fr.nathan818.azplugin.bukkit.compat.material.BukkitMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassWriter;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import java.util.stream.Collectors;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Transforms the {@code org.bukkit.Material} enum to add AZ Launcher custom materials.
 */
public class BukkitMaterialTransformers {

    private static final String BUKKIT_MATERIAL = "org/bukkit/Material";
    private static final Method MATERIAL_SUBCLASS_CONSTRUCTOR = createConstructor(
        t(String.class), // enum name
        INT_TYPE, // enum ordinal
        INT_TYPE, // id
        INT_TYPE, // stack
        INT_TYPE // durability
    );

    public static void register(Agent agent) {
        agent.addTransformer(
            BUKKIT_MATERIAL,
            AddEnumConstantTransformer::new,
            MATERIALS.stream()
                .map(material ->
                    new AddEnumConstantTransformer.EnumConstant(material.getName(), (mg, type, name, ordinal) -> {
                        Type subclassType = t(getMaterialSubclass(material));
                        mg.newInstance(subclassType);
                        mg.dup();
                        mg.push(name);
                        mg.push(ordinal);
                        mg.push(material.getId());
                        mg.push(material.getStack());
                        mg.push(material.getDurability());
                        mg.invokeConstructor(subclassType, MATERIAL_SUBCLASS_CONSTRUCTOR);
                    })
                )
                .collect(Collectors.toList())
        );
        for (BukkitMaterialDefinition material : MATERIALS) {
            agent.addTransformer(getMaterialSubclass(material), clazz ->
                clazz.write(cw -> createMaterialSubclass(cw, material))
            );
        }
    }

    private static void createMaterialSubclass(AZClassWriter cw, BukkitMaterialDefinition material) {
        // Define Material subclass
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, getMaterialSubclass(material), null, BUKKIT_MATERIAL, null);

        // Define constructor (calls super)
        AZGeneratorAdapter mg = generateMethod(cw, Opcodes.ACC_PUBLIC, MATERIAL_SUBCLASS_CONSTRUCTOR);
        mg.loadThis();
        mg.loadArgs();
        mg.invokeConstructor(t(BUKKIT_MATERIAL), asMethod(mg));
        mg.returnValue();
        mg.endMethod();

        // Define boolean methods
        defineConstantGetter(cw, "isBlock", material.isBlock());
        defineConstantGetter(cw, "isEdible", material.isEdible());
        defineConstantGetter(cw, "isSolid", material.isSolid());
        defineConstantGetter(cw, "isTransparent", material.isTransparent());
        defineConstantGetter(cw, "isFlammable", material.isFlammable());
        defineConstantGetter(cw, "isBurnable", material.isBurnable());
        defineConstantGetter(cw, "isOccluding", material.isOccluding());
        defineConstantGetter(cw, "hasGravity", material.isHasGravity());

        // Finish
        cw.visitEnd();
    }

    private static String getMaterialSubclass(BukkitMaterialDefinition material) {
        return "fr/nathan818/azplugin/bukkit/compat/agent/RtClass$Material" + material.getId();
    }
}
