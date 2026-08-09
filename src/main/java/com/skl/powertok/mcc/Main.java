package com.skl.powertok.mcc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import com.skl.powertok.mcc.managers.CommandManager;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getLogger().info("Plugin started");

        CommandManager commandLoader = new CommandManager(this);
        commandLoader.registerCommand();

        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();
        World playerWorld = player.getWorld();
        String playerWorldName = playerWorld.getName();
        Location spawnPoint = playerWorld.getSpawnLocation();

        Component emptyComponent = Component.empty();
        Component joiningComponent = MiniMessage.miniMessage().deserialize(playerName + " joined the game").color(NamedTextColor.YELLOW);
        
        event.joinMessage(emptyComponent);
        Bukkit.getConsoleSender().sendMessage(joiningComponent);

        if(playerWorldName.equals("world")) {
            player.teleport(spawnPoint);
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage("§a[PowerTok] §fBienvenue sur le serveur PowerTok");
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin stopped");
    }
    
}
