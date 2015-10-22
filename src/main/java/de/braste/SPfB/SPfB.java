package de.braste.SPfB;

import de.braste.SPfB.commands.*;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import de.braste.SPfB.functions.CommandFilter;
import de.braste.SPfB.functions.Functions;
import de.braste.SPfB.functions.MySqlPoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class SPfB extends JavaPlugin {
    public Functions Funcs;
    public List<Block> FurnaceBlocks;
    private String host;
    private String port;
    private String db;
    private String user;
    private String pw;
    private YamlConfiguration config;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();
        ConfigurationSection database = getConfig().getConfigurationSection("mysql");
        if (database != null) {
            host = database.getString("host");
            port = database.getString("port");
            db = database.getString("database");
            user = database.getString("user");
            pw = database.getString("password");

            if (host == null || port == null || db == null || user == null)
                getLogger().warning(String.format("Database configuration incomplete. %s won't work!", pdfFile.getName()));
        } else {
            getLogger().warning(String.format("No database configuration found. %s won't work!", pdfFile.getName()));
        }
        // Save the config
        getConfig().options().copyDefaults(true);
        saveConfig();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Funcs = new Functions(initMySqlConnectionPool(), this);
            FurnaceBlocks = Collections.synchronizedList(new ArrayList<Block>());
            try {
                File datafolder = getDataFolder();
                File data = new File(datafolder.getAbsolutePath().toString() + "/FurnaceBlocks.dat");
                config = YamlConfiguration.loadConfiguration(data);

                ConfigurationSection furnaces = config.getConfigurationSection("Furnace");
                Set<String> keys = furnaces.getKeys(true);

                for (String key: keys) {
                   List<double[]> map = (List<double[]>) furnaces.get(key);
                    for (double[] d: map) {
                        Block b = getServer().getWorld(key).getBlockAt((int)d[0], (int)d[1], (int)d[2]);
                        if (b != null) {
                            synchronized (FurnaceBlocks) {
                                FurnaceBlocks.add(b);
                            }
                        }
                    }
                }
            } catch(Exception e) {
                getLogger().warning("Can not load Furnaces from Config file: " + e);
            }

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    UpdateFurnace();
                }
            }, 2400L, 2400L);
        } catch (Exception e) {
            getLogger().warning(String.format("%s version %s can't be enabled: ", pdfFile.getName(), pdfFile.getVersion()));
            e.printStackTrace();
            return;
        }
        pm.registerEvents(new SPfBListener(this), this);



        getServer().getLogger().setFilter(new CommandFilter());

        //HOME
        getCommand("home").setExecutor(new home(this));

        //SETHOME
        getCommand("sethome").setExecutor(new sethome(this));

        //SPAWN
        getCommand("spawn").setExecutor(new spawn(this));

        //SETSPAWN
        getCommand("setspawn").setExecutor(new setspawn(this));

        //SETGROUP
        //getCommand("setgroup").setExecutor(new setgroup(this));

        //GETGROUPS
        //getCommand("getgroups").setExecutor(new getgroups(this));

        //RIFT
        //getCommand("rift").setExecutor(new rift(this));

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

        getLogger().info(String.format("%s version %s enabled", pdfFile.getName(), pdfFile.getVersion()));
    }

    @Override
    public void onDisable() {
        BukkitScheduler schedule = getServer().getScheduler();
        schedule.cancelTasks(getServer().getPluginManager().getPlugin("SPfB"));
        if (Funcs != null)
            Funcs.CloseConnections();
        PluginDescriptionFile pdfFile = this.getDescription();
        try {
            File datafolder = getDataFolder();
            Map<String, List<double[]>> map = new HashMap<>();

            for (int i = 0; i < FurnaceBlocks.size(); i++) {

                double[] coords = new double[3];
                Location loc = FurnaceBlocks.get(i).getLocation();
                coords[0] = loc.getX();
                coords[1] = loc.getY();
                coords[2] = loc.getZ();
                if (map.containsKey(loc.getWorld().getName())) {
                    map.get(loc.getWorld().getName()).add(coords);
                } else {
                    List<double[]> list = new ArrayList();
                    list.add(coords);
                    map.put(loc.getWorld().getName(), list);
                }
            }
            config.set("Furnace", map);
            config.save(datafolder.getAbsolutePath().toString() + "/FurnaceBlocks.dat");
        } catch(Exception e) {

        }
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

    private void UpdateFurnace() {
        try {
            if ((int) Funcs.getConfigNode("debug", "int") > 1) {
                getLogger().info("Updating furnaces");
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
        Block[] blocks;
        synchronized (FurnaceBlocks) {
            blocks  = FurnaceBlocks.toArray(new Block[FurnaceBlocks.size()]);
        }
        for (Block b: blocks) {
            Block blockUnder = b.getRelative(BlockFace.DOWN);
            if (blockUnder.getType() == Material.LAVA || blockUnder.getType() == Material.STATIONARY_LAVA) {
                ((Furnace) b.getState()).setBurnTime((short)10000);
                continue;
            }
            synchronized (FurnaceBlocks) {
                FurnaceBlocks.remove(b);
            }
        }
    }
}
