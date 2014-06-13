package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class wp implements CommandExecutor {
    private final SPfB plugin;

    public wp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.wp")) {
                System.out.println(player.getName() + " used SPfB.wp");
                if (args.length == 2 && player.hasPermission("SPfB.allwp")) {
                    try {
                        Location loc = plugin.Funcs.getWaypoint(args[0], args[1], player.getWorld());
                        if (loc != null) player.teleport(loc);
                        else plugin.Funcs.sendSystemMessage(player, "Wegpunkt "+args[1]+" von Spieler "+args[0]+" auf Welt "+player.getWorld().getName()+" nicht gefunden.");
                    } catch (MySqlPoolableException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else if (args.length == 1) {
                    try {
                        Location loc = plugin.Funcs.getWaypoint(player, args[0]);
                        if (loc != null) player.teleport(loc);
                        else plugin.Funcs.sendSystemMessage(player, "Wegpunkt "+args[1]+" auf Welt "+player.getWorld().getName()+" nicht gefunden.");
                    } catch (MySqlPoolableException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else if (args.length > 2) {
                    plugin.Funcs.sendSystemMessage(player, "Zu viele Parameter:");
                }
                else {
                    plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                }
                return false;
            }
            else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.wp");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
