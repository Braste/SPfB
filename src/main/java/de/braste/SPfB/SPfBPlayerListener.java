package de.braste.SPfB;

import de.braste.SPfBFunctions.Funcs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class SPfBPlayerListener implements Listener {

    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public SPfBPlayerListener(final SPfB instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (funcs.getConfigNode("debug") == 2 && !funcs.isAdmin(player)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server wird zur Zeit gewartet!");
        } else if (player.getName().regionMatches(true, 0, "Player", 0, 6)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Name '" + player.getName() + "' not allowed");
        }
        funcs.debug(player);

        if (funcs.isLoggedIn(player)) {
            funcs.logout(player);
        }
        player = null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (funcs.isInDatabase(player)) {
            funcs.setIP(player);
            funcs.setOnline(player, 1);

            if (funcs.isInGroup(player, "user")) {

                if (funcs.isRegistered(player)) {
                    funcs.systemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
                } else {
                    funcs.systemMessage(player, "Du bist nicht registriert. Bitte registriere dich mit '/register <password> <password>'");
                }
            } else {
                funcs.systemMessage(player, "Du bist ein Gast. Um dich registrieren zu können, wende dich bitte an einen Administrator.");
            }
        } else if (funcs.insertPlayer(player)) {
            funcs.systemMessage(player, "Du bist ein Gast. Um dich registrieren zu können, wende dich bitte an einen Administrator.");
        }
        player = null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        funcs.logout(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.startsWith("/")) {
            String group = funcs.getGroup(event.getPlayer().getName());
            String prefix = funcs.getGroupPrefix(group);

            Date now = new Date();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(now);

            List<Player> list = event.getPlayer().getWorld().getPlayers();
            System.out.println("[" + event.getPlayer().getName() + "]" + message);

            message = time + prefix + " [" + group + "]<" + event.getPlayer().getName() + ">: " + message;

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
        split = null;

        if (event.getPlayer().getServer().getPluginCommand(command) == null) {
            System.out.println("Name: " + event.getPlayer().getName() + " - minecraft." + command);

            if (!funcs.isLoggedIn(event.getPlayer()) || !funcs.canUseCommand(event.getPlayer().getName(), "minecraft." + command)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (!funcs.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
