package de.braste.SPfB.commands;

import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class listwarps implements CommandExecutor {
    private final SPfB plugin;

    public listwarps(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || (plugin.Funcs.getIsLoggedIn((Player)sender) && sender.hasPermission("SPfB.listwarps"))) {
            try {
                if (sender instanceof Player)
                    showWarpPoints(sender, plugin.Funcs.listWarpPoints(((Player)sender).getWorld()));
                else if (args.length == 1)
                    showWarpPoints(sender, plugin.Funcs.listWarpPoints(args[0]));
            } catch (SQLException | MySqlPoolableException e) {
                e.printStackTrace();
            }
        } else {
            plugin.Funcs.sendSystemMessage((Player)sender, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.listwarps");
        }
        return true;
    }

    private void showWarpPoints(CommandSender sender, List<String[]> warppoints) {
        for (String[] s : warppoints) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, String.format("%s: %s", s[1], s[0]));
            else
                plugin.getLogger().info(String.format("%s: %s", s[1], s[0]));
        }
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
