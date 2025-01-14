package fr.nathan818.azplugin.bukkit.chat;

import fr.nathan818.azplugin.common.AZConstants;
import fr.nathan818.azplugin.common.chat.NotchianChatComponentLike;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;

public interface AZBungeeChatComponent extends NotchianChatComponent, NotchianChatComponentLike {
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
}
