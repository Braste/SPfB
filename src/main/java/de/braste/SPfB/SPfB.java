package de.braste.SPfB;

import de.braste.SPfB.commands.*;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import de.braste.SPfB.functions.Functions;
import de.braste.SPfB.functions.MySqlPoolableObjectFactory;
import de.braste.SPfB.object.Gate;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import static java.lang.String.format;

public class SPfB extends JavaPlugin {
    public static Permission Perms;
    public static Chat Chat;
    public Functions Funcs;
    public final List<Block> FurnaceBlocks = Collections.synchronizedList(new ArrayList<>());
    public static final List<Gate> Portals = Collections.synchronizedList(new ArrayList<>());
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

            if (host == null || port == null || db == null || user == null) {
                getLogger().warning(format("Datenbankkonfiguration unvollständig. %s wird nicht laufen!", pdfFile.getName()));
            }
        } else {
            getLogger().warning(format("Keine Datenbankkonfiguration gefunden. %s wird nicht laufen!", pdfFile.getName()));
        }
        // Save the config
        getConfig().options().copyDefaults(true);
        saveConfig();
        try {
            if (!setupPermissions() || !setupChat())
                throw new Exception();
            Funcs = new Functions(initMySqlConnectionPool(), this);
            try {
                File datafolder = getDataFolder();
                File data = new File(datafolder.getAbsolutePath() + "/FurnaceBlocks.dat");
                config = YamlConfiguration.loadConfiguration(data);

                ConfigurationSection furnaces = config.getConfigurationSection("Furnace");
                Set<String> keys = furnaces.getKeys(true);

                for (String key: keys) {
                   List<List<Double>> map = (List<List<Double>>) furnaces.get(key);
                    for (List<Double> d: map) {
                        Block b = getServer().getWorld(key).getBlockAt(d.get(0).intValue(), d.get(1).intValue(), d.get(2).intValue());
                        if (b != null) {
                            synchronized (FurnaceBlocks) {
                                FurnaceBlocks.add(b);
                            }
                        }
                    }
                }
                synchronized (FurnaceBlocks) {
                    getLogger().info(format("%s Öfen geladen.", FurnaceBlocks.size()));
                }
            } catch(Exception e) {
                getLogger().warning("Fehler beim Laden der Öfen aus YAML: ");
                e.printStackTrace();
            }


            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::UpdateFurnace, 2400L, 2400L);
        } catch (Exception e) {
            getLogger().warning(format("%s kann nicht geladen werden: ", pdfFile.getName()));

            return;
        }
        pm.registerEvents(new SPfBListener(this), this);

        //HOME
        getCommand("home").setExecutor(new home(this));

        //SETHOME
        getCommand("sethome").setExecutor(new sethome(this));

        //SPAWN
        getCommand("spawn").setExecutor(new spawn(this));

        //SETSPAWN
        getCommand("setspawn").setExecutor(new setspawn(this));

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

        getLogger().info(format("%s erfolgreich geladen!", pdfFile.getName()));
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

            for (Block FurnaceBlock : FurnaceBlocks) {

                double[] coords = new double[3];
                Location loc = FurnaceBlock.getLocation();
                coords[0] = loc.getX();
                coords[1] = loc.getY();
                coords[2] = loc.getZ();
                if (map.containsKey(loc.getWorld().getName())) {
                    map.get(loc.getWorld().getName()).add(coords);
                } else {
                    List<double[]> list = new ArrayList<>();
                    list.add(coords);
                    map.put(loc.getWorld().getName(), list);
                }
            }
            config.set("Furnace", map);
            config.save(datafolder.getAbsolutePath() + "/FurnaceBlocks.dat");
        } catch(Exception ignored) {

        }
        getLogger().info(format("%s deaktiviert.", pdfFile.getName()));
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

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            Perms = permissionProvider.getProvider();
        }
        return (Perms != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            Chat = chatProvider.getProvider();
        }

        return (Chat != null);
    }

    private void UpdateFurnace() {
        try {
            if ((int) Funcs.getConfigNode("debug", "int") > 1) {
                getLogger().info("Aktualisiere Öfen");
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
