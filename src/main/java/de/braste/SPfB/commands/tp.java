package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class tp implements CommandExecutor {
    private final SPfB plugin;

    public tp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = null;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.tp")) {
                System.out.println(player.getName() + " used SPfB.tp");
                if (args.length == 1) {
                    UUID playerId = plugin.Funcs.GetUUID(args[0]);
                    if (playerId != null) {
                        try {
                            if (plugin.Funcs.getConfigNodeInt("debug") >= 1) {
                                 plugin.getLogger().info("Player: "+args[0]+", UUID: "+playerId.toString());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (MySqlPoolableException e) {
                            e.printStackTrace();
                        }
                        target = player.getServer().getPlayer(playerId);
                    }
                    if (target != null) {
                        player.teleport(target);
                        return true;
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Spieler " + args[0] + " wurde nicht gefunden!");
                    }
                }
            }
            else  {
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.tp");
            }
            return true;
        }
        if (args.length == 2) {
            Player source = null;
            Map<String, UUID> playerIds = plugin.Funcs.GetUUIDs(Arrays.asList(args));
            if (playerIds != null)
            {
                source = sender.getServer().getPlayer(playerIds.get(args[0]));
                target = sender.getServer().getPlayer(playerIds.get(args[1]));
            }

            if (target != null) {
                if (source != null) {
                    source.teleport(target);
                }
                else {
                    if (sender instanceof Player)
                        plugin.Funcs.sendSystemMessage((Player)sender, "Spieler " + args[1] + " wurde nicht gefunden!");
                    else
                        plugin.getLogger().info("Spieler " + args[1] + " wurde nicht gefunden!");
                }
            }
            else {
                if (sender instanceof Player)
                    plugin.Funcs.sendSystemMessage((Player)sender, "Spieler " + args[0] + " wurde nicht gefunden!");
                else
                    plugin.getLogger().info("Spieler " + args[0] + " wurde nicht gefunden!");
            }
        }
        else if (args.length > 2) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player)sender, "Zu viele Parameter:");
            else
                plugin.getLogger().info("Zu viele Parameter:");
        }
        else {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player)sender, "Zu wenig Parameter:");
            else
                plugin.getLogger().info("Zu wenig Parameter:");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
