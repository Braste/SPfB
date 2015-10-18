package de.braste.SPfB;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

class SPfBBlockListener implements Listener {

    private final SPfB plugin;

    public SPfBBlockListener(final SPfB instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        Block furnace = event.getBlockPlaced();
        if (furnace instanceof Furnace) {
            Block lava = furnace.getRelative(BlockFace.DOWN);
            if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
                furnace.setType(Material.BURNING_FURNACE);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        Block lava = event.getBlock();
        if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
            Block furnace = lava.getRelative(BlockFace.UP);
            if (furnace.getType() == Material.BURNING_FURNACE) {
                furnace.setType(Material.FURNACE);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(final BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(final SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(final BlockFromToEvent event) {
        Block lava = event.getBlock();
        if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA)
        {
            if (event.getToBlock().getRelative(BlockFace.UP).getType() == Material.FURNACE)
            {
                event.getToBlock().getRelative(BlockFace.UP).setType(Material.BURNING_FURNACE);
            }
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
