package fr.nathan818.azplugin.bukkit.compat.util;

import fr.nathan818.azplugin.bukkit.compat.material.BlockDefinition;
import fr.nathan818.azplugin.bukkit.compat.type.BlockState;
import fr.nathan818.azplugin.bukkit.compat.type.BoundingBox;
import fr.nathan818.azplugin.bukkit.compat.type.RayTraceResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@UtilityClass
public class HandlerConstants {

    public static final BlockState DEFAULT_BLOCK_STATE = new BlockState(-1, -1);
    public static final ItemStack DEFAULT_ITEM_STACK = new ItemStack(-1);
    public static final int DEFAULT_ITEM_ID = -1;
    public static final int DEFAULT_ITEM_DATA = -1;
    public static final int DEFAULT_ITEM_AMOUNT = -1;
    public static final List<ItemStack> DEFAULT_DROPS_LIST = Collections.unmodifiableList(new ArrayList<>(0));
    public static final BlockDefinition.MaterialColor DEFAULT_MATERIAL_COLOR = null;
    public static final String DEFAULT_TRANSLATION_KEY = null;
    public static final BoundingBox DEFAULT_BOUNDING_BOX = BoundingBox.of(
        Double.MIN_VALUE,
        Double.MIN_VALUE,
        Double.MIN_VALUE,
        Double.MIN_VALUE,
        Double.MIN_VALUE,
        Double.MIN_VALUE
    );
    public static final RayTraceResult DEFAULT_RAY_TRACE_RESULT = new RayTraceResult(
        new Vector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE)
    );
}
