package com.skl.powertok.mcc.managers;

import java.lang.IllegalStateException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    
    private final JavaPlugin plugin;
    private JsonObject config;
    private Boolean loaded;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loaded = false;
    }

    private Path resolveConfigPath() {

        String os = System.getProperty("os.name").toLowerCase();
        Path configPath;

        if(os.contains("win")) {

            String appData = System.getenv("APPDATA");

            if(appData == null) {
                appData = System.getProperty("user.home") + "\\AppData\\Roaming";
            }

            configPath = Path.of(appData, "PowerTok", "config.json");

        }

        else {
            
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            String configBase = (xdgConfig != null)
                ? xdgConfig
                : System.getProperty("user.home") + "/.config";

            configPath = Path.of(configBase, "powertok", "config.json");

        }

        return(configPath);

    }

    private void load() {

        if(loaded) {
            return;
        }

        Path configPath = resolveConfigPath();

        if(!Files.exists(configPath)) {
            Bukkit.getLogger().severe("[MCC Guard] Le fichier de configuration " + configPath + " est introuvable");
            return;
        }

        try {

            String content = Files.readString(configPath);
            config = JsonParser.parseString(content).getAsJsonObject();

            loaded = true;

        }

        catch(IOException e) {
            Bukkit.getLogger().severe("[MCC Guard] Impossible de lire le fichier de configuration : " + e.getMessage() + "\n" + e.getStackTrace());
        }

        catch(JsonSyntaxException | IllegalStateException e) {
            Bukkit.getLogger().severe("[MCC Guard] Fichier de configuration mal formé : " + e.getMessage() + "\n" + e.getStackTrace());
        }

    }

    public String getHwid() {

        load();

        if(config == null || !config.has("hwid")) {
            return(null);
        }

        String key = config.get("hwid").getAsString();
        return(key);

    }

    public String getKey() {

        load();

        String pluginName = plugin.getName().toLowerCase();

        if(config == null || !config.has(pluginName)) {
            return(null);
        }

        String key = config.get(pluginName).getAsString();
        return(key);

    }

}
