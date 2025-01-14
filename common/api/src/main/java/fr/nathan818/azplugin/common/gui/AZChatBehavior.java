package fr.nathan818.azplugin.common.gui;

import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.plsp.model.PLSPRegex;
import pactify.client.api.plsp.model.SimplePLSPRegex;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
public final class AZChatBehavior {

    private final @NonNull UUID id;
    private final @Nullable PLSPRegex pattern;
    private final @Nullable NotchianChatComponent message;
    private final @Nullable String tagColor; // TODO: Replace this with an AZ chat API color object
    private final short priority;

    public boolean isSet() {
        return pattern != null;
    }

    public static AZChatBehavior remove(@NotNull UUID id) {
        return new AZChatBehavior(id, null, null, null, (short) 0);
    }

    public static class Builder {

        public Builder pattern(@Nullable PLSPRegex pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder patternRe2j(@NotNull String re2jPattern) {
            this.pattern = SimplePLSPRegex.re2j(re2jPattern);
            return this;
        }

        public Builder patternRe2j(@NotNull String re2jPattern, int flags) {
            this.pattern = SimplePLSPRegex.re2j(re2jPattern, flags);
            return this;
        }

        public Builder message(@Nullable NotchianChatComponent message) {
            this.message = message;
            return this;
        }

        public Builder message(@Nullable NotchianChatComponentLike message) {
            this.message = NotchianChatComponentLike.convert(message);
            return this;
        }

        public Builder priority(short priority) {
            this.priority = priority;
            return this;
        }

        public Builder priority(int priority) {
            if (priority < Short.MIN_VALUE || priority > Short.MAX_VALUE) {
                throw new IllegalArgumentException(
                    ("priority out of range: " + priority) +
                    (" (expected: " + Short.MIN_VALUE + ".." + Short.MAX_VALUE) +
                    ")"
                );
            }
            this.priority = (short) priority;
            return this;
        }

        public Builder unset() {
            this.pattern = null;
            this.message = null;
            this.tagColor = null;
            this.priority = 0;
            return this;
        }

        public AZChatBehavior build() {
            int setCount = 0;
            if (pattern != null) {
                ++setCount;
            }
            if (message != null) {
                ++setCount;
            }
            if (tagColor != null) {
                ++setCount;
            }
            if (priority != 0) {
                ++setCount;
            }
            if (setCount != 0 && setCount != 4) {
                throw new IllegalArgumentException(
                    "All fields must be set or unset" +
                    (" (pattern: " + (pattern != null)) +
                    (", message: " + (message != null)) +
                    (", tagColor: " + (tagColor != null)) +
                    (", priority: " + (priority != 0)) +
                    ")"
                );
            }
            return new AZChatBehavior(id, pattern, message, tagColor, priority);
        }
    }
}
