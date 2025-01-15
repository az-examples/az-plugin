package fr.nathan818.azplugin.bukkit.compat.agent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CompatBridge {

    public static CallEntityTrackBeginEventFunction callEntityTrackBeginEventFunction = (entity, viewer) -> {};
    public static GetHeadHeightFunction getHeadHeightFunction = (entity, unscaledHeadHeight) -> unscaledHeadHeight;

    public static void callEntityTrackBeginEvent(@NotNull Entity entity, @NotNull Player viewer) {
        callEntityTrackBeginEventFunction.callEntityTrackBeginEvent(entity, viewer);
    }

    public static float getHeadHeight(@NotNull Entity entity, float unscaledHeadHeight) {
        return getHeadHeightFunction.getHeadHeight(entity, unscaledHeadHeight);
    }

    public interface CallEntityTrackBeginEventFunction {
        void callEntityTrackBeginEvent(@NotNull Entity entity, @NotNull Player viewer);
    }

    public interface GetHeadHeightFunction {
        float getHeadHeight(@NotNull Entity entity, float unscaledHeadHeight);
    }
}
