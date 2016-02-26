package de.braste.SPfB.commands;

import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class testSaveGates implements CommandExecutor {
    private final SPfB plugin;

    public testSaveGates(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.saveGates();
        plugin.saveConfig();
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
