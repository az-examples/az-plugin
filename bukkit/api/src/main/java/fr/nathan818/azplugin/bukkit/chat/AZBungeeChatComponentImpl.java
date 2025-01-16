package fr.nathan818.azplugin.bukkit.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
final class AZBungeeChatComponentImpl extends AZBungeeChatComponentAbstract {

    static final AZBungeeChatComponentImpl EMPTY = new AZBungeeChatComponentImpl(new BaseComponent[0]);

    private final @NonNull BaseComponent[] bungeeComponent;

    static BaseComponent[] duplicate(BaseComponent[] component) {
        component = component.clone();
        for (int i = 0; i < component.length; i++) {
            component[i] = component[i].duplicate();
        }
        return component;
    }
}
