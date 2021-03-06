package de.braste.SPfB;

import de.braste.SPfB.exceptions.MySqlPoolableException;
import de.braste.SPfB.object.Gate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceInventory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.block.BlockFace.DOWN;

class SPfBListener implements Listener {

    private final SPfB plugin;
    private final HashMap<Player, Location> playerLocationAtEvent = new HashMap<>();

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
        synchronized (SPfB.Portals) {
            for (Gate g : SPfB.Portals.values()) {
                if (g.getIsValid() && g.containsBlock(event.getBlockReplacedState().getBlock())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (placedBlock.getType() == Material.FURNACE) {
            Block blockUnder = placedBlock.getRelative(DOWN);
            if (blockUnder.getType() == Material.LAVA) {
                AddFurnace(placedBlock, event);
            }
        }
        Block blockUpper = placedBlock.getRelative(BlockFace.UP);
        if (blockUpper.getType() == Material.FURNACE) {
            RemoveFurnace(blockUpper, event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.Funcs.getIsLoggedIn(player)) {
            event.setCancelled(true);
        }
        Block blockBreak = event.getBlock();
        synchronized (SPfB.Portals) {
            for (Gate g : SPfB.Portals.values()) {
                if (!g.getIsValid() || !g.containsFrameBlock(blockBreak))
                    continue;
                for (Gate g2 : SPfB.Portals.values()) {
                    if (g2.getIsValid() && g.equals(g2.getTo())) {
                        g2.setTo(null);
                    }
                }
                SPfB.Portals.remove(g.getId());
                plugin.Funcs.sendSystemMessage(player, "Portal " + g.getId() + " zerstört!");
                g.removeGate();
                break;
            }
        }
        synchronized (plugin.FurnaceBlocks) {
            if (!plugin.FurnaceBlocks.containsKey(blockBreak)) {
                return;
            }
        }
        RemoveFurnace(blockBreak, event);
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
        if (event.getLine(0).equals("[portal]"))
        {
            String id = event.getLine(1);
            BlockState state = event.getBlock().getState();
            BlockData data = state.getBlockData();
            BlockFace face = (( org.bukkit.block.data.type.Furnace)data).getFacing();
            Block b = event.getBlock().getRelative(face.getOppositeFace());

            if (!id.equals("") && event.getPlayer().hasPermission("SPFB.createPortal")) {
                Material mat = Material.WATER;
                Gate gate;
                synchronized (SPfB.Portals) {
                    for (Gate g : SPfB.Portals.values()) {
                        if (g.getId().equals(id) || g.containsBlock(b))
                            return;
                    }
                    gate = new Gate(id, mat, face.getOppositeFace(), b, false);
                    if (!gate.getIsValid())
                        return;
                    SPfB.Portals.put(id, gate);
                    if (!event.getLine(2).equals("")) {
                        if (SPfB.Portals.containsKey(event.getLine(2))) {
                            gate.setTo(SPfB.Portals.get(event.getLine(2)));
                        }
                    }
                    plugin.Funcs.sendSystemMessage(player, "Portal " + id + " erfolgreich erstellt!");
                    event.getBlock().breakNaturally();
                }
            } else if (event.getPlayer().hasPermission("SPFB.getPortalInfo")) {
                synchronized (SPfB.Portals) {
                    for (Gate g : SPfB.Portals.values()) {
                        if (g.getIsValid() && g.containsBlock(b)) {
                            event.setLine(1, "ID: " + g.getId());
                            event.setLine(2, "Target: " + g.getToId());
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material mat = event.getChangedType();

        if (block.getType() == Material.FURNACE && (mat == Material.AIR || mat == Material.LAVA)) {
            Block blockUnder = block.getRelative(BlockFace.DOWN);
            if (blockUnder.getType() == Material.LAVA) {
                AddFurnace(block, event);
            }
            else {
                RemoveFurnace(block, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (event.isCancelled())
            return;

        final Block block = event.getBlock();

        if (!block.getType().equals(Material.WATER) && !block.getType().equals(Material.LAVA))
            return;

        synchronized (SPfB.Portals) {
            SPfB.Portals.values().stream().filter(g -> g.getIsValid() && g.containsBlock(block)).forEach(g -> {
                event.setCancelled(true);
            });
        }
    }
    //endregion

    //region Player
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player player = event.getPlayer();

        try {
            if ((int) plugin.Funcs.getConfigNode("debug", "int") == 2 && !SPfB.Perms.playerInGroup(player, "admin")) {
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
            Player player = event.getPlayer();
            String group = SPfB.Perms.getPrimaryGroup(player);
            String prefix = SPfB.Chat.getPlayerPrefix(player) != null ? SPfB.Chat.getPlayerPrefix(player) : SPfB.Chat.getGroupPrefix(player.getWorld(), group);

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
    public void onPlayerPickupItem(final PlayerAttemptPickupItemEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock != null && clickedBlock.getType() != null && clickedBlock.getType() == Material.FURNACE) {
            Block blockUnder = clickedBlock.getRelative(BlockFace.DOWN);
            if (blockUnder != null && blockUnder.getType() != null && blockUnder.getType() == Material.LAVA) {
                AddFurnace(clickedBlock, event);
            }

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
            if (blockToCheck.getType() == Material.FURNACE) {
                AddFurnace(blockToCheck, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (!plugin.Funcs.getIsLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }

        Location loc = playerLocationAtEvent.get(event.getPlayer());
        Block b = event.getPlayer().getWorld().getBlockAt(loc);
        synchronized (SPfB.Portals) {
            for (Gate g : SPfB.Portals.values()) {
                if (g.getIsValid() && g.containsBlock(b)) {
                    //event.getPortalTravelAgent().setCanCreatePortal(false);
                    event.setCancelled(true);
                    if (g.getTo() != null) {
                        //event.setTo(g.getTo().getTeleportLocation());
                        event.getPlayer().teleport(g.getTo().getTeleportLocation());
                        return;
                    }
                    if (g.getTeleportLocation() != null)
                        //event.setTo(g.getTeleportLocation());
                        event.getPlayer().teleport(g.getTeleportLocation());
                    return;
                }
            }
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent event) {
        Block b = event.getPlayer().getWorld().getBlockAt(event.getTo());
        synchronized (SPfB.Portals) {
            for (Gate g : SPfB.Portals.values()) {
                if (g.getIsValid() && g.containsBlock(b)) {
                    //event.setCancelled(true);
                    if (g.getTo() != null) {
                        event.getPlayer().teleport(g.getTo().getTeleportLocation());
                        return;
                    }
                    if (g.getTeleportLocation() != null)
                        event.getPlayer().teleport(g.getTeleportLocation());
                    return;
                }
            }
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageEvent event) {

        if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            Location loc = event.getEntity().getLocation();
            Block b = event.getEntity().getWorld().getBlockAt(loc);
            synchronized (SPfB.Portals) {
                for (Gate g : SPfB.Portals.values()) {
                    if (g.getIsValid() && g.containsBlock(b)) {
                        event.setCancelled(true);
                        event.getEntity().setFireTicks(0);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityPortalEnterEvent(final EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            final Location playerLocation = event.getLocation();
            this.playerLocationAtEvent.put(player, playerLocation);
        }
    }
    //endregion

    //region Inventory
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFurnaceSmeltEvent(final FurnaceSmeltEvent event) {
        Block block = event.getBlock();
        synchronized (plugin.FurnaceBlocks) {
            if (!plugin.FurnaceBlocks.containsKey(block)) {
                return;
            }
        }
        Block blockUnder = block.getRelative(DOWN);
        if (blockUnder.getType() == Material.LAVA) {
            AddFurnace(block, event);
        } else {
            RemoveFurnace(block, event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFurnaceClickedEvent(final InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.FURNACE) {
            Furnace furnace = ((FurnaceInventory) event.getInventory()).getHolder();
            synchronized (plugin.FurnaceBlocks) {
                if (!plugin.FurnaceBlocks.containsKey(furnace.getBlock())) {
                    return;
                }
            }
            if (event.getSlotType() == InventoryType.SlotType.FUEL) {
                event.setCancelled(true);
            }
        }
    }
    //endregion

    private void AddFurnace(Block furnace, Event event)
    {

        BlockState state = furnace.getState();
        BlockData data = state.getBlockData();
        BlockFace face = (( org.bukkit.block.data.type.Furnace)data).getFacing();
        ((org.bukkit.block.data.type.Furnace)data).setLit(true);
        state = furnace.getState();

        ((org.bukkit.block.data.type.Furnace)data).setFacing(face);
        state.setBlockData(data);
        short burnTimeAdd = (short) 10000;
        ((Furnace) state).setBurnTime(burnTimeAdd);
        FurnaceInventory inventory = ((Furnace) state).getInventory();
        state.update(true);

        synchronized (plugin.FurnaceBlocks) {
            if (!plugin.FurnaceBlocks.containsKey(furnace)) {
                plugin.FurnaceBlocks.put(furnace, null);
                try {
                    if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                        plugin.getLogger().info("Furnace added");
                        plugin.getLogger().info(String.format("Event: %s", event.getEventName()));
                        plugin.getLogger().info(String.format("FurnaceBlocks: %s", plugin.FurnaceBlocks.size()));
                        plugin.getLogger().info(String.format("Inventory: %s", inventory));

                        for (Block b : plugin.FurnaceBlocks.keySet()) {
                            plugin.getLogger().info(String.format("BlockInFurnaceBlocks: %s", b.toString()));
                        }
                    }
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void RemoveFurnace(Block furnace, Event event)
    {
        synchronized (plugin.FurnaceBlocks) {
            if (plugin.FurnaceBlocks.containsKey(furnace)) {
                plugin.FurnaceBlocks.remove(furnace);

                BlockState state = furnace.getState();
                BlockData data = state.getBlockData();
                BlockFace face = (( org.bukkit.block.data.type.Furnace)data).getFacing();
                furnace.setType(Material.FURNACE);
                state = furnace.getState();
                ((org.bukkit.block.data.type.Furnace)data).setFacing(face);
                state.setBlockData(data);
                ((Furnace) state).setBurnTime((short) 0);
                state.update(true);
            }
            try {
                if ((int) plugin.Funcs.getConfigNode("debug", "int") > 0) {
                    plugin.getLogger().info("Furnace removed");
                    plugin.getLogger().info(String.format("Event: %s", event.getEventName()));
                    plugin.getLogger().info(String.format("FurnaceBlocks: %s", plugin.FurnaceBlocks.size()));
                    for (Block b : plugin.FurnaceBlocks.keySet()) {
                        plugin.getLogger().info(String.format("BlockInFurnaceBlocks: %s", b.toString()));
                    }
                }
            } catch (SQLException | MySqlPoolableException e) {
                e.printStackTrace();
            }
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
