package de.braste.SPfB;

import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.*;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class SPfBListener implements Listener {

    private final SPfB plugin;

    public SPfBListener(final SPfB instance) {
        plugin = instance;
    }

    //region Blocks
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        Block placedBlock = event.getBlock();
        try {
            if (placedBlock.getType() == Material.FURNACE) {
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info(String.format("Furnace: %s", placedBlock.getType().toString()));
                    plugin.getLogger().info(String.format("BlockInstance: %s", placedBlock.toString()));
                }
                Block blockUnder = placedBlock.getRelative(BlockFace.DOWN);
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info(String.format("Lava: %s", blockUnder.getType().toString()));
                }
                if (blockUnder.getType() == Material.LAVA || blockUnder.getType() == Material.STATIONARY_LAVA) {
                    plugin.FurnaceBlocks.add(blockUnder);
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
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
    //endregion

    //region Player
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            PermissionUser user = PermissionsEx.getUser(player);
            if ((int) plugin.Funcs.getConfigNode("debug", "int") == 2 && !user.inGroup("admin")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Der Server wird zur Zeit gewartet!");
            } else if (player.getName().regionMatches(true, 0, "Player", 0, 6)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, String.format("Name '%s' nicht erlaubt", player.getName()));
            }
            if (plugin.Funcs.getIsLoggedIn(player)) {
                try {
                    plugin.Funcs.logout(player);
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("SPfB.register")) {
            try {
                if (plugin.Funcs.getIsRegistered(player)) {
                    if (plugin.Funcs.login(player))
                        plugin.Funcs.sendSystemMessage(player, "Erfolgreich eingeloggt, willkommen " + player.getName() + "!");
                    else
                        plugin.Funcs.sendSystemMessage(player, "Login gescheitert! Bitte wende dich an einen Administrator!");
                } else {
                    String session = Integer.toString((int) (System.currentTimeMillis() / 1000));
                    if (plugin.Funcs.register(player, session, session))
                        plugin.Funcs.sendSystemMessage(player, String.format("Erfolgreich registriert. Willkommen %s!", player.getName()));
                }
            } catch (SQLException | MySqlPoolableException e) {
                e.printStackTrace();
            }
        } else {
            plugin.Funcs.sendSystemMessage(player, "Du bist ein Gast. Um dich registrieren zu können, wende dich bitte an einen Administrator.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        try {
            plugin.Funcs.logout(event.getPlayer());
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.startsWith("/")) {
            PermissionUser user = PermissionsEx.getUser(event.getPlayer());
            String group = user.getParentIdentifiers().get(0);
            String prefix = user.getPrefix();

            Date now = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(now);

            List<Player> list = event.getPlayer().getWorld().getPlayers();
            plugin.getLogger().info("[" + event.getPlayer().getName() + "]" + message);

            message = String.format("%s%s [%s]<%s>: %s", time, prefix, group, event.getPlayer().getName(), message);

            for (Player aList : list) {
                aList.sendMessage(message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
            }

            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().split("\\s+");

        if (split.length == 0) {
            event.setCancelled(true);
            return;
        }
        String command = split[0].substring(1);

        if (event.getPlayer().getServer().getPluginCommand(command) == null) {
            plugin.getLogger().info(String.format("Name: %s - minecraft.%s", event.getPlayer().getName(), command));

            if (!plugin.Funcs.getIsLoggedIn(event.getPlayer()) || !event.getPlayer().hasPermission("minecraft." + command)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerShearEntity(final PlayerShearEntityEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(final PlayerFishEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    //endregion

    //region Entity
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (damaged instanceof Player && (!(damaged).hasPermission("SPfB.register") || !plugin.Funcs.getIsLoggedIn((Player) damaged))) {
            event.setCancelled(true);
        }
        if (damager instanceof Player && !plugin.Funcs.getIsLoggedIn((Player) damager)) {
            event.setCancelled(true);
        }
    }
    //endregion

    //region Inventory
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFurnaceSmeltEvent(final FurnaceSmeltEvent event) {
        Block furnace = event.getBlock();
        if (furnace instanceof Furnace) {
            Block lava = furnace.getRelative(BlockFace.DOWN);
            if (lava.getType() == Material.LAVA || lava.getType() == Material.STATIONARY_LAVA) {
                ((Furnace) furnace).setBurnTime((short)10000);
            }
        }
    }
    //endregion

    public SPfB getPlugin() {
        return plugin;
    }
}