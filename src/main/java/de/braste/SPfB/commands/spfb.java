package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spfb implements CommandExecutor {
    private final SPfB plugin;

    public spfb(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (sender instanceof Player)
                    plugin.Funcs.sendSystemMessage((Player) sender, "Lade Konfig neu.");
                else
                    plugin.getLogger().info("Lade Konfig neu.");
                plugin.reloadConfig();
            }
        } else if (args.length == 0) {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, "Zu wenig Parameter:");
            else
                plugin.getLogger().info("Zu wenig Parameter:");
            return false;
        } else {
            if (sender instanceof Player)
                plugin.Funcs.sendSystemMessage((Player) sender, "Zu viele Parameter:");
            else
                plugin.getLogger().info("Zu viele Parameter:");
            return false;
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
