package de.braste.SPfB;

import de.braste.SPfBFunctions.Funcs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

class SPfBBlockListener implements Listener {

    private final SPfB plugin;
    private final Funcs funcs = new Funcs();
    private int blocksPlaced = 0;

    public SPfBBlockListener(final SPfB instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!funcs.isLoggedIn(player)) {
            event.setCancelled(true);
        }
        else
        {
            blocksPlaced++;
            if (blocksPlaced > 200)
            {
                blocksPlaced = 0;
                new HelperThread().start();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!funcs.isLoggedIn(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(final SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!funcs.isLoggedIn(player)) {
            event.setCancelled(true);
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
