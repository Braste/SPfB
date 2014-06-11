package de.braste.SPfB;

import de.braste.SPfB.commands.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class SPfB extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();
        BukkitScheduler schedule = getServer().getScheduler();

        pm.registerEvents(new SPfBBlockListener(this), this);
        pm.registerEvents(new SPfBPlayerListener(this), this);
        pm.registerEvents(new SPfBEntityListener(this), this);

        //HOME
        getCommand("home").setExecutor(new home(this));

        //SETHOME
        getCommand("sethome").setExecutor(new sethome(this));

        //SPAWN
        getCommand("spawn").setExecutor(new spawn(this));

        //SETSPAWN
        getCommand("setspawn").setExecutor(new setspawn(this));

        //LOGIN
        getCommand("login").setExecutor(new login(this));

        //REGISTER
        getCommand("register").setExecutor(new register(this));

        //SETGROUP
        getCommand("setgroup").setExecutor(new setgroup(this));

         //GETGROUPS
        getCommand("getgroups").setExecutor(new getgroups(this));

        //CHANGEPW
        getCommand("changepw").setExecutor(new changepw(this));

        //WARP
        getCommand("warp").setExecutor(new warp(this));

        //CREATEWARP
        getCommand("createwarp").setExecutor(new createwarp(this));

        //LISTWARP
        getCommand("listwarps").setExecutor(new listwarps(this));

        //DELETEWARP
        getCommand("deletewarp").setExecutor(new deletewarp(this));

        //REMOVEREG
        getCommand("removereg").setExecutor(new removereg(this));

        //CLEARINVENTORY
        getCommand("clearinventory").setExecutor(new clearinventory(this));

        //WP
        getCommand("wp").setExecutor(new wp(this));

        //SETWP
        getCommand("setwp").setExecutor(new setwp(this));

        //LISTWP
        getCommand("listwp").setExecutor(new listwp(this));

        //DELETEWP
        getCommand("deletewp").setExecutor(new deletewp(this));

        //TP
        getCommand("tp").setExecutor(new tp(this));

        //LIST
        getCommand("list").setExecutor(new list(this));

        //BROADCAST
        getCommand("broadcast").setExecutor(new broadcast(this));

        //RIFT
        getCommand("rift").setExecutor(new rift(this));

        //SETWEATHER
        getCommand("setweather").setExecutor(new setweather(this));

        //RELOADPLUGIN
        getCommand("reloadplugin").setExecutor(new reloadplugin(this));

        /*//MONSTER
        getCommand("monster").setExecutor(new monster(this));*/

        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled");
    }

    @Override
    public void onDisable() {
        BukkitScheduler schedule = getServer().getScheduler();
        schedule.cancelTasks(getServer().getPluginManager().getPlugin("SPfB"));

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " disabled");
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
