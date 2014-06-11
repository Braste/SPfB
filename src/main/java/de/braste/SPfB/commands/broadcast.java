package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class broadcast implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public broadcast(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.broadcast")) {
                System.out.println(player.getName() + " used SPfB.broadcast");
                if (funcs.isLoggedIn(player)) {
                    String message = "";

                    for (String aArgs : args) {
                        message = message + " " + aArgs;
                    }
                    funcs.broadcastMessage(player, message);
                }
                else funcs.systemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
