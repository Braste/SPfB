package de.braste.SPfB;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

class SPfBInventoryListener implements Listener {

    private final SPfB plugin;

    public SPfBInventoryListener(final SPfB instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFurnaceSmeltEvent(final FurnaceSmeltEvent event) {
        Block furnace = event.getBlock();
        if (furnace instanceof Furnace) {
            Block lava = furnace.getRelative(BlockFace.DOWN);
            if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
                furnace.setType(Material.BURNING_FURNACE);
            }
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
