package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class changepw implements CommandExecutor {
    private final SPfB plugin;

    public changepw(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            try {
                if (!plugin.Funcs.getIsRegister(player)) {
                    plugin.Funcs.sendSystemMessage(player, "Du bist nicht registriert. Bitte registriere dich mit '/register <password>'");
                    return false;
                }
                if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.changepw")) {
                    if (args.length == 3) {
                        if (plugin.Funcs.changePassword(player, args[0], args[1], args[2])) {
                            plugin.Funcs.sendSystemMessage(player, "Passwort erfolgreich geändert");
                            return true;
                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Änderung nicht erfolgreich!:");
                        }
                    } else if (args.length > 3) {
                        plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                    }
                    return false;
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.changepw");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (MySqlPoolableException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
