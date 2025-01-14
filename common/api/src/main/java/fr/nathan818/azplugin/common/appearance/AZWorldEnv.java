package fr.nathan818.azplugin.common.appearance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZWorldEnv {

    private final @Nullable String name;
    private final @Nullable Type type;

    public boolean isEmpty() {
        return name == null && type == null;
    }

    public static AZWorldEnv build(@Nullable String name, @Nullable Type type) {
        return new AZWorldEnv(name, type);
    }

    public enum Type {
        NORMAL,
        NETHER,
        THE_END,
    }
}
