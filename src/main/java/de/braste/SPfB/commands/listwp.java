package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class listwp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public listwp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (funcs.canUseCommand(player, "SPfB.listwp")) {
                System.out.println(player.getName() + " used SPfB.listwp");
                if (funcs.isLoggedIn(player)) {
                    boolean ret = false;
                    if (args.length == 1) {
                        funcs.listWaypoints(player, args[0]);
                        ret = true;
                    } else if (args.length < 1) {
                        funcs.listWaypoints(player, null);
                        ret = true;
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
