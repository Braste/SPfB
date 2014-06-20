package de.braste.SPfB.commands;

import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class home implements CommandExecutor {
    private final SPfB plugin;

    public home(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.home")) {
                plugin.getLogger().info(player.getName() + " used SPfB.home");
                Location loc;
                try {
                    loc = plugin.Funcs.getHomeLocation(player);
                    if (loc != null) player.teleport(loc);
                    else
                        plugin.Funcs.sendSystemMessage(player, "Kein Home-Punkt auf Welt" + player.getWorld().getName() + " gefunden.");
                } catch (SQLException | MySqlPoolableException e) {
                    e.printStackTrace();
                    return false;
                }

            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.home");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
