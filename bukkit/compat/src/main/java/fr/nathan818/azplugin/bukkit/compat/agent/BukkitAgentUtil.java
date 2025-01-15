package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.material.EnumDefinition;
import fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AddEnumConstantTransformer;
import fr.nathan818.azplugin.common.utils.asm.ClassRewriter;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

@UtilityClass
public class BukkitAgentUtil {

    private static final int NEW_CHAT_MESSAGE_LIMIT = 16384;

    public static void registerCommon(Agent agent) {
        agent.addClassToPreload("fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge");
        agent.addClassToPreload(
            "fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge$CallEntityTrackBeginEventFunction"
        );
        agent.addClassToPreload("fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge$GetHeadHeightFunction");
        BukkitMaterialTransformers.register(agent);
    }

    public static <T extends EnumDefinition> void registerMaterialEnumTransformer(
        Agent agent,
        String className,
        Function<? super T, ? extends AddEnumConstantTransformer.InitializerGenerator> initializerFn,
        Collection<? extends T> materials,
        boolean fixToolMaterialName1_8
    ) {
        agent.addTransformer(className, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) ->
                new AddEnumConstantTransformer(
                    api,
                    cv,
                    materials
                        .stream()
                        .map(material -> {
                            String name = material.getName();
                            if (fixToolMaterialName1_8) {
                                name = NMSMaterialDefinitions.fixToolMaterialName1_8(name);
                            }
                            return new AddEnumConstantTransformer.EnumConstant(name, initializerFn.apply(material));
                        })
                        .collect(Collectors.toList())
                )
            );
            log(Level.INFO, "Successfully inserted new enum values into {0}", className);
            return crw.getBytes();
        });
    }

    public static void registerChatPacketTransformer(
        Agent agent,
        String className,
        int defaultLimit,
        int minRefCount,
        int maxRefCount
    ) {
        agent.addTransformer(className, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);

            // Verify if the target class matches the expected pattern
            ChatMessageLimitClassTransformer verifier = crw.read(api ->
                new ChatMessageLimitClassTransformer(api, null, defaultLimit, 0)
            );
            if (verifier.getRefCount() < minRefCount || verifier.getRefCount() > maxRefCount) {
                return null;
            }

            // Increase the limit
            crw.rewrite((api, cv) -> new ChatMessageLimitClassTransformer(api, cv, defaultLimit, NEW_CHAT_MESSAGE_LIMIT)
            );
            log(
                Level.INFO,
                "Successfully increased {0} message limit from {1} to {2}",
                className,
                defaultLimit,
                NEW_CHAT_MESSAGE_LIMIT
            );
            return crw.getBytes();
        });
    }

    public static void registerGetItemStackHandle(
        Agent agent,
        String compatBridgeClassName,
        String craftItemStackClassName
    ) {
        agent.addTransformer(compatBridgeClassName, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite(GetItemStackHandleTransformer::new);
            return crw.getBytes();
        });
        agent.addTransformer(craftItemStackClassName, (loader, ignored, bytes) -> {
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
                        access = (access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
                        return super.visitField(access, name, descriptor, signature, value);
                    }
                }
            );
            return crw.getBytes();
        });
    }

    public static void registerCraftField(
        Agent agent,
        String compatBridgeClassName,
        String getterName,
        String setterName,
        String implClass,
        String fieldName
    ) {
        agent.addTransformer(implClass, (loader, ignored, bytes) -> {
            ClassRewriter crw = new ClassRewriter(loader, bytes);
            crw.rewrite((api, cv) ->
                new ClassVisitor(api, cv) {
                    @Override
                    public void visitEnd() {
                        visitField(Opcodes.ACC_PUBLIC, fieldName, "Ljava/lang/Object;", null, null);
                        super.visitEnd();
                    }
                }
            );
            return crw.getBytes();
        });
        agent.addTransformer(compatBridgeClassName, (loader, ignored, bytes) -> {
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
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if (getterName.equals(name)) {
                            return new GeneratorAdapter(api, mv, access, name, descriptor) {
                                @Override
                                public void visitCode() {
                                    loadArg(0);
                                    checkCast(Type.getObjectType(implClass));
                                    getField(Type.getObjectType(implClass), fieldName, Type.getType(Object.class));
                                    returnValue();
                                    endMethod();
                                }
                            };
                        } else if (setterName.equals(name)) {
                            return new GeneratorAdapter(api, mv, access, name, descriptor) {
                                @Override
                                public void visitCode() {
                                    loadArg(0);
                                    loadArg(1);
                                    putField(Type.getObjectType(implClass), fieldName, Type.getType(Object.class));
                                    returnValue();
                                    endMethod();
                                }
                            };
                        }
                        return mv;
                    }
                }
            );
            return crw.getBytes();
        });
    }
}
