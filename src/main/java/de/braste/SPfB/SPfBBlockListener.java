package de.braste.SPfB;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

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
        /*Block furnace = event.getBlock();
        try {
            if (furnace.getType() == Material.FURNACE) {
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info(String.format("Furnace: %s", furnace.getType().toString()));
                }
                Block lava = furnace.getRelative(BlockFace.DOWN);
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info(String.format("Lava: %s", lava.getType().toString()));
                }
                if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
                    BlockFace face = ((Furnace) furnace.getState().getData()).getFacing();
                    furnace.setType(Material.BURNING_FURNACE);
                    ((Furnace) furnace.getState().getData()).setFacingDirection(face);
                    ((org.bukkit.block.Furnace) furnace).setBurnTime((short)10000);
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        /*Block lava = event.getBlock();
        if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
            Block furnace = lava.getRelative(BlockFace.UP);
            if (furnace.getType() == Material.BURNING_FURNACE) {
                BlockFace face = ((Furnace) furnace.getState().getData()).getFacing();
                furnace.setType(Material.FURNACE);
                ((Furnace) furnace.getState().getData()).setFacingDirection(face);
                ((org.bukkit.block.Furnace) furnace).setBurnTime((short)0);
            }
        }*/
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

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(final BlockFromToEvent event) {
        try {
            Block lava = event.getBlock();
            if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                plugin.getLogger().info(String.format("BlockFromTo - From: %s", lava.getType().toString()));
            }
            if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA)
            {
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info(String.format("BlockFromTo - ToUpper: %s", event.getToBlock().getRelative(BlockFace.UP).getType().toString()));
                }
                if (event.getToBlock().getRelative(BlockFace.UP).getType() == Material.FURNACE)
                {
                    Block block = event.getToBlock().getRelative(BlockFace.UP);
                    BlockFace face = ((Furnace) block.getState().getData()).getFacing();
                    block.setType(Material.BURNING_FURNACE);
                    ((Furnace) block.getState().getData()).setFacingDirection(face);
                    ((org.bukkit.block.Furnace) block).setBurnTime((short)10000);
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }

    }*/

    public SPfB getPlugin() {
        return plugin;
    }
}
