package de.braste.SPfB.commands;

import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.lang.String.format;

public class settargetportal implements CommandExecutor {
    private final SPfB plugin;

    public settargetportal(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (!(sender instanceof Player) || (plugin.Funcs.getIsLoggedIn((Player) sender) && sender.hasPermission("SPFB.createPortal"))) {
                synchronized (SPfB.Portals) {
                    if (SPfB.Portals.containsKey(args[0])) {
                        if (SPfB.Portals.containsKey(args[1])) {
                            SPfB.Portals.get(args[0]).setTo(SPfB.Portals.get(args[1]));
                        } else {
                            if (sender instanceof Player)
                                plugin.Funcs.sendSystemMessage((Player) sender, format("Portal %s nicht gefunden", args[1]));
                            else
                                plugin.getLogger().info(format("Portal %s nicht gefunden", args[1]));
                        }
                    } else {
                        if (sender instanceof Player)
                            plugin.Funcs.sendSystemMessage((Player) sender, format("Portal %s nicht gefunden", args[0]));
                        else
                            plugin.getLogger().info(format("Portal %s nicht gefunden", args[0]));
                    }
                }
            } else {
                plugin.Funcs.sendSystemMessage((Player) sender, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPFB.createPortal");
            }
        } else if (args.length > 2) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, "Zu viele Parameter:");
            else
                plugin.getLogger().info("Zu viele Parameter:");
            return false;
        } else if (!(sender instanceof Player)) {
            plugin.getLogger().info("Zu wenig Parameter:");
            return false;
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
