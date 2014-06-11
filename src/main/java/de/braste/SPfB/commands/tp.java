package de.braste.SPfB.commands;


import com.evilmidget38.UUIDFetcher;
import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class tp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public tp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("SPfB.tp"))
                return true;
            if (funcs.isLoggedIn(player)) {
                System.out.println(player.getName() + " used SPfB.tp");
                if (args.length == 1) {
                    try
                    {
                        UUID playerId = UUIDFetcher.getUUIDOf(args[0]);
                        target = player.getServer().getPlayer(playerId);
                    }
                    catch(Exception e)
                    {
                        sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                        e.printStackTrace();
                        sender.getServer().getLogger().warning("Trying with player name");
                        target = sender.getServer().getPlayer(args[0]);
                    }
                    if (target != null) {
                        player.teleport(target);
                        return true;
                    } else {
                        funcs.systemMessage(player, "Spieler " + args[0] + " wurde nicht gefunden!");
                    }
                }
                return true;
            }
            else  {
                funcs.systemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
                return true;
            }
        }
        if (args.length == 2) {
            Player source;

            try
            {
                Map<String, UUID> playerIds = UUIDFetcher.getUUIDOf(Arrays.asList(args));
                source = sender.getServer().getPlayer(playerIds.get(args[0]));
                target = sender.getServer().getPlayer(playerIds.get(args[1]));
            }
            catch(Exception e)
            {
                sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                e.printStackTrace();
                sender.getServer().getLogger().warning("Trying with player name");
                source = sender.getServer().getPlayer(args[0]);
                target = sender.getServer().getPlayer(args[1]);
            }

            if (target != null) {
                if (source != null) {
                    source.teleport(target);
                    return true;
                } else {
                    if (sender instanceof Player)
                        funcs.systemMessage((Player)sender, "Spieler " + args[1] + " wurde nicht gefunden!");
                    else
                        sender.getServer().getLogger().info("Spieler " + args[1] + " wurde nicht gefunden!");
                }
            } else {
                if (sender instanceof Player)
                    funcs.systemMessage((Player)sender, "Spieler " + args[0] + " wurde nicht gefunden!");
                else
                    sender.getServer().getLogger().info("Spieler " + args[0] + " wurde nicht gefunden!");
            }
        } else if (args.length > 2) {
            if (sender instanceof Player)
                funcs.systemMessage((Player)sender, "Zu viele Parameter:");
            else
                sender.getServer().getLogger().info("Zu viele Parameter:");
        } else {
            if (sender instanceof Player)
                funcs.systemMessage((Player)sender, "Zu wenig Parameter:");
            else
                sender.getServer().getLogger().info("Zu wenig Parameter:");
        }
        return false;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
