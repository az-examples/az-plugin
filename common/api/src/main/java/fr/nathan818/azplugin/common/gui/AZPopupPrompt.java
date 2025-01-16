package fr.nathan818.azplugin.common.gui;

import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import lombok.AccessLevel;
import lombok.Builder;
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
public final class AZPopupPrompt {

    private final @NonNull NotchianChatComponent description;
    private final @Nullable NotchianChatComponent okEvent;
    private final @Nullable NotchianChatComponent cancelEvent;
    private final @Nullable String defaultValue;
    private final @Nullable PLSPRegex typingRegex;
    private final @Nullable PLSPRegex finalRegex;
    private final boolean password;

    public static AZPopupPrompt build(
        @NotNull NotchianChatComponentLike description,
        @Nullable NotchianChatComponentLike okEvent
    ) {
        return new AZPopupPrompt(
            NotchianChatComponentLike.convertNonNull(description),
            NotchianChatComponentLike.convert(okEvent),
            null,
            null,
            null,
            null,
            false
        );
    }

    public static AZPopupPrompt build(
        @NotNull NotchianChatComponentLike description,
        @Nullable NotchianChatComponentLike okEvent,
        @Nullable NotchianChatComponentLike cancelEvent
    ) {
        return new AZPopupPrompt(
            NotchianChatComponentLike.convertNonNull(description),
            NotchianChatComponentLike.convert(okEvent),
            NotchianChatComponentLike.convert(cancelEvent),
            null,
            null,
            null,
            false
        );
    }

    public static class Builder {

        private Builder description(@NotNull NotchianChatComponent description) {
            this.description = description;
            return this;
        }

        public Builder description(@NotNull NotchianChatComponentLike description) {
            this.description = NotchianChatComponentLike.convertNonNull(description);
            return this;
        }

        private Builder okEvent(@Nullable NotchianChatComponent okEvent) {
            this.okEvent = okEvent;
            return this;
        }

        public Builder okEvent(@Nullable NotchianChatComponentLike okEvent) {
            this.okEvent = NotchianChatComponentLike.convert(okEvent);
            return this;
        }

        private Builder cancelEvent(@Nullable NotchianChatComponent cancelEvent) {
            this.cancelEvent = cancelEvent;
            return this;
        }

        public Builder cancelEvent(@Nullable NotchianChatComponentLike cancelEvent) {
            this.cancelEvent = NotchianChatComponentLike.convert(cancelEvent);
            return this;
        }

        public Builder typingRegex(@Nullable PLSPRegex typingRegex) {
            this.typingRegex = typingRegex;
            return this;
        }

        public Builder typingRegexRe2j(@NotNull String re2jPattern) {
            this.typingRegex = SimplePLSPRegex.re2j(re2jPattern);
            return this;
        }

        public Builder typingRegexRe2j(@NotNull String re2jPattern, int flags) {
            this.typingRegex = SimplePLSPRegex.re2j(re2jPattern, flags);
            return this;
        }

        public Builder finalRegex(@Nullable PLSPRegex finalRegex) {
            this.finalRegex = finalRegex;
            return this;
        }

        public Builder finalRegexRe2j(@NotNull String re2jPattern) {
            this.finalRegex = SimplePLSPRegex.re2j(re2jPattern);
            return this;
        }

        public Builder finalRegexRe2j(@NotNull String re2jPattern, int flags) {
            this.finalRegex = SimplePLSPRegex.re2j(re2jPattern, flags);
            return this;
        }
    }
}
