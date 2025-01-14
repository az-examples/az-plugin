package fr.nathan818.azplugin.bukkit.compat.type;

public enum OptBoolean {
    TRUE,
    FALSE,
    DEFAULT;

    public static OptBoolean fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean toBoolean() {
        return this == TRUE;
    }
}
