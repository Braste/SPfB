package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class deletewp implements CommandExecutor {
    private final SPfB plugin;

    public deletewp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.deletewp")) {
                plugin.getLogger().info(String.format("%s used SPfB.deletewp", player.getName()));
                if (args.length == 1) {
                    try {
                        if (plugin.Funcs.deleteWaypoint(player, args[0])) {
                            plugin.Funcs.sendSystemMessage(player, String.format("Wegpunkt %s auf Welt %s erfolgreich gelöscht.", args[0], player.getWorld().getName()));
                            return true;
                        }
                        else {
                            plugin.Funcs.sendSystemMessage(player, String.format("Wegpunkt %s auf Welt %s nicht gefunden.", args[0], player.getWorld().getName()));
                        }
                    } catch (SQLException | MySqlPoolableException e) {
                        e.printStackTrace();
                    }
                } else if (args.length > 1) {
                    plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                }
                return false;
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.deletewp");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
