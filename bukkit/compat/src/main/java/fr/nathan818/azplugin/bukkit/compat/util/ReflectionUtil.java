package fr.nathan818.azplugin.bukkit.compat.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {

    public static Field findFieldOfType(Class<?> clazz, String typeSimpleName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getSimpleName().equals(typeSimpleName)) {
                return field;
            }
        }
        if (clazz.getSuperclass() != Object.class && clazz.getSuperclass() != null) {
            return findFieldOfType(clazz.getSuperclass(), typeSimpleName);
        }
        throw new NoSuchFieldError("No field of type " + typeSimpleName + " in " + clazz);
    }

    @SneakyThrows(ReflectiveOperationException.class)
    public static Cancellable setArrayConstant(Class<?> clazz, String fieldName, int index, float value) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        float[] array = (float[]) field.get(null);
        if (index < array.length) {
            float oldValue = array[index];
            array[index] = value;
            return () -> array[index] = oldValue;
        } else {
            float[] newArray = Arrays.copyOf(array, index + 1);
            newArray[index] = value;
            field.set(null, newArray);
            return () -> {};
        }
    }

    @FunctionalInterface
    public interface Cancellable extends AutoCloseable {
        void cancel();

        @Override
        default void close() {
            cancel();
        }
    }
}
