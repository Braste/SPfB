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
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceInventory;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

class SPfBListener implements Listener {

    private final SPfB plugin;
    private final short burnTimeAdd = (short)10000;

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
        if (placedBlock.getType() == Material.FURNACE) {
            Block blockUnder = placedBlock.getRelative(DOWN);
            if (blockUnder.getType() == Material.LAVA || blockUnder.getType() == Material.STATIONARY_LAVA) {
                ((Furnace) placedBlock.getState()).setBurnTime(burnTimeAdd);
                AddFurnace(placedBlock, "onBlockPlace");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        Block blockBreak = event.getBlock();
        if (blockBreak.getType() == Material.LAVA || blockBreak.getType() == Material.STATIONARY_LAVA) {
            Block blockOver = blockBreak.getRelative(UP);
            if (blockOver.getType() == Material.BURNING_FURNACE || blockOver.getType() == Material.FURNACE) {
                RemoveFurnace(blockOver, "onBlockBreak");
                ((Furnace) blockBreak.getState()).setBurnTime((short)0);
            }
        } else if (plugin.FurnaceBlocks.contains(blockBreak)) {
            RemoveFurnace(blockBreak, "onBlockBreak");
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
        Block blockFrom = event.getBlock();
        if (blockFrom.getType() == Material.LAVA || blockFrom.getType() == Material.STATIONARY_LAVA) {
            Block blockOver = event.getToBlock().getRelative(UP);
            if (blockOver.getType() == Material.BURNING_FURNACE || blockOver.getType() == Material.FURNACE) {
                ((Furnace) blockOver.getState()).setBurnTime(burnTimeAdd);
                AddFurnace(blockOver, "onBlockFromTo");
            }
        }
    }
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
        if (event.getBucket() == Material.LAVA_BUCKET)
        {
            BlockFace face = event.getBlockFace();
            Block blockToCheck = event.getBlockClicked().getRelative(face).getRelative(BlockFace.UP);
            if (blockToCheck.getType() == Material.BURNING_FURNACE || blockToCheck.getType() == Material.FURNACE) {
                AddFurnace(blockToCheck, "onBlockFromTo");
                ((Furnace) blockToCheck.getState()).setBurnTime(burnTimeAdd);
            }
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
        Block block = event.getBlock();
        if (plugin.FurnaceBlocks.contains(block)) {
            Block blockUnder = block.getRelative(DOWN);
            if (blockUnder.getType() == Material.LAVA || blockUnder.getType() == Material.STATIONARY_LAVA) {
                ((Furnace) block.getState()).setBurnTime(burnTimeAdd);
                AddFurnace(block, "onFurnaceSmeltEvent");
            } else {
                RemoveFurnace(block, "onFurnaceSmeltEvent");
                ((Furnace) block.getState()).setBurnTime((short) 0);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFurnaceClickedEvent(final InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.FURNACE) {
            Furnace furnace = ((FurnaceInventory) event.getInventory()).getHolder();
            Block blockUnder = furnace.getBlock().getRelative(DOWN);
            if (blockUnder.getType() == Material.LAVA || blockUnder.getType() == Material.STATIONARY_LAVA) {
                try
                {
                    if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                        plugin.getLogger().info(String.format("SlotType: %s", event.getSlotType().toString()));
                    }
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
                if (event.getSlotType() == InventoryType.SlotType.FUEL) {
                    event.setCancelled(true);
                } else if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                    furnace.setBurnTime(burnTimeAdd);
                    AddFurnace(furnace.getBlock(), "onFurnaceClickedEvent");
                }
            } else if (plugin.FurnaceBlocks.contains(furnace.getBlock())) {
                RemoveFurnace(furnace.getBlock(), "onFurnaceClickedEvent");
                furnace.setBurnTime((short)0);
            }
        }
    }
    //endregion

    private void AddFurnace(Block furnace, String function)
    {
        if (!plugin.FurnaceBlocks.contains(furnace)) {
            plugin.FurnaceBlocks.add(furnace);
        }
        try
        {
            if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                plugin.getLogger().info(String.format("Function: %s", function));
                plugin.getLogger().info(String.format("FurnaceBlocks: %s", plugin.FurnaceBlocks.size()));
                for (Block b: plugin.FurnaceBlocks)
                {
                    plugin.getLogger().info(String.format("BlockInFurnaceBlocks: %s", b.toString()));
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
    }
    private void RemoveFurnace(Block furnace, String function)
    {
        if (plugin.FurnaceBlocks.contains(furnace)) {
            plugin.FurnaceBlocks.remove(furnace);
        }
        try
        {
            if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                plugin.getLogger().info(String.format("Function: %s", function));
                plugin.getLogger().info(String.format("FurnaceBlocks: %s", plugin.FurnaceBlocks.size()));
                for (Block b: plugin.FurnaceBlocks)
                {
                    plugin.getLogger().info(String.format("BlockInFurnaceBlocks: %s", b.toString()));
                }
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
