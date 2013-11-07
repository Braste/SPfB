package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public tp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (funcs.canUseCommand(player, "SPfB.tp")) {
                System.out.println(player.getName() + " used SPfB.tp");
                if (funcs.isLoggedIn(player)) {
                    boolean ret = false;
                    Player target;

                    if (args.length == 1) {
                        if ((target = player.getServer().getPlayer(args[0])) != null) {
                            player.teleport(target);
                            ret = true;
                        } else {
                            funcs.systemMessage(player, "Spieler " + args[0] + " wurde nicht gefunden!");
                        }
                    } else if (args.length == 2) {
                        Player source;
                        if ((target = player.getServer().getPlayer(args[1])) != null) {
                            if ((source = player.getServer().getPlayer(args[0])) != null) {
                                source.teleport(target);
                                ret = true;
                            } else {
                                funcs.systemMessage(player, "Spieler " + args[1] + " wurde nicht gefunden!");
                            }
                        } else {
                            funcs.systemMessage(player, "Spieler " + args[0] + " wurde nicht gefunden!");
                        }
                    } else if (args.length > 2) {
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
