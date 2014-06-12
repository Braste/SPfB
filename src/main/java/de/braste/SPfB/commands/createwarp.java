package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class createwarp implements CommandExecutor {
    private final SPfB plugin;

    public createwarp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.createwarp")) {
                System.out.println(player.getName() + " used SPfB.createwarp");
                if (args.length == 1) {
                    try {
                        int ret = plugin.Funcs.setWarpPoint(player, args[0]);
                        if (ret == 1) {
                            plugin.Funcs.sendSystemMessage(player, "Globaler Wegpunkt " + args[0] + " erfolgreich gesetzt.");
                            return true;
                        }
                        else if (ret == -1)
                        {
                            plugin.Funcs.sendSystemMessage(player, "Globaler Wegpunkt " + args[0] + " existiert schon.");
                            return true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (MySqlPoolableException e) {
                        e.printStackTrace();
                    }
                } else if (args.length > 1) {
                    plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                }
                return false;
            }
            else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.createwarp");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
