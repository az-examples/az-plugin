package fr.nathan818.azplugin.bukkit.compat.agent;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class BukkitAgentCompat {

    public static final String COMPAT_BRIDGE = "fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge";
    public static final Method CALL_ENTITY_TRACK_BEGIN_EVENT = new Method(
        "callEntityTrackBeginEvent",
        Type.VOID_TYPE,
        new Type[] { Type.getObjectType("org/bukkit/entity/Entity"), Type.getObjectType("org/bukkit/entity/Player") }
    );

    public static void register(Agent agent) {
        agent.addClassToPreload(COMPAT_BRIDGE);
        BukkitMaterialTransformers.register(agent);
    }

    public static void invokeCompatBridge(GeneratorAdapter mg, Method method) {
        mg.invokeStatic(Type.getObjectType(COMPAT_BRIDGE), method);
    }
}
