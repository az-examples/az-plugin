package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.addField;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static fr.nathan818.azplugin.common.utils.asm.AZClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZClassVisitor;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MiscTransformers {

    public static void registerGetItemStackHandle(Agent agent, String compatBridgeClass, String craftItemStackClass) {
        registerFieldGetter(agent, compatBridgeClass, "getItemStackHandle", null, craftItemStackClass, "handle");
    }

    public static void registerGetMetaItemUnhandledTags(
        Agent agent,
        String compatBridgeClass,
        String craftMetaItemClass
    ) {
        registerFieldGetter(
            agent,
            compatBridgeClass,
            "getMetaItemUnhandledTags",
            "org/bukkit/inventory/meta/ItemMeta",
            craftMetaItemClass,
            "unhandledTags"
        );
    }

    public static void registerGetNbtCompoundMap(Agent agent, String compatBridgeClass, String nmsNbtCompoundClass) {
        registerFieldGetter(agent, compatBridgeClass, "getNbtCompoundMap", null, nmsNbtCompoundClass, "map");
    }

    private static void registerFieldGetter(
        Agent agent,
        String compatBridgeClass,
        String getterName,
        @Nullable String interfaceClass,
        String holderClass,
        String fieldName
    ) {
        agent.addTransformer(compatBridgeClass, (api, cv) ->
            new AZClassVisitor(api, cv) {
                @Override
                public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
                ) {
                    if (name.equals(getterName)) {
                        // public static nms.ItemStack getItemStackHandle(CraftItemStack stack) {
                        //   return stack.handle;
                        // }
                        AZGeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                        mg.loadArg(0);
                        if (interfaceClass != null) {
                            mg.checkCast(t(interfaceClass));
                        }
                        mg.getField(mg.getArgumentTypes()[0], fieldName, mg.getReturnType());
                        mg.returnValue();
                        mg.endMethod();
                        addInfo(cv, getClassName(), "Defined {0} method", name);
                        return null;
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        );
        agent.addTransformer(holderClass, (api, cv) ->
            new AZClassVisitor(api, cv) {
                @Override
                public FieldVisitor visitField(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    Object value
                ) {
                    if (name.equals(fieldName)) {
                        // Make the "handle" field public
                        access = (access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
                        addInfo(cv, getClassName(), "Changed {0} field access to public", fieldName);
                    }
                    return super.visitField(access, name, descriptor, signature, value);
                }
            }
        );
    }

    public static void registerCraftField(
        Agent agent,
        String compatBridgeClass,
        String getterName,
        String setterName,
        String holderClass,
        String fieldName
    ) {
        agent.addTransformer(holderClass, (api, cv) ->
            new AZClassVisitor(api, cv) {
                @Override
                public void visitEnd() {
                    // Add a public Object field to the holder class
                    addField(cv, Opcodes.ACC_PUBLIC, fieldName, "Ljava/lang/Object;");
                    addInfo(cv, holderClass, "Added field " + fieldName);
                    super.visitEnd();
                }
            }
        );
        agent.addTransformer(compatBridgeClass, (api, cv) ->
            new AZClassVisitor(api, cv) {
                @Override
                public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
                ) {
                    if (getterName.equals(name)) {
                        // public static Object getterName(Object holder) {
                        //   return ((HolderClass) holder).fieldName;
                        // }
                        AZGeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                        mg.loadArg(0);
                        mg.checkCast(t(holderClass));
                        mg.getField(t(holderClass), fieldName, t(Object.class));
                        mg.returnValue();
                        mg.endMethod();
                        addInfo(cv, compatBridgeClass, "Defined " + getterName + " method");
                        return null;
                    }
                    if (setterName.equals(name)) {
                        // public static void setterName(Object holder, Object value) {
                        //   ((HolderClass) holder).fieldName = value;
                        // }
                        AZGeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                        mg.loadArg(0);
                        mg.loadArg(1);
                        mg.putField(t(holderClass), fieldName, t(Object.class));
                        mg.returnValue();
                        mg.endMethod();
                        addInfo(cv, compatBridgeClass, "Defined " + setterName + " method");
                        return null;
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        );
    }
}
