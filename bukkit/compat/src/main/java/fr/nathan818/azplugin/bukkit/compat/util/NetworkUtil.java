package fr.nathan818.azplugin.bukkit.compat.util;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import lombok.SneakyThrows;

public class NetworkUtil {

    public static void injectPlayerInHandlers(ChannelPipeline pipeline, Object nmsPlayer) {
        for (Map.Entry<String, ChannelHandler> e : pipeline) {
            String name = e.getKey();
            ChannelHandler handler = e.getValue();
            injectPlayerInHandler(handler, nmsPlayer);
        }
    }

    @SneakyThrows
    private static void injectPlayerInHandler(ChannelHandler handler, Object nmsPlayer) {
        Class<?> handlerClass = handler.getClass();
        do {
            for (Field field : handlerClass.getDeclaredFields()) {
                if (
                    "nmsPlayer".equals(field.getName()) &&
                    Modifier.isPublic(field.getModifiers()) &&
                    field.getType().isAssignableFrom(nmsPlayer.getClass()) &&
                    field.get(handler) == null
                ) {
                    field.set(handler, nmsPlayer);
                }
            }
        } while (
            (handlerClass = handlerClass.getSuperclass()) != null && ChannelHandler.class.isAssignableFrom(handlerClass)
        );
    }
}
