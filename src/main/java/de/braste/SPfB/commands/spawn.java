package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spawn implements CommandExecutor {
    private final SPfB plugin;

    public spawn(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (player.hasPermission("SPfB.spawn")) {
                plugin.getLogger().info(player.getName() + " used SPfB.spawn");
                World world = player.getWorld();
                Location loc = world.getSpawnLocation().add(0.0, 2.0, 0.0);
                if (loc != null) player.teleport(loc);
            }
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
