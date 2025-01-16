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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder", toBuilder = true)
@Getter
@ToString
public final class AZPopupConfirm {

    private final @NonNull NotchianChatComponent description;
    private final @Nullable NotchianChatComponent okEvent;
    private final @Nullable NotchianChatComponent cancelEvent;

    public static AZPopupConfirm build(
        @NotNull NotchianChatComponentLike description,
        @Nullable NotchianChatComponentLike okEvent
    ) {
        return new AZPopupConfirm(
            NotchianChatComponentLike.convertNonNull(description),
            NotchianChatComponentLike.convert(okEvent),
            null
        );
    }

    public static AZPopupConfirm build(
        @NotNull NotchianChatComponentLike description,
        @Nullable NotchianChatComponentLike okEvent,
        @Nullable NotchianChatComponentLike cancelEvent
    ) {
        return new AZPopupConfirm(
            NotchianChatComponentLike.convertNonNull(description),
            NotchianChatComponentLike.convert(okEvent),
            NotchianChatComponentLike.convert(cancelEvent)
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
    }
}
