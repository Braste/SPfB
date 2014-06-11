package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import de.braste.SPfBFunctions.Funcs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class list implements CommandExecutor {
    private final SPfB plugin;
    private final Funcs funcs = new Funcs();

    public list(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            funcs.listPlayers(sender);
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.list")) {
                System.out.println(player.getName() + " used SPfB.list");
                funcs.listPlayers(player);
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
