package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class wp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public wp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.wp")) {
                System.out.println(player.getName() + " used SPfB.wp");
                if (funcs.isLoggedIn(player)) {

                    if (args.length == 2 && player.hasPermission("SPfB.allwp")) {
                        funcs.getPlayerWaypoint(player, args[0], args[1]);
                        return true;
                    }
                    else if (args.length == 1) {
                        funcs.getOwnWaypoint(player, args[0]);
                        return true;
                    } else if (args.length > 2) {
                        funcs.systemMessage(player, "Zu viele Parameter:");
                    } else {
                        funcs.systemMessage(player, "Zu wenig Parameter:");
                    }
                    return false;
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
