package fr.nathan818.azplugin.bukkit.item;

import fr.nathan818.azplugin.bukkit.AZBukkit;
import java.util.Objects;
import org.bukkit.inventory.ItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public abstract class AZBukkitItemStackAbstract implements AZBukkitItemStack {

    @Override
    public String toString() {
        ItemStack itemStack = getBukkitItemStack();
        NotchianNbtTagCompound tag = AZBukkit.platform().getItemStackTag(itemStack);
        return (
            "AZBukkitItemStack[" +
            itemStack.getTypeId() +
            (":" + itemStack.getDurability()) +
            ("*" + itemStack.getAmount()) +
            (tag == null ? "" : tag) +
            "]"
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AZBukkitItemStack)) {
            return false;
        }
        AZBukkitItemStack that = (AZBukkitItemStack) obj;
        return Objects.equals(getBukkitItemStack(), that.getBukkitItemStack());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getBukkitItemStack());
    }
}
