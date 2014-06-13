package de.braste.SPfB.commands;


import com.evilmidget38.UUIDFetcher;
import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
//TODO
public class setgroup implements CommandExecutor {
    private final SPfB plugin;

    public setgroup(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (!(sender instanceof Player)) {
            boolean ret = false;

            if (args.length == 2) {
                Player target;
                try
                {
                    UUID playerId = UUIDFetcher.getUUIDOf(args[0]);
                    target = sender.getServer().getPlayer(playerId);
                }
                catch(Exception e)
                {
                    sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                    e.printStackTrace();
                    sender.getServer().getLogger().warning("Trying with player name");
                    target = sender.getServer().matchPlayer(args[0]).get(0);
                }
                if (target != null)
                {
                    funcs.setGroup(args[0], args[1]);
                    System.out.println("Gruppe von " + args[0] + " erfolgreiche auf " + args[1] + " gesetzt");
                    ret = true;
                }
            } else if (args.length > 2) {
                System.out.println("Zu viele Parameter:");
            } else {
                System.out.println("Zu wenig Parameter:");
            }
            return ret;
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.setgroup")) {
                System.out.println(player.getName() + " used SPfB.setgroup");
                if (plugin.Funcs.getIsLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length == 2) {
                        Player target;
                        try
                        {
                            UUID playerId = UUIDFetcher.getUUIDOf(args[0]);
                            target = sender.getServer().getPlayer(playerId);
                        }
                        catch(Exception e)
                        {
                            sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                            e.printStackTrace();
                            sender.getServer().getLogger().warning("Trying with player name");
                            target = sender.getServer().matchPlayer(args[0]).get(0);
                        }
                        if (!funcs.isAdmin(target)) {
                            funcs.setGroup(args[0], args[1]);
                            plugin.Funcs.sendSystemMessage(player, "Gruppe von " + args[0] + " erfolgreiche auf " + args[1] + " gesetzt");
                            ret = true;
                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Die Gruppe von Administratoren kann nicht geändert werden!");
                        }
                    } else if (args.length > 2) {
                        plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                    }
                    return ret;
                }
                else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
            }
        }*/
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
