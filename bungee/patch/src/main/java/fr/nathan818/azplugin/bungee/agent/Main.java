package fr.nathan818.azplugin.bungee.agent;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.agent.LoadPluginsHook;
import fr.nathan818.azplugin.common.utils.agent.PluginSupport;
import java.lang.instrument.Instrumentation;

public class Main {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            Agent agent = new Agent();
            LoadPluginsHook.register(agent, n -> n.equals("net/md_5/bungee/api/plugin/PluginManager"));
            PacketTransformers.register(agent);
            inst.addTransformer(agent);
            PluginSupport.markAgentLoaded(Main.class);
        } catch (Throwable ex) {
            throw PluginSupport.handleFatalError(ex);
        }
    }
}
