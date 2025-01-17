package fr.nathan818.azplugin.bungee.agent;

import fr.nathan818.azplugin.common.utils.agent.Agent;
import fr.nathan818.azplugin.common.utils.agent.AgentSupport;
import fr.nathan818.azplugin.common.utils.agent.LoadPluginsHook;
import java.lang.instrument.Instrumentation;

public class Main {

    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            AgentSupport.markAgentInjected(Main.class);
            Agent agent = new Agent();
            LoadPluginsHook.register(agent, n -> n.equals("net/md_5/bungee/api/plugin/PluginManager"));
            PacketTransformers.register(agent);
            inst.addTransformer(agent);
            AgentSupport.markAgentLoaded(Main.class);
        } catch (Throwable ex) {
            throw AgentSupport.handleFatalError(ex);
        }
    }
}
