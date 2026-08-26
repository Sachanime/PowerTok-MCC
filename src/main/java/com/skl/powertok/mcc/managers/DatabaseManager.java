package com.skl.powertok.mcc.managers;

import java.sql.SQLException;
import java.sql.Connection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseManager {
    
    private HikariDataSource dataSource;

    public void connect(FileConfiguration config) {

        HikariConfig hikariConfig = new HikariConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String db = config.getString("database.name");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + db);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(10000);

        this.dataSource = new HikariDataSource(hikariConfig);

    }

    public Connection getConnection() throws SQLException {
        return(dataSource.getConnection());
    }

    public void close() {
        
        if(dataSource != null) {
            dataSource.close();
        }

    }

}
