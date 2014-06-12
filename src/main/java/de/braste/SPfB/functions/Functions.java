package de.braste.SPfB.functions;

import com.evilmidget38.UUIDFetcher;
import de.braste.SPfB.SPfB;
import de.braste.SPfB.exceptions.MySqlPoolableException;
import org.apache.commons.pool.ObjectPool;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Functions {
    private final ObjectPool _connPool;
    private final SPfB _plugin;

    public Functions(ObjectPool connPool, final SPfB instance)
    {
        _connPool = connPool;
        _plugin = instance;
    }

    public void CloseConnections()
    {
        try {
            _connPool.close();
            _connPool.clear();
        } catch (Exception e) {

        }
    }

    public Location getHomeLocation(Player player) throws MySqlPoolableException, SQLException {
        Location loc = null;
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        UUID playerId = player.getUniqueId();
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT x, y, z, rotX FROM homes WHERE name = '" + playerId.toString() + "' AND world = '" + player.getWorld().getName() + "'");
            if(!res.next()) {
                updateToUUID(player, "homes", "name");
                res = st.executeQuery("SELECT x, y, z, rotX FROM homes WHERE name = '" + playerId.toString() + "' AND world = '" + player.getWorld().getName() + "'");
                while (res.next()) {
                    loc = new Location(player.getWorld(), res.getDouble("x"), res.getDouble("y"), res.getDouble("z"), res.getFloat("rotX"), 0);
                }
            }
            else {
                loc = new Location(player.getWorld(), res.getDouble("x"), res.getDouble("y"), res.getDouble("z"), res.getFloat("rotX"), 0);
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return loc;
    }

    public String getConfigNode(String node) throws SQLException, MySqlPoolableException {
        String value = null;
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT value FROM config WHERE node = '" + node + "'");
            while (res.next()) {
                value = res.getString("value");
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return value;
    }

    public int getConfigNodeInt(String node) throws SQLException, MySqlPoolableException {
        int value = 0;
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT value FROM config WHERE node = '" + node + "'");
            while (res.next()) {
                value = res.getInt("value");
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return value;
    }

    public boolean getIsRegister(Player player) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        UUID playerId = player.getUniqueId();
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT count(*) as count FROM reg WHERE name = '" + playerId.toString() + "'");
            while (res.next()) {
                if (res.getInt("count") == 0)
                {
                    updateToUUID(player, "reg", "name");
                    res = st.executeQuery("SELECT count(*) as count FROM reg WHERE name = '" + playerId.toString() + "'");
                    while (res.next()) {
                        return (res.getInt("count") > 0);
                    }
                }
                return (res.getInt("count") > 0);
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return false;
    }

    public boolean login(Player player, String password)
    {
        password = getSHA(password);
        try {
            return password.equals(getPassword(player)) && setSession(player);
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (MySqlPoolableException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout(Player player) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        UUID playerId = player.getUniqueId();
        try {
            conn = (Connection) _connPool.borrowObject();
            st = conn.createStatement();
            if (st.executeUpdate("UPDATE reg SET session = 0 WHERE name = '" + playerId.toString() + "'") <= 0) {
                updateToUUID(player, "reg", "name");
                st.executeUpdate("UPDATE reg SET session = 0 WHERE name = '" + playerId.toString() + "'");
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(st);
            safeClose(conn);
        }
    }

    public boolean getIsLoggedIn(Player player)
    {
        try {
            if (getSession(player) > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MySqlPoolableException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendSystemMessage(Player player, String message) {
        message = "&6[SYSTEM] " + message;
        player.sendMessage(message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
    }

    public UUID GetUUID(Player player)
    {
        UUID playerId = null;
        try
        {
            playerId = UUIDFetcher.getUUIDOf(player.getName());
        }
        catch(Exception e)
        {
            _plugin.getLogger().warning("Exception while running UUIDFetcher");
            e.printStackTrace();
        }
        return playerId;
    }

    public UUID GetUUID(String name)
    {
        UUID playerId = null;
        try
        {
            playerId = UUIDFetcher.getUUIDOf(name);
        }
        catch(Exception e)
        {
            _plugin.getLogger().warning("Exception while running UUIDFetcher");
            e.printStackTrace();
        }
        return playerId;
    }


    public Map<String, UUID> GetUUIDs(List<String> names)
    {
        Map<String, UUID> playerIds = null;
        try
        {
             playerIds = UUIDFetcher.getUUIDOf(names);
        }
        catch(Exception e)
        {
            _plugin.getLogger().warning("Exception while running UUIDFetcher");
            e.printStackTrace();
        }
        return playerIds;
    }

    public boolean changePassword(Player player, String oldPw, String newPw, String controlPw) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        UUID playerId = player.getUniqueId();
        oldPw = getSHA(oldPw);
        try {
            if (newPw.equals(controlPw) && getPassword(player).equals(oldPw)) {
                conn = (Connection) _connPool.borrowObject();
                st = conn.createStatement();
                if (st.executeUpdate("UPDATE reg SET password = " + newPw + " WHERE name = '" + playerId.toString() + "'") <= 0) {
                    updateToUUID(player, "reg", "name");
                    if (st.executeUpdate("UPDATE reg SET password = " + newPw + " WHERE name = '" + playerId.toString() + "'") > 0)
                        return true;
                }
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(st);
            safeClose(conn);
        }
        return false;
    }

    public int setWarpPoint(Player player, String name) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        Location loc = player.getLocation();
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT count(*) as count FROM warps WHERE name = '" + name + "' AND world = '" + player.getWorld() + "'");
            while (res.next()) {
                if (res.getInt("count") > 0)
                    return -1;
                if (st.executeUpdate("INSERT INTO warps (id, name, x, y, z, rotX, rotY, world) VALUES (null, '" + name + "', " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", 0.0, '" + player.getWorld() + "')") > 0) {
                    return 1;
                }
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return 0;
    }

    private int getSession(Player player) throws SQLException, MySqlPoolableException {
        int session = 0;
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        UUID playerId = player.getUniqueId();
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT session FROM reg WHERE name = '" + playerId.toString() + "'");
            if(!res.next()) {
                updateToUUID(player, "reg", "name");
                res = st.executeQuery("SELECT session FROM reg WHERE name = '" + playerId.toString() + "'");
            }
            else {
                session = res.getInt("session");
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return session;
    }

    private boolean setSession(Player player) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        UUID playerId = player.getUniqueId();
        int session = (int) (System.currentTimeMillis() / 1000);
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            if (st.executeUpdate("UPDATE reg SET session = " + session + " WHERE name = '" + playerId.toString() + "'") <= 0)
            {
                updateToUUID(player, "reg", "name");
                if (st.executeUpdate("UPDATE reg SET session = " + session + " WHERE name = '" + playerId.toString() + "'") > 0)
                    return true;
            }
            else {
                return true;
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(st);
            safeClose(conn);
        }
        return false;
    }

    private void updateToUUID(Player player, String table, String field) throws SQLException, MySqlPoolableException {
        Connection conn = null;
        Statement st = null;
        UUID playerId = player.getUniqueId();
        _plugin.getLogger().info("Updating name "+player.getName()+" to UUID " + playerId.toString());
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            st.executeUpdate("UPDATE " + table + " SET " + field + " = '" + playerId.toString() + "' WHERE " + field + " = '" + player.getName() + "'");
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(st);
            safeClose(conn);
        }
    }

    private String getPassword(Player player) throws SQLException, MySqlPoolableException {
        String password = null;
        Connection conn = null;
        Statement st = null;
        ResultSet res = null;
        UUID playerId = player.getUniqueId();
        try {
            conn = (Connection)_connPool.borrowObject();
            st = conn.createStatement();
            res = st.executeQuery("SELECT password FROM reg WHERE name = '" + playerId.toString() + "'");
            if(!res.next()) {
                updateToUUID(player, "reg", "name");
                res = st.executeQuery("SELECT password FROM reg WHERE name = '" + playerId.toString() + "'");
            }
            else {
                password = res.getString("password");
            }
        } catch (SQLException e) {
            throw e;
        }  catch (Exception e) {
            throw new MySqlPoolableException("Failed to borrow connection from the pool", e);
        } finally {
            safeClose(res);
            safeClose(st);
            safeClose(conn);
        }
        return password;
    }

    private void safeClose(Connection conn) {
        if (conn != null) {
            try {
                _connPool.returnObject(conn);
            }
            catch (Exception e) {
                _plugin.getLogger().warning("Failed to return the connection to the pool");
                e.printStackTrace();
            }
        }
    }

    private void safeClose(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (SQLException e) {
                _plugin.getLogger().warning("Failed to close databse resultset");
                e.printStackTrace();
            }
        }
    }

    private void safeClose(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                _plugin.getLogger().warning("Failed to close databse statment");
                e.printStackTrace();
            }
        }
    }

    private static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder();
        for (byte aB : b) {
            buf.append(hexDigit[(aB >> 4) & 0x0f]);
            buf.append(hexDigit[aB & 0x0f]);
        }
        return buf.toString();
    }

    private String getSHA(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] output = md.digest();
            md.update(password.getBytes());
            output = md.digest();
            return bytesToHex(output);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return "";
    }
}
