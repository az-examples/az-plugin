package fr.nathan818.azplugin.bukkit.compat.agent;

import static fr.nathan818.azplugin.common.utils.asm.ASMUtil.t;
import static org.objectweb.asm.Type.VOID_TYPE;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.asm.AZGeneratorAdapter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class BukkitAgentCompat {

    public static final String COMPAT_BRIDGE = "fr/nathan818/azplugin/bukkit/compat/agent/CompatBridge";
    public static final Method CALL_ENTITY_TRACK_BEGIN_EVENT = new Method(
        "callEntityTrackBeginEvent",
        VOID_TYPE,
        new Type[] { t("org/bukkit/entity/Entity"), t("org/bukkit/entity/Player") }
    );

    public static void register(Agent agent) {
        agent.addClassToPreload(COMPAT_BRIDGE);
        BukkitMaterialTransformers.register(agent);
    }

    public static void invokeCompatBridge(AZGeneratorAdapter mg, Method method) {
        mg.invokeStatic(t(COMPAT_BRIDGE), method);
    }
}
