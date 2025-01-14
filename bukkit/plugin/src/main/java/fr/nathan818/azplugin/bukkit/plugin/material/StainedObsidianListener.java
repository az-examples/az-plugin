package fr.nathan818.azplugin.bukkit.plugin.material;

import fr.nathan818.azplugin.bukkit.AZMaterial;
import java.util.ListIterator;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class StainedObsidianListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) {
            return;
        }

        boolean stainedObsidianRemoved = false;
        for (ListIterator<Block> it = event.blockList().listIterator(); it.hasNext();) {
            Block block = it.next();
            if (block.getType() == AZMaterial.STAINED_OBSIDIAN) {
                // Prevent Ender Dragon from destroying Stained Obsidian
                it.remove();
                stainedObsidianRemoved = true;
            }
        }

        if (stainedObsidianRemoved && event.blockList().isEmpty()) {
            // Cancel the event if Stained Obsidian was the only block destroyed
            event.setCancelled(true);
        }
    }
}
