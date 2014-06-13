package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setspawn implements CommandExecutor {
    private final SPfB plugin;

    public setspawn(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.setspawn")) {
                System.out.println(player.getName() + " used SPfB.setspawn");
                    if (player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())) {
                        player.getServer().broadcastMessage(player.getName() + " hat erfolgreich den Spawnpunkt gesetzt.");
                    }
            }
            else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.setspawn");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
