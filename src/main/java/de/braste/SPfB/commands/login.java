package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class login implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public login(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (funcs.canUseCommand(player, "SPfB.login")) {
                System.out.println(player.getName() + " used SPfB.login");
                boolean ret = false;

                if (funcs.getConfigNode("debug") >= 1) {
                    String sOut =  player.getName();
                    for (String s : args) {
                        sOut += " - " + s;
                    }
                    System.out.println(sOut);
                }

                if (!funcs.isLoggedIn(player)) {
                    if (funcs.isRegistered(player)) {
                        if (args.length == 1) {
                            if (funcs.login(player, args[0])) {
                                funcs.systemMessage(player, "Erfolgreich eingeloggt, willkommen " + player.getName() + "!");
                                ret = true;
                            } else {
                                funcs.systemMessage(player, "Login gescheitert!");
                            }
                        } else if (args.length > 1) {
                            funcs.systemMessage(player, "Zu viele Parameter:");
                        } else {
                            funcs.systemMessage(player, "Zu wenig Parameter:");
                        }
                    } else {
                        funcs.systemMessage(player, "Du bist nicht registriert. Bitte registriere dich mit '/register <password> <password>'");
                        ret = true;
                    }
                } else {
                    funcs.systemMessage(player, "Du bist schon eingeloggt.");
                    ret = true;
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
