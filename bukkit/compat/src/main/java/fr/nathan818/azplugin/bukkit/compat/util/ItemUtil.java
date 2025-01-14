package fr.nathan818.azplugin.bukkit.compat.util;

import static fr.nathan818.azplugin.common.AZPlatform.log;

import fr.nathan818.azplugin.bukkit.compat.material.NMSMaterialDefinitions;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemUtil {

    public static <T extends Enum<T>> T findMaterial(
        Class<T> enumClass,
        String materialName,
        boolean fixToolMaterialName1_8
    ) {
        if (fixToolMaterialName1_8) {
            materialName = NMSMaterialDefinitions.fixToolMaterialName1_8(materialName);
        }
        try {
            return Enum.valueOf(enumClass, materialName);
        } catch (IllegalArgumentException ignored) {}

        T fallback = enumClass.getEnumConstants()[0];
        try {
            fallback = Enum.valueOf(enumClass, fixToolMaterialName1_8 ? "EMERALD" : "DIAMOND");
        } catch (IllegalArgumentException ignored) {}
        log(
            Level.WARNING,
            "Unable to find material {0} in {1}, falling back to {2}",
            materialName,
            enumClass.getSimpleName(),
            fallback.name()
        );
        return fallback;
    }
}
