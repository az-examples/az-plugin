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
public final class AZPopupAlert {

    private final @NonNull NotchianChatComponent description;
    private final @Nullable NotchianChatComponent closeEvent;

    public static AZPopupAlert build(@NotNull NotchianChatComponentLike description) {
        return new AZPopupAlert(NotchianChatComponentLike.convertNonNull(description), null);
    }

    public static AZPopupAlert build(
        @NotNull NotchianChatComponentLike description,
        @Nullable NotchianChatComponentLike closeEvent
    ) {
        return new AZPopupAlert(
            NotchianChatComponentLike.convertNonNull(description),
            NotchianChatComponentLike.convert(closeEvent)
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

        private Builder closeEvent(@Nullable NotchianChatComponent closeEvent) {
            this.closeEvent = closeEvent;
            return this;
        }

        public Builder closeEvent(@Nullable NotchianChatComponentLike closeEvent) {
            this.closeEvent = NotchianChatComponentLike.convert(closeEvent);
            return this;
        }
    }
}
