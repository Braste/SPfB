package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class list implements CommandExecutor {
    private final SPfB plugin;

    public list(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String players = "", k = "";
        Player[] playerList = plugin.getServer().getOnlinePlayers();

        for (Player p : playerList) {
            players += String.format("%s%s", k, p.getName());
            k = ", ";
        }

        if (!(sender instanceof Player)) {
            plugin.getLogger().info(players);
        } else {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.list")) {
                plugin.getLogger().info(player.getName() + " used SPfB.list");
                plugin.Funcs.sendSystemMessage(player, players);
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
