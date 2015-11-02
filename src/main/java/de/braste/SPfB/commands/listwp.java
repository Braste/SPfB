package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class listwp implements CommandExecutor {
    private final SPfB plugin;

    public listwp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.listwp")) {
                if (args.length == 0) {
                    try {
                        showWaypoints(sender, plugin.Funcs.listWaypoints(player));
                    } catch (SQLException | MySqlPoolableException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.listwp");
        }
        if (args.length == 1) {
            if (!(sender instanceof Player) || (plugin.Funcs.getIsLoggedIn((Player) sender) && sender.hasPermission("SPfB.listallwp"))) {
                try {
                    showWaypoints(sender, plugin.Funcs.listWaypoints(args[0]));
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                plugin.Funcs.sendSystemMessage((Player) sender, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.listallwp");
            }
        } else if (args.length > 1) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, "Zu viele Parameter:");
            else
                plugin.getLogger().info("Zu viele Parameter:");
            return false;
        }
        return true;
    }

    private void showWaypoints(CommandSender sender, List<String[]> waypoints) {
        for (String[] s : waypoints) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, String.format("%s: %s", s[1], s[0]));
            else
                plugin.getLogger().info(String.format("%s: %s", s[1], s[0]));
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
