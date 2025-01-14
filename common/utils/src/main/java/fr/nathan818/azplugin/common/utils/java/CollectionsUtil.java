package fr.nathan818.azplugin.common.utils.java;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class CollectionsUtil {

    @Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
    public static <T, R> @Nullable List<R> mapToList(
        @Nullable Collection<T> list,
        @NotNull Function<? super T, ? extends R> mapper
    ) {
        if (list == null) {
            return null;
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, R> Iterable<R> transformIterable(
        @NotNull Iterable<? extends T> iterable,
        @NotNull Function<? super T, ? extends R> transformer
    ) {
        return () -> transformIterator(iterable.iterator(), transformer);
    }

    public static <T, R> Iterator<R> transformIterator(
        @NotNull Iterator<? extends T> iterator,
        @NotNull Function<? super T, ? extends R> transformer
    ) {
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return transformer.apply(iterator.next());
            }
        };
    }

    @SafeVarargs
    public static <T> Iterable<T> mergeIterables(@NotNull Iterable<? extends T> @NotNull... iterables) {
        if (iterables.length == 0) {
            return Collections.emptyList();
        }
        return () ->
            new Iterator<T>() {
                private int index = 0;
                private Iterator<? extends T> current = iterables[0].iterator();

                @Override
                public boolean hasNext() {
                    while (current != null && !current.hasNext()) {
                        if (++index >= iterables.length) {
                            return false;
                        }
                        current = iterables[index].iterator();
                    }
                    return current != null;
                }

                @Override
                public T next() {
                    return current.next();
                }
            };
    }

    public static <T> Iterable<T> filterIterable(
        @NotNull Iterable<? extends T> iterable,
        @NotNull Predicate<? super T> filter
    ) {
        return () -> filterIterator(iterable.iterator(), filter);
    }

    private static <T> Iterator<T> filterIterator(
        @NotNull Iterator<? extends T> iterator,
        @NotNull Predicate<? super T> filter
    ) {
        return new Iterator<T>() {
            private boolean hasNext = false;
            private T next = null;

            @Override
            public boolean hasNext() {
                if (hasNext) {
                    return true;
                }
                while (iterator.hasNext()) {
                    T next = iterator.next();
                    if (filter.test(next)) {
                        this.next = next;
                        hasNext = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                hasNext = false;
                return next;
            }
        };
    }
}
