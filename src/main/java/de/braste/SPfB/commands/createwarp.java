package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class createwarp implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public createwarp(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (funcs.canUseCommand(player, "SPfB.createwarp")) {
                System.out.println(player.getName() + " used SPfB.createwarp");
                if (funcs.isLoggedIn(player)) {
                    boolean ret = false;

                    if (args.length == 1) {
                        if (funcs.setWarp(player, args[0])) {
                            funcs.systemMessage(player, "Warppunkt " + args[0] + " erfolgreich gesetzt.");
                            ret = true;
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
