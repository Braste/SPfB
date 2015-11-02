package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class removereg implements CommandExecutor {
    private final SPfB plugin;

    public removereg(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || (plugin.Funcs.getIsLoggedIn((Player)sender) && sender.hasPermission("SPfB.removereg"))) {
            if (args.length == 1) {
                UUID playerId = plugin.Funcs.getUUID(args[0]);
                try {
                    if (plugin.Funcs.removeReg(playerId)) {
                        if (sender instanceof Player)
                            plugin.Funcs.sendSystemMessage((Player)sender, "Registrierung von " + args[0] + " erfolgreich gelöscht");
                        else
                            plugin.getLogger().info("Registrierung von " + args[0] + " erfolgreich gelöscht");
                    } else {
                        if (sender instanceof Player)
                            plugin.Funcs.sendSystemMessage((Player)sender, "Registrierung von " + args[0] + " konnte nicht gelöscht werden");
                        else
                            plugin.getLogger().info("Registrierung von " + args[0] + " konnte nicht gelöscht werden");
                    }
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                }

            } else if (args.length > 1) {
                if (sender instanceof Player)
                    plugin.Funcs.sendSystemMessage((Player)sender, "Zu viele Parameter:");
                else
                    plugin.getLogger().info("Zu viele Parameter:");
            } else {
                if (sender instanceof Player)
                    plugin.Funcs.sendSystemMessage((Player)sender, "Zu wenig Parameter:");
                else
                    plugin.getLogger().info("Zu wenig Parameter:");
            }
        } else {
            plugin.Funcs.sendSystemMessage((Player)sender, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.removereg");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
