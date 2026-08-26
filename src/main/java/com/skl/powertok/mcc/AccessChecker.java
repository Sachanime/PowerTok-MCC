package com.skl.powertok.mcc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.skl.powertok.mcc.managers.DatabaseManager;

public class AccessChecker {

    private JavaPlugin plugin;
    private DatabaseManager databaseManager;

    public AccessChecker(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }
    
    public void checkAccess() {

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

            try(
                Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM minecraft_access WHERE username = 'Sachanime'");
            ) {
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        
                        Boolean access = rs.getBoolean("powerraids");

                        if(access) {
                            Bukkit.getLogger().info("Authorized access");
                        }

                        else{
                            Bukkit.getLogger().info("Unauthorized access");
                        }

                    }
                }
            }

            catch(SQLException e) {
                e.printStackTrace();
            }

        });

    }

}
