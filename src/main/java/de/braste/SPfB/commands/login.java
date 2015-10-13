package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class login implements CommandExecutor {
    private final SPfB plugin;

    public login(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.login")) {
                plugin.getLogger().info(player.getName() + " used SPfB.login");

                try {
                    if ((int) plugin.Funcs.getConfigNode("debug", "int") >= 1) {
                        String sOut = player.getName();
                        for (String s : args) {
                            sOut += " - " + s;
                        }
                        plugin.getLogger().info(sOut);
                    }
                    if (!plugin.Funcs.getIsLoggedIn(player)) {
                        if (plugin.Funcs.getIsRegistered(player)) {
                            if (args.length == 1) {
                                if (plugin.Funcs.login(player, args[0])) {
                                    plugin.Funcs.sendSystemMessage(player, "Erfolgreich eingeloggt, willkommen " + player.getName() + "!");
                                    return true;
                                } else {
                                    plugin.Funcs.sendSystemMessage(player, "Login gescheitert!");
                                    return false;
                                }
                            } else if (args.length > 1) {
                                plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                                return false;
                            } else {
                                plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                                return false;
                            }
                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Du bist nicht registriert. Bitte registriere dich mit '/register <password> <password>'");
                            return true;
                        }
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Du bist schon eingeloggt.");
                        return true;
                    }
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
