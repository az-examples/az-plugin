package fr.nathan818.azplugin.common.utils;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JvmMagic {

    public static boolean removeJarFromClassLoader(ClassLoader classLoader, URL jar) {
        Object ucp = getUCP(classLoader);
        if (ucp == null) {
            return false;
        }

        boolean removed = false;
        synchronized (ucp) {
            removed |= removeURL(ucp, "path", jar, false);
            removed |= removeURL(ucp, "urls", jar, true); // Java 10-
            removed |= removeURL(ucp, "unopenedUrls", jar, true); // Java 11+
            removeURL(ucp, "loaders", jar, false);
            removeURL(ucp, "lmap", jar, false);
        }
        return removed;
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static Object getUCP(ClassLoader classLoader) {
        Field ucpField = findDeclaredField(classLoader.getClass(), "ucp");
        if (ucpField == null) {
            return null;
        }
        ucpField.setAccessible(true);
        return ucpField.get(classLoader);
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static boolean removeURL(Object ucp, String fieldName, URL target, boolean optional) {
        Field field;
        try {
            field = ucp.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (optional) {
                return false;
            }
            throw ex;
        }
        field.setAccessible(true);

        Object urlsObj = field.get(ucp);
        if (urlsObj == null) {
            return true;
        }
        if (urlsObj instanceof Collection) {
            return removeURL((Collection<?>) urlsObj, target);
        }
        if (urlsObj instanceof Map) {
            return removeURL((Map<?, ?>) urlsObj, target);
        }
        throw new IllegalStateException("Unsupported URL field type: " + urlsObj.getClass());
    }

    private static boolean removeURL(Collection<?> urlsCollection, URL target) {
        for (Iterator<?> it = urlsCollection.iterator(); it.hasNext();) {
            Object url = it.next();
            if (matchURL(url, target)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    private static boolean removeURL(Map<?, ?> urlsObj, URL target) {
        for (Iterator<?> it = urlsObj.entrySet().iterator(); it.hasNext();) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            if (matchURL(entry.getValue(), target)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static boolean matchURL(Object value, URL target) {
        if (target.equals(value)) {
            return true;
        }
        try {
            Field csuField = value.getClass().getDeclaredField("csu");
            csuField.setAccessible(true);
            Object csu = csuField.get(value);
            if (target.equals(csu)) {
                return true;
            }
        } catch (NoSuchFieldException ignored) {}
        return false;
    }

    private static Field findDeclaredField(Class<?> clazz, String fieldName) {
        do {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {}
        } while ((clazz = clazz.getSuperclass()) != null);
        return null;
    }
}
