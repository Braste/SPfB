package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class changepw implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public changepw(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (funcs.canUseCommand(player, "SPfB.changepw")) {
                System.out.println(player.getName() + " used SPfB.changepw");
                boolean ret = false;

                if (funcs.isRegistered(player)) {
                    if (funcs.isLoggedIn(player)) {
                        if (args.length == 3) {
                            if (funcs.changePassword(player, args[0], args[1], args[2])) {
                                funcs.systemMessage(player, "Passwort erfolgreich geändert");
                                ret = true;
                            } else {
                                funcs.systemMessage(player, "Änderung nicht erfolgreich!:");
                            }
                        } else if (args.length > 3) {
                            funcs.systemMessage(player, "Zu viele Parameter:");
                        } else {
                            funcs.systemMessage(player, "Zu wenig Parameter:");
                        }
                    } else {
                        funcs.systemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
                    }
                } else {
                    funcs.systemMessage(player, "Du bist nicht registriert. Bitte registriere dich mit '/register <password>'");
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
