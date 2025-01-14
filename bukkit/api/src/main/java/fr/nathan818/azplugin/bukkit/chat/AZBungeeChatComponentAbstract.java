package fr.nathan818.azplugin.bukkit.chat;

import java.util.Arrays;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class AZBungeeChatComponentAbstract implements AZBungeeChatComponent {

    @Override
    public String toString() {
        return "AZBungeeChatComponent[\"" + TextComponent.toPlainText(getBungeeComponent()) + "\"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AZBungeeChatComponent)) {
            return false;
        }
        AZBungeeChatComponent that = (AZBungeeChatComponent) obj;
        return Arrays.equals(getBungeeComponent(), that.getBungeeComponent());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBungeeComponent());
    }
}
