package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//TODO
public class monster implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public monster(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (!(sender instanceof Player)) {
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.monster")) {
                System.out.println(player.getName() + " used SPfB.monster");
                if (plugin.Funcs.getIsLoggedIn(player)) {

                    funcs.spawnMonster(player, );
                }
                else plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt. Bitte logge dich mit '/login <password>' ein");
            }
        }*/
        return true;

    }

    public SPfB getPlugin() {
        return plugin;
    }
}
