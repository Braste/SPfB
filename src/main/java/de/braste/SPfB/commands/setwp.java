package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class setwp implements CommandExecutor {
    private final SPfB plugin;

    public setwp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.setwp")) {
                plugin.getLogger().info(String.format("%s used SPfB.setwp", player.getName()));
                if (args.length == 1) {
                    try {
                        int result = plugin.Funcs.setWaypoint(player, args[0]);
                        if (result == 1) {
                            plugin.Funcs.sendSystemMessage(player, String.format("Wegpunkt %s auf Welt %s erfolgreich gesetzt.", args[0], player.getWorld().getName()));
                            return true;
                        } else if (result == -1) {
                            plugin.Funcs.sendSystemMessage(player, String.format("Wegpunkt %s auf Welt %s bereits vorhanden.", args[0], player.getWorld().getName()));
                            return true;
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
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.setwp");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
