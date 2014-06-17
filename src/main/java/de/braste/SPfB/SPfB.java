package de.braste.SPfB;

import de.braste.SPfB.commands.*;
import de.braste.SPfB.functions.CommandFilter;
import de.braste.SPfB.functions.Functions;
import de.braste.SPfB.functions.MySqlPoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SPfB extends JavaPlugin {
    public Functions Funcs;
    private String host;
    private String port;
    private String db;
    private String user;
    private String pw;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();
        ConfigurationSection database = getConfig().getConfigurationSection("mysql");
        if (database != null)
        {
            host = database.getString("host");
            port = database.getString("port");
            db = database.getString("database");
            user = database.getString("user");
            pw = database.getString("password");
        }
        else
        {
            getLogger().warning(String.format("No database configuration found. %s won't work!", pdfFile.getName()));
        }
        // Save the config
        getConfig().options().copyDefaults(true);
        saveConfig();
        Funcs = new Functions(initMySqlConnectionPool(), this);
        pm.registerEvents(new SPfBBlockListener(this), this);
        pm.registerEvents(new SPfBPlayerListener(this), this);
        pm.registerEvents(new SPfBEntityListener(this), this);
        getLogger().setFilter(new CommandFilter());

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
        //getCommand("setgroup").setExecutor(new setgroup(this));

         //GETGROUPS
        //getCommand("getgroups").setExecutor(new getgroups(this));

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

        //RELOADPLUGIN
        //getCommand("reloadplugin").setExecutor(new reloadplugin(this));

        /*//MONSTER
        getCommand("monster").setExecutor(new monster(this));*/

        getLogger().info(String.format("%s version %s enabled", pdfFile.getName(), pdfFile.getVersion()));
    }

    @Override
    public void onDisable() {
        BukkitScheduler schedule = getServer().getScheduler();
        schedule.cancelTasks(getServer().getPluginManager().getPlugin("SPfB"));
        if (Funcs != null)
            Funcs.CloseConnections();
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info(String.format("%s version %s disabled", pdfFile.getName(), pdfFile.getVersion()));
    }

    private ObjectPool initMySqlConnectionPool() {
        PoolableObjectFactory mySqlPoolableObjectFactory = new MySqlPoolableObjectFactory(host,
                Integer.parseInt(port), db, user, pw);
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 10;
        config.testOnBorrow = true;
        config.testWhileIdle = true;
        config.timeBetweenEvictionRunsMillis = 10000;
        config.minEvictableIdleTimeMillis = 60000;

        GenericObjectPoolFactory genericObjectPoolFactory = new GenericObjectPoolFactory(mySqlPoolableObjectFactory, config);
        return genericObjectPoolFactory.createPool();
    }
}
