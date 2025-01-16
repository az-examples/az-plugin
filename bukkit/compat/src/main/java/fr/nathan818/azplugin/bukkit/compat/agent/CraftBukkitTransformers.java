package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.addField;
import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.generateMethod;
import static fr.nathan818.azplugin.common.utils.asm.AgentClassWriter.addInfo;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class CraftBukkitTransformers {

    public static void registerGetItemStackHandle(Agent agent, String compatBridgeClass, String craftItemStackClass) {
        agent.addTransformer(compatBridgeClass, (loader, className, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) ->
                new ClassVisitor(api, cv) {
                    @Override
                    public MethodVisitor visitMethod(
                        int access,
                        String name,
                        String descriptor,
                        String signature,
                        String[] exceptions
                    ) {
                        if ("getItemStackHandle".equals(name)) {
                            // public static nms.ItemStack getItemStackHandle(CraftItemStack stack) {
                            //   return stack.handle;
                            // }
                            GeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                            mg.loadArg(0);
                            mg.getField(mg.getArgumentTypes()[0], "handle", mg.getReturnType());
                            mg.returnValue();
                            mg.endMethod();
                            addInfo(cv, className, "Defined getItemStackHandle method");
                            return mg;
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }
            );
            return crw.getBytes();
        });
        agent.addTransformer(craftItemStackClass, (loader, className, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
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
                        if ("handle".equals(name)) {
                            // Make the "handle" field public
                            access = (access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
                            addInfo(cv, className, "Changed handle field access to public");
                        }
                        return super.visitField(access, name, descriptor, signature, value);
                    }
                }
            );
            return crw.getBytes();
        });
    }

    public static void registerCraftField(
        Agent agent,
        String compatBridgeClass,
        String getterName,
        String setterName,
        String holderClass,
        String fieldName
    ) {
        agent.addTransformer(holderClass, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) ->
                new ClassVisitor(api, cv) {
                    @Override
                    public void visitEnd() {
                        // Add a public Object field to the holder class
                        addField(cv, Opcodes.ACC_PUBLIC, fieldName, "Ljava/lang/Object;");
                        addInfo(cv, holderClass, "Added field " + fieldName);
                        super.visitEnd();
                    }
                }
            );
            return crw.getBytes();
        });
        agent.addTransformer(compatBridgeClass, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) ->
                new ClassVisitor(api, cv) {
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
                            GeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                            mg.loadArg(0);
                            mg.checkCast(Type.getObjectType(holderClass));
                            mg.getField(Type.getObjectType(holderClass), fieldName, Type.getType(Object.class));
                            mg.returnValue();
                            mg.endMethod();
                            addInfo(cv, compatBridgeClass, "Defined " + getterName + " method");
                            return mg;
                        }
                        if (setterName.equals(name)) {
                            // public static void setterName(Object holder, Object value) {
                            //   ((HolderClass) holder).fieldName = value;
                            // }
                            GeneratorAdapter mg = generateMethod(cv, access, name, descriptor, signature, exceptions);
                            mg.loadArg(0);
                            mg.loadArg(1);
                            mg.putField(Type.getObjectType(holderClass), fieldName, Type.getType(Object.class));
                            mg.returnValue();
                            mg.endMethod();
                            addInfo(cv, compatBridgeClass, "Defined " + setterName + " method");
                            return mg;
                        }
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }
            );
            return crw.getBytes();
        });
    }
}
