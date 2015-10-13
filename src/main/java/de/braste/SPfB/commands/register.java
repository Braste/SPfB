package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class register implements CommandExecutor {
    private final SPfB plugin;

    public register(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.register")) {
                plugin.getLogger().info(player.getName() + " used SPfB.register");

                try {
                    if (!plugin.Funcs.getIsRegistered(player)) {
                        if (args.length == 2) {
                            if (plugin.Funcs.register(player, args[0], args[1])) {
                                plugin.Funcs.sendSystemMessage(player, String.format("Erfolgreich registriert. Willkommen %s!", player.getName()));
                                return true;
                            } else {
                                plugin.Funcs.sendSystemMessage(player, "Registrierung nicht erfolgreich.");
                            }
                        } else if (args.length > 2) {
                            plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                        }
                        return false;
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Du bist schon registriert. Bitte logge dich mit '/login <password>' ein");
                    }

                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }
            } else plugin.Funcs.sendSystemMessage(player, "Du hast nicht die erforderliche Berechtigung SPfB.register");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
