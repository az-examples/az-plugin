package fr.nathan818.azplugin.bukkit.item;

public abstract class ItemStackProxyAbstract implements ItemStackProxy {

    @Override
    public String toString() {
        return (
            "ItemStackProxy[" +
            getTypeId() +
            (":" + getDurability()) +
            ("*" + getAmount()) +
            // TODO: Tag
            "]"
        );
    }
}
