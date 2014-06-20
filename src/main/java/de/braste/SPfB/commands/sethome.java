package de.braste.SPfB.commands;

import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class sethome implements CommandExecutor {

    private final SPfB plugin;

    public sethome(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.sethome")) {
                plugin.getLogger().info(player.getName() + " used SPfB.sethome");
                try {
                    if (plugin.Funcs.setHomeLocation(player)) {
                        plugin.Funcs.sendSystemMessage(player, "Home-Punkt gesetzt.");
                    } else plugin.Funcs.sendSystemMessage(player, "Home-Punkt konnte nicht gesetzt werden.");
                } catch (MySqlPoolableException | SQLException e) {
                    e.printStackTrace();
                }
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.sethome");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
