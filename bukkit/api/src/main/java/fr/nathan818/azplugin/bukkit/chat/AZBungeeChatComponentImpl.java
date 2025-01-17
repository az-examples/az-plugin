package fr.nathan818.azplugin.bukkit.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
final class AZBungeeChatComponentImpl implements AZBungeeChatComponent {

    static final AZBungeeChatComponentImpl EMPTY = new AZBungeeChatComponentImpl(new BaseComponent[0]);

    private final @NonNull BaseComponent[] bungeeComponent;

    static BaseComponent[] duplicate(BaseComponent[] component) {
        component = component.clone();
        for (int i = 0; i < component.length; i++) {
            component[i] = component[i].duplicate();
        }
        return component;
    }

    @Override
    public String toString() {
        return AZBungeeChatComponent.toString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return AZBungeeChatComponent.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return AZBungeeChatComponent.hashCode(this);
    }
}
