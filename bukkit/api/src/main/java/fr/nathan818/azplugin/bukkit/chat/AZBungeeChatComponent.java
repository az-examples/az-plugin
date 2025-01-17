package fr.nathan818.azplugin.bukkit.chat;

import fr.nathan818.azplugin.common.AZConstants;
import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import java.util.Arrays;
import java.util.function.Consumer;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;

public interface AZBungeeChatComponent extends NotchianChatComponent, NotchianChatComponentLike {
    static @NotNull AZBungeeChatComponent empty() {
        return AZBungeeChatComponentImpl.EMPTY;
    }

    @Contract("null -> null; !null -> new")
    static @Nullable AZBungeeChatComponent copyOf(@NotNull BaseComponent @Nullable... component) {
        if (component == null) {
            return null;
        }
        return new AZBungeeChatComponentImpl(AZBungeeChatComponentImpl.duplicate(component));
    }

    @Contract("null -> null; !null -> new")
    static @Nullable AZBungeeChatComponent mirrorOf(@NotNull BaseComponent @Nullable... component) {
        if (component == null) {
            return null;
        }
        return new AZBungeeChatComponentImpl(component);
    }

    static @NotNull AZBungeeChatComponent build(@NotNull Consumer<ComponentBuilder> builderConsumer) {
        ComponentBuilder builder = new ComponentBuilder("");
        builderConsumer.accept(builder);
        return mirrorOf(builder.create());
    }

    static @NotNull AZBungeeChatComponent build(
        @NonNull String text,
        @NotNull Consumer<ComponentBuilder> builderConsumer
    ) {
        ComponentBuilder builder = new ComponentBuilder(text);
        builderConsumer.accept(builder);
        return mirrorOf(builder.create());
    }

    static @NotNull AZBungeeChatComponent fromLegacyText(@NotNull String text) {
        return mirrorOf(TextComponent.fromLegacyText(text));
    }

    static @NotNull AZBungeeChatComponent fromJson(@NotNull String json) {
        return mirrorOf(ComponentSerializer.parse(json));
    }

    @NotNull
    BaseComponent@NotNull[] getBungeeComponent();

    @Override
    default void write(NotchianPacketBuffer buf) {
        BaseComponent[] bungeeComponent = getBungeeComponent();
        String json;
        if (bungeeComponent.length == 1) {
            json = ComponentSerializer.toString(bungeeComponent[0]);
        } else {
            json = ComponentSerializer.toString(bungeeComponent);
        }
        NotchianPacketUtil.writeString(buf, json, AZConstants.CHAT_COMPONENT_MAX_LENGTH);
    }

    @Override
    default AZBungeeChatComponent shallowClone() {
        return mirrorOf(getBungeeComponent());
    }

    @Override
    default AZBungeeChatComponent deepClone() {
        return copyOf(getBungeeComponent());
    }

    @Override
    default AZBungeeChatComponent asNotchianChatComponent() {
        return this;
    }

    static String toString(@Nullable AZBungeeChatComponent component) {
        if (component == null) {
            return "null";
        }
        return "AZBungeeChatComponent[\"" + TextComponent.toPlainText(component.getBungeeComponent()) + "\"]";
    }

    static boolean equals(@Nullable AZBungeeChatComponent a, @Nullable Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || !(b instanceof AZBungeeChatComponent)) {
            return false;
        }
        AZBungeeChatComponent that = (AZBungeeChatComponent) b;
        return Arrays.equals(a.getBungeeComponent(), that.getBungeeComponent());
    }

    static int hashCode(@Nullable AZBungeeChatComponent component) {
        if (component == null) {
            return 0;
        }
        return Arrays.hashCode(component.getBungeeComponent());
    }
}
