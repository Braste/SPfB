package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//TODO
public class setweather implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public setweather(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.setweather")) {
                System.out.println(player.getName() + " used SPfB.setweather");
                if (plugin.Funcs.getIsLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length > 1) {
                        plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                    } else if (args.length == 1) {

                        try {
                            int duration = Integer.parseInt(args[0]);
                            if (duration == 0) {
                                funcs.setWeather(player, 0, false);
                            } else {
                                funcs.setWeather(player, duration, true);
                            }
                            funcs.broadcastMessage(player, player.getName() + " hat das Wetter geändert.");
                            ret = true;
                        } catch (NumberFormatException e) {
                            plugin.Funcs.sendSystemMessage(player, args[0] + " ist keine gültige Zahl.");
                        }
                    } else {
                        funcs.setWeather(player, 0, false);
                        funcs.broadcastMessage(player, player.getName() + " hat das Wetter geändert.");
                        ret = true;
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
