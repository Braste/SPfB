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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.String.format;

public class SPfB extends JavaPlugin {
    public static Permission Perms;
    public static Chat Chat;
    public Functions Funcs;
    //public final List<Block> FurnaceBlocks = Collections.synchronizedList(new ArrayList<>());
    public final Map<Block, BukkitTask> FurnaceBlocks = new HashMap<>();
    public static final Map<String, Gate> Portals = Collections.synchronizedMap(new HashMap<>());
    public static Logger logger;
    private String host;
    private String port;
    private String db;
    private String user;
    private String pw;
    private ConfigurationSection database;
    private ConfigurationSection furnaces;
    private ConfigurationSection gates;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();
        database = getConfig().getConfigurationSection("mysql");
        furnaces = getConfig().getConfigurationSection("Furnace");
        gates = getConfig().getConfigurationSection("Gates");
        logger = getLogger();

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
            loadFurnaces();
            loadGates();
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

        //SETTARGETPORTAL
        getCommand("settargetportal").setExecutor(new settargetportal(this));

        getLogger().info(format("%s erfolgreich geladen!", pdfFile.getName()));
    }

    @Override
    public void onDisable() {
        BukkitScheduler schedule = getServer().getScheduler();
        schedule.cancelTasks(getServer().getPluginManager().getPlugin("SPfB"));
        if (Funcs != null)
            Funcs.CloseConnections();
        PluginDescriptionFile pdfFile = this.getDescription();
        saveFurnaces();
        saveGates();
        saveConfig();
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

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            Perms = permissionProvider.getProvider();
        }
        return (Perms != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            Chat = chatProvider.getProvider();
        }

        return (Chat != null);
    }

    private void saveFurnaces() {
        try {
            Map<String, List<double[]>> map = new HashMap<>();

            for (Block FurnaceBlock : FurnaceBlocks.keySet()) {

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
            if (!getConfig().contains("Furnace"))
                getConfig().createSection("Furnace");
            getConfig().set("Furnace", map);
        } catch(Exception ignored) {

        }
    }

    public void saveGates() {
        try {
            Map<String, Map<String, Object>> map = new HashMap<>();

            synchronized (Portals) {
                for (Gate g : Portals.values()) {
                    Map<String, Object> mapList =  new HashMap<>();

                    mapList.put("toId", g.getToId());
                    mapList.put("facing", g.getFacing().name());
                    mapList.put("portalMaterial", g.getPortalMaterial().name());
                    mapList.put("world", g.getWorld().getName());

                    List<Double> locList = new ArrayList<>();
                    Location loc = g.getStartBlockLocation();
                    locList.add(loc.getX());
                    locList.add(loc.getY());
                    locList.add(loc.getZ());

                    mapList.put("startBlockLocation", locList);

                    map.put(g.getId(), mapList);
                }
            }
            if (!getConfig().contains("Gates"))
                getConfig().createSection("Gates");
            getConfig().set("Gates", map);
        } catch(Exception ignored) {

        }
    }

    private void loadFurnaces() {
        try {
            if (furnaces == null) {
                File dataFolder = getDataFolder();
                File data = new File(dataFolder.getAbsolutePath() + "/FurnaceBlocks.dat");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(data);
                furnaces = config.getConfigurationSection("Furnace");
            }
            Set<String> keys = furnaces.getKeys(false);

            for (String key: keys) {
                List<List<Double>> map = (List<List<Double>>) furnaces.get(key);
                for (List<Double> d: map) {
                    Block b = getServer().getWorld(key).getBlockAt(d.get(0).intValue(), d.get(1).intValue(), d.get(2).intValue());
                    if (b != null) {
                        synchronized (FurnaceBlocks) {
                            FurnaceBlocks.put(b, null);
                        }
                    }
                }
            }
            synchronized (FurnaceBlocks) {
                getLogger().info(format("%s Öfen geladen.", FurnaceBlocks.size()));
            }
        } catch(Exception e) {
            getLogger().warning("Fehler beim Laden der Öfen: ");
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskTimer(this, this::UpdateFurnaces, 2400L, 2400L);
    }

    private void loadGates() {
        try {
            Set<String> keys = gates.getKeys(false);

            for (String key : keys) {
                try {
                    ConfigurationSection map = (ConfigurationSection) gates.get(key);
                    BlockFace facing = BlockFace.valueOf(map.getString("facing"));
                    Material portalMaterial = Material.valueOf(map.getString("portalMaterial"));
                    List<Double> startBlockLocation = (List<Double>) map.get("startBlockLocation");
                    World world = getServer().getWorld(map.getString("world"));
                    Block startBlock = world.getBlockAt(startBlockLocation.get(0).intValue(), startBlockLocation.get(1).intValue(), startBlockLocation.get(2).intValue());

                    Gate g = new Gate(key, portalMaterial, facing, startBlock, true);
                    if (g.getIsValid()) {
                        synchronized (Portals) {
                            Portals.put(key, g);
                        }
                    }
                } catch(Exception e) {
                    getLogger().info(format("Portal %s konnte nicht geladen werden.", key));
                }
            }

            for (String key : keys) {
                ConfigurationSection map = (ConfigurationSection) gates.get(key);
                String toId = map.getString("toId");
                synchronized (Portals) {
                    if (Portals.containsKey(key) && Portals.containsKey(toId)) {
                        Portals.get(key).setTo(Portals.get(toId));
                    }
                }
            }
            synchronized (Portals) {
                getLogger().info(format("%s Portal(e) geladen.", Portals.size()));
            }

        } catch(Exception e) {
            getLogger().warning("Fehler beim Laden der Portale: ");
            e.printStackTrace();
        }
    }

    private void UpdateFurnaces() {
        try {
            if ((int) Funcs.getConfigNode("debug", "int") > 1) {
                getLogger().info("Aktualisiere Öfen");
            }
        } catch (SQLException | MySqlPoolableException e) {
            e.printStackTrace();
        }
        List<World> worlds = Bukkit.getWorlds();

        for (World w : worlds) {
            Collection<Entity> furnaces = w.getEntitiesByClasses(Furnace.class);
            try {
                if ((int) Funcs.getConfigNode("debug", "int") > 1) {
                    getLogger().info("Anzahl Öfen: " + furnaces.size());
                }
            } catch (SQLException | MySqlPoolableException e) {
                e.printStackTrace();
            }
            Set<Block> blocks;

            synchronized (FurnaceBlocks) {
                blocks = FurnaceBlocks.keySet();
            }
            for (Block b : blocks) {

                if (furnaces.contains(b)) {
                    Block blockUnder = b.getRelative(BlockFace.DOWN);

                    if (blockUnder.getType() == Material.LAVA) {
                        ((Furnace) b.getState()).setBurnTime((short)10000);
                    }
                }
            }
        }
    }
}
