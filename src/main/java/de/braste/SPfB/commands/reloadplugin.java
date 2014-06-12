package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
//TODO
public class reloadplugin implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public reloadplugin(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            boolean ret = false;

            if (args.length == 1) {
                Plugin p = plugin.getServer().getPluginManager().getPlugin(args[0]);

                if (p != null && !p.getDescription().getName().equals(plugin.getDescription().getName())) {

                    if (p.isEnabled()) {
                        plugin.getServer().getPluginManager().disablePlugin(p);

                        while (p.isEnabled()) {

                        }
                        plugin.getServer().getPluginManager().enablePlugin(p);
                    }
                    while (!p.isEnabled()) {

                    }
                    System.out.println("Plugin " + args[0] + " erfolgreich geladen.");

                } else {
                    System.out.println("Plugin " + args[0] + " nicht vorhanden.");
                }
                ret = true;
            } else if (args.length > 1) {
                System.out.println("Zu viele Parameter:");
            } else {
                System.out.println("Zu wenig Parameter:");
            }
            return ret;
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.reloadplugin")) {
                System.out.println(player.getName() + " used SPfB.reloadplugin");
                if (plugin.Funcs.getIsLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length == 1) {
                        Plugin p = player.getServer().getPluginManager().getPlugin(args[0]);

                        if (p != null && !p.getDescription().getName().equals(plugin.getDescription().getName())) {

                            if (p.isEnabled()) {
                                player.getServer().getPluginManager().disablePlugin(p);

                                while (p.isEnabled()) {

                                }
                                player.getServer().getPluginManager().enablePlugin(p);
                            }
                            while (!p.isEnabled()) {

                            }
                            plugin.Funcs.sendSystemMessage(player, "Plugin " + args[0] + " erfolgreich geladen.");

                        } else {
                            plugin.Funcs.sendSystemMessage(player, "Plugin " + args[0] + " nicht vorhanden.");
                        }
                        ret = true;
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
