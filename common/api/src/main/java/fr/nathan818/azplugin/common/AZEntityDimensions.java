package fr.nathan818.azplugin.common;

import static fr.nathan818.azplugin.common.AZPlatform.log;
import static java.util.Objects.requireNonNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@UtilityClass
public class AZEntityDimensions {

    private static final String ENTITIES_DIMENSIONS_PATH = "/pactify/client/api/entities_dimensions.json";
    private static final Map<String, Map<String, Entry>> ENTITIES_DIMENSIONS;

    static {
        Map<String, Map<String, Entry>> entitiesDimensions = new LinkedHashMap<>();
        try (
            Reader reader = new InputStreamReader(
                requireNonNull(
                    AZEntityDimensions.class.getResourceAsStream(ENTITIES_DIMENSIONS_PATH),
                    ENTITIES_DIMENSIONS_PATH + " not found"
                ),
                StandardCharsets.UTF_8
            )
        ) {
            Object entitiesObj = AZ.platform().parseJson(reader);
            asMap(entitiesObj).forEach((entityType, variantsObj) -> {
                Map<String, Entry> dimensions = new LinkedHashMap<>();
                asMap(variantsObj).forEach((state, values) -> {
                    Map<String, Object> map = asMap(values);
                    dimensions.put(
                        state,
                        new Entry(
                            asDouble(map.get("bboxWidth")),
                            asDouble(map.get("bboxHeight")),
                            asDouble(map.get("eyeHeight"))
                        )
                    );
                });
                entitiesDimensions.put(entityType, Collections.unmodifiableMap(dimensions));
            });
        } catch (Exception ex) {
            log(Level.WARNING, "Failed to load entities dimensions", ex);
        }
        ENTITIES_DIMENSIONS = Collections.unmodifiableMap(entitiesDimensions);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object values) {
        if (values instanceof Map) {
            return (Map<String, Object>) values;
        }
        return Collections.emptyMap();
    }

    private static double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {}
        }
        return 0.0D;
    }

    @Unmodifiable
    public static @NotNull Map<String, @NotNull Map<@NotNull String, @NotNull Entry>> getAll() {
        return ENTITIES_DIMENSIONS;
    }

    @Unmodifiable
    public static @NotNull Collection<@NotNull String> getEntityTypes() {
        return ENTITIES_DIMENSIONS.keySet();
    }

    @Unmodifiable
    public static @NotNull Collection<@NotNull String> getEntityStates(@Nullable String entityType) {
        return getDimensions(entityType).keySet();
    }

    @Unmodifiable
    public static @NotNull Map<@NotNull String, @NotNull Entry> getDimensions(@Nullable String entityType) {
        return ENTITIES_DIMENSIONS.getOrDefault(entityType, Collections.emptyMap());
    }

    public static @Nullable Entry getDimensions(@Nullable String entityType, @Nullable String state) {
        Map<String, Entry> dimensions = ENTITIES_DIMENSIONS.get(entityType);
        if (dimensions == null) {
            return null;
        }
        Entry entry = dimensions.get(state);
        return entry == null ? dimensions.get("default") : entry;
    }

    public static @Nullable Entry getDefaultDimensions(@Nullable String entityType) {
        return getDimensions(entityType, "default");
    }

    public static @Nullable Entry getSneakDimensions(@Nullable String entityType) {
        return getDimensions(entityType, "sneak");
    }

    public static @Nullable Entry getSleepDimensions(@Nullable String entityType) {
        return getDimensions(entityType, "sleep");
    }

    public static @Nullable Entry getElytraDimensions(@Nullable String entityType) {
        return getDimensions(entityType, "elytra");
    }

    public static @Nullable Entry getBabyDimensions(@Nullable String entityType) {
        return getDimensions(entityType, "baby");
    }

    public static @Nullable Entry getSizeDimensions(@Nullable String entityType, int size) {
        if (size < 0) {
            size = 0;
        } else if (size > 10) {
            size = 10;
        }
        return getDimensions(entityType, "size" + size);
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static final class Entry {

        private final double bboxWidth;
        private final double bboxHeight;
        private final double eyeHeight;
    }
}
