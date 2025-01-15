package fr.nathan818.azplugin.bukkit.agent;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.CompatRegistry;
import fr.nathan818.azplugin.bukkit.compat.agent.BukkitAgentUtil;
import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.agent.LoadPluginsHook;
import fr.nathan818.azplugin.common.utils.agent.PluginSupport;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Main2 {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            Agent agent = new Agent();
            LoadPluginsHook.register(agent, n -> n.startsWith("org/bukkit/craftbukkit/") && n.endsWith("/CraftServer"));
            registerAgentCompats(agent);
            inst.addTransformer(agent);
            PluginSupport.markAgentLoaded(Main.class);
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex);
        }
    }

    private static void registerAgentCompats(Agent agent) throws ReflectiveOperationException {
        BukkitAgentUtil.registerCommon(agent);

        List<String> registeredAgentCompats = new ArrayList<>();
        for (String agentCompatClassName : CompatRegistry.getAgentCompatClasses()) {
            Class<?> agentCompatClass;
            try {
                agentCompatClass = Class.forName(agentCompatClassName);
            } catch (ClassNotFoundException ignored) {
                continue;
            }
            agentCompatClass.getMethod("register", Agent.class).invoke(null, agent);
            registeredAgentCompats.add(agentCompatClass.getSimpleName());
        }
        log(Level.INFO, "Registered agent compats: {0}", registeredAgentCompats);
    }
}
