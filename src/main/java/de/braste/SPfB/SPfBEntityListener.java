package de.braste.SPfB;

import de.braste.SPfBFunctions.Funcs;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class SPfBEntityListener implements Listener {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public SPfBEntityListener(final SPfB instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player && !funcs.isLoggedIn((Player)damager)) {
            event.setCancelled(true);
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
