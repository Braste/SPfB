package de.braste.SPfB.commands;


import de.braste.SPfB.SPfB;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class clearinventory implements CommandExecutor {
    private final SPfB plugin;

    public clearinventory(SPfB plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (plugin.Funcs.getIsLoggedIn(player) && player.hasPermission("SPfB.clearinventory")) {
                System.out.println(player.getName() + " used SPfB.clearinventory");
                boolean ret = false;
                int firstSlot = -1;
                int lastSlot = player.getInventory().getSize();

                if (args.length >= 1) {
                    if (args[0].toLowerCase().equals("main")) {
                        firstSlot = 9;
                    } else if (args[0].toLowerCase().equals("bar")) {
                        firstSlot = 0;
                        lastSlot = 9;
                    } else if (args[0].toLowerCase().equals("all")) {
                        firstSlot = 0;
                    } else {
                        plugin.Funcs.sendSystemMessage(player, "Parameter " + args[0] + " unbekannt:");
                    }

                    if (firstSlot != -1) {
                        for (int i = firstSlot; i < lastSlot; i++) {
                            player.getInventory().clear(i);
                        }
                        ret = true;
                    }
                } else {
                    plugin.Funcs.sendSystemMessage(player, "Zu wenig Parameter:");
                }

                return ret;
            } else
                plugin.Funcs.sendSystemMessage(player, "Du bist nicht eingeloggt oder hast nicht die erforderliche Berechtigung SPfB.clearinventory");
        }
        return true;
    }

    public SPfB getPlugin() {
        return plugin;
    }
}
