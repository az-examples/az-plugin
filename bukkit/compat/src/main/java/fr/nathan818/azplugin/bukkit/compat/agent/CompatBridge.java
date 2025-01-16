package fr.nathan818.azplugin.bukkit.compat.agent;

import fr.nathan818.azplugin.bukkit.compat.event.EntityTrackBeginEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CompatBridge {

    public static GetHeadHeightFunction getHeadHeightFunction = GetHeadHeightFunction.DEFAULT;

    public static void callEntityTrackBeginEvent(@NotNull Entity entity, @NotNull Player viewer) {
        Bukkit.getPluginManager().callEvent(new EntityTrackBeginEvent(entity, viewer));
    }

    public static float getHeadHeight(@NotNull Entity entity, float unscaledHeadHeight) {
        return getHeadHeightFunction.getHeadHeight(entity, unscaledHeadHeight);
    }

    public interface GetHeadHeightFunction {
        GetHeadHeightFunction DEFAULT = (entity, unscaledHeadHeight) -> unscaledHeadHeight;

        float getHeadHeight(@NotNull Entity entity, float unscaledHeadHeight);
    }
}
