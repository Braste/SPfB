package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//TODO
public class register implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public register(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.register")) {
                System.out.println(player.getName() + " used SPfB.register");
                boolean ret = false;

                if (!funcs.isRegistered(player)) {
                    if (funcs.isInGroup(player, "user")) {
                        if (args.length == 2) {
                            if (funcs.register(player, args[0], args[1])) {
                                plugin.Funcs.sendSystemMessage(player, "Erfolgreich registriert, willkommen " + player.getName() + "!");
                                ret = true;
                            } else {
                                plugin.Funcs.sendSystemMessage(player, "Registrierung gescheitert!");
                            }
                        } else if (args.length > 2) {
                            plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                        }
                    }  else {
                        plugin.Funcs.sendSystemMessage(player, "Du bist nicht berechtigt, dich zu registrieren. Wende dich bitte an einen Administrator.");
                        ret = true;
                    }
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Du bist schon registriert. Bitte logge dich mit '/login <password>' ein");
                }

                return ret;
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
