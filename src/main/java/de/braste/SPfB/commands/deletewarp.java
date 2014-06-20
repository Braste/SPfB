package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class deletewarp implements CommandExecutor {
    private final SPfB plugin;

    public deletewarp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.deletewarp")) {
                plugin.getLogger().info(player.getName() + " used SPfB.deletewarp");

                if (args.length == 1) {
                    try {
                        if (plugin.Funcs.deleteWarpPoint(player, args[0]))
                            plugin.Funcs.sendSystemMessage(player, "Warppunkt " + args[0] + " erfolgreich gelöscht.");
                        else
                            plugin.Funcs.sendSystemMessage(player, "Warppunkt " + args[0] + " konnte nicht gelöscht werden.");
                    } catch (SQLException | MySqlPoolableException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (args.length > 1) {
                    plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                }
                return false;
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.deletewarp");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
