package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//TODO
public class deletewarp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public deletewarp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.deletewarp")) {
                System.out.println(player.getName() + " used SPfB.deletewarp");
                if (funcs.isLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length == 1) {
                        if (funcs.deleteWarp(player, args[0])) {
                            plugin.Funcs.sendSystemMessage(player, "Warppunkt " + args[0] + " erfolgreich gelöscht.");
                            ret = true;
                        }
                    } else if (args.length > 1) {
                        plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                    }
                    return ret;
                }
                else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
