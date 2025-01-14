package fr.nathan818.azplugin.bukkit.agent;

import static fr.nathan818.azplugin.bukkit.compat.material.BukkitMaterialDefinitions.MATERIALS;
import static fr.nathan818.azplugin.common.AZPlatform.log;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.defineConstantGetter;

import fr.nathan818.azplugin.bukkit.compat.material.BukkitMaterialDefinition;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ASMUtil;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Transforms the {@code org.bukkit.Material} enum to add AZ Launcher custom materials.
 */
class BukkitMaterialTransformers {

    private static final Type BUKKIT_MATERIAL_TYPE = Type.getObjectType("org/bukkit/Material");
    private static final Method MATERIAL_SUBCLASS_CONSTRUCTOR = ASMUtil.createConstructor(
        Type.getType(String.class), // enum name
        Type.INT_TYPE, // enum ordinal
        Type.INT_TYPE, // id
        Type.INT_TYPE, // stack
        Type.INT_TYPE // durability
    );

    public static void register(Agent agent) {
        agent.addTransformer(BUKKIT_MATERIAL_TYPE.getInternalName(), BukkitMaterialTransformers::transformMaterialEnum);
        for (BukkitMaterialDefinition material : MATERIALS) {
            agent.addTransformer(getMaterialSubclassType(material).getInternalName(), (ignored1, ignored2) ->
                createMaterialSubclass(material)
            );
        }
    }

    private static byte[] transformMaterialEnum(String className, byte[] bytes) {
        ClassRewriter crw = new ClassRewriter(bytes);
        crw.rewrite((api, cv) ->
            new AddEnumConstantTransformer(
                api,
                cv,
                MATERIALS.stream()
                    .map(material ->
                        new AddEnumConstantTransformer.EnumConstant(material.getName(), (mg, type, name, ordinal) -> {
                            Type subclassType = getMaterialSubclassType(material);
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
            )
        );
        log(Level.INFO, "Successfully inserted new enum values into {0}", className);
        return crw.getBytes();
    }

    private static byte[] createMaterialSubclass(BukkitMaterialDefinition material) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        // Define Material subclass
        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            getMaterialSubclassType(material).getInternalName(),
            null,
            BUKKIT_MATERIAL_TYPE.getInternalName(),
            null
        );

        // Define constructor (calls super)
        GeneratorAdapter mg = ASMUtil.generateMethod(cw, Opcodes.ACC_PUBLIC, MATERIAL_SUBCLASS_CONSTRUCTOR);
        mg.loadThis();
        mg.loadArgs();
        mg.invokeConstructor(BUKKIT_MATERIAL_TYPE, ASMUtil.asMethod(mg));
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
        return cw.toByteArray();
    }

    private static Type getMaterialSubclassType(BukkitMaterialDefinition material) {
        return Type.getObjectType(RtClass.class.getName().replace('.', '/') + "$Material" + material.getId());
    }
}
