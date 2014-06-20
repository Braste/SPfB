package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

//TODO
public class rift implements CommandExecutor {
    private final SPfB plugin;

    public rift(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (!(sender instanceof Player)) {
            if (args.length == 1) {
                try {
                    int time = Integer.parseInt(args[0]);
                    funcs.broadcastMessage(plugin.getServer(), "Die Zeit wurde auf " + args[0] + " festgesetzt.");
                    return funcs.riftTime(plugin.getServer(), time);
                } catch (NumberFormatException e) {
                    System.out.println(args[0] + " ist keine gültige Zeit.");
                }
            } else if (args.length > 1) {
                System.out.println("Zu viele Parameter:");
            } else {
                funcs.broadcastMessage(plugin.getServer(), "Die Zeitschleife wurde beendet.");
                return funcs.riftTime(plugin.getServer(), -1);
            }
            return false;

        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.rift")) {
                System.out.println(player.getName() + " used SPfB.rift");
                if (plugin.Funcs.getIsLoggedIn(player)) {
                    if (args.length == 1) {
                        try {
                            int time = Integer.parseInt(args[0]);
                            funcs.broadcastMessage(player, player.getName() + " hat die Zeit auf " + args[0] + " festgesetzt.");
                            return funcs.riftTime(player, time);
                        } catch (NumberFormatException e) {
                            plugin.Funcs.sendSystemMessage(player, args[0] + " ist keine gültige Zeit.");
                        }
                    } else if (args.length > 1) {
                        plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                    } else {
                        funcs.broadcastMessage(player, player.getName() + " hat die Zeitschleife beendet.");
                        return funcs.riftTime(player, -1);
                    }
                    return false;
                }
                else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
            }
        }*/
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
