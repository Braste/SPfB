package de.braste.SPfB.functions;

import org.apache.commons.pool.BasePoolableObjectFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlPoolableObjectFactory extends BasePoolableObjectFactory {
    private String host;
    private int port;
    private String schema;
    private String user;
    private String password;

    public MySqlPoolableObjectFactory(String host, int port, String schema,
                                      String user, String password) {
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean validateObject(Object conn) {
        try {
            if (((Connection) conn).isValid(0))
                return true;
        } catch (SQLException ignore) {
            // ignore
        }
        return false;
    }

    @Override
    public Object makeObject() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String url = "jdbc:mysql://" + host + ":" + port + "/"
                + schema + "?autoReconnectForPools=true";
        return DriverManager.getConnection(url, user, password);
    }
}
