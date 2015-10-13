package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class broadcast implements CommandExecutor {
    private final SPfB plugin;

    public broadcast(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.broadcast")) {
                System.out.println(player.getName() + " used SPfB.broadcast");
                String message = "";

                for (String aArgs : args) {
                    message = message + " " + aArgs;
                }
                plugin.getServer().broadcastMessage(message);
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.broadcast");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
