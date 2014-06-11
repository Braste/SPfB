package de.braste.SPfB.commands;


import com.evilmidget38.UUIDFetcher;
import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class removereg implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public removereg(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            boolean ret = false;

            if (args.length == 1) {
                Player target;
                try
                {
                    UUID playerId = UUIDFetcher.getUUIDOf(args[0]);
                    target = sender.getServer().getPlayer(playerId);
                }
                catch(Exception e)
                {
                    sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                    e.printStackTrace();
                    sender.getServer().getLogger().warning("Trying with player name");
                    target = sender.getServer().matchPlayer(args[0]).get(0);
                }
                if (target != null)
                {
                    funcs.removeReg(args[0]);
                    System.out.println("Registrierung von " + args[0] + " erfolgreiche gelöscht");
                    ret = true;
                }
            } else if (args.length > 1) {
                System.out.println("Zu viele Parameter:");
            } else {
                System.out.println("Zu wenig Parameter:");
            }
            return ret;
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.removereg")) {
                System.out.println(player.getName() + " used SPfB.removereg");
                if (funcs.isLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length == 1) {
                        Player target;
                        try
                        {
                            UUID playerId = UUIDFetcher.getUUIDOf(args[0]);
                            target = sender.getServer().getPlayer(playerId);
                        }
                        catch(Exception e)
                        {
                            sender.getServer().getLogger().warning("Exception while running UUIDFetcher");
                            e.printStackTrace();
                            sender.getServer().getLogger().warning("Trying with player name");
                            target = sender.getServer().matchPlayer(args[0]).get(0);
                        }
                        if (!funcs.isAdmin(target)) {
                            funcs.removeReg(args[0]);
                            funcs.systemMessage(player, "Registrierung von " + args[0] + " erfolgreiche gelöscht");
                            ret = true;
                        } else {
                            funcs.systemMessage(player, "Die Registrierung von Administratoren kann nicht gelöscht werden!");
                        }
                    } else if (args.length > 1) {
                        funcs.systemMessage(player, "Zu viele Parameter:");
                    } else {
                        funcs.systemMessage(player, "Zu wenig Parameter:");
                    }
                    return ret;
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
