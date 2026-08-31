package com.skl.powertok.mcc.managers;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AccessManager {

    private JavaPlugin plugin;
    private ConfigManager configManager;

    public AccessManager(JavaPlugin plugin) {
        this.plugin = plugin;
        configManager = new ConfigManager(plugin);
    }
    
    public Boolean checkAccess() {

        try {

            String pluginName = plugin.getName();
            String hwid = configManager.getHwid();
            String key = configManager.getKey();

            if(hwid == null || hwid.isEmpty()) {
                Bukkit.getLogger().severe("[MCC Guard] Identification impossible");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return(false);
            }

            if(key == null || key.isEmpty()) {
                Bukkit.getLogger().severe("[MCC Guard] Aucune clé de licence pour " + pluginName);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return(false);
            }

            String url = String.format(
                "https://api.powertok.fr/verify?plugin=%s&hwid=%s&key=%s",
                URLEncoder.encode(pluginName, StandardCharsets.UTF_8),
                URLEncoder.encode(hwid, StandardCharsets.UTF_8),
                URLEncoder.encode(key, StandardCharsets.UTF_8)
            );

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            
            if(!json.has("access")) {
                int errorCode = response.statusCode();
                Bukkit.getLogger().severe("[MCC Guard] Une erreur est survenue lors de la vérification");
                Bukkit.getLogger().severe("[MCC Guard] ERROR " + errorCode);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return(false);
            }

            Boolean access = json.get("access").getAsBoolean();

            if(!access) {

                if(!json.has("error")) {
                    int errorCode = response.statusCode();
                    Bukkit.getLogger().severe("[MCC Guard] Une erreur est survenue lors de la vérification");
                    Bukkit.getLogger().severe("[MCC Guard] ERROR " + errorCode);
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    return(false);
                }

                int errorCode = response.statusCode();
                String error = json.get("error").getAsString();
                Bukkit.getLogger().severe("[MCC Guard] Clé de licence invalide pour " + pluginName);
                Bukkit.getLogger().severe("[MCC Guard] ERROR " + errorCode + " " + error);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return(false);

            }

            return(true);

        }

        catch(IOException | InterruptedException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
            return(false);
        }

        catch(Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
            return(false);
        }

    }

}
