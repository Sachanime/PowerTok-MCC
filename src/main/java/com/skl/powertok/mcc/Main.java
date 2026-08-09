package com.skl.powertok.mcc;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.World;
import org.bukkit.Location;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import com.skl.powertok.mcc.managers.BossbarManager;
import org.bukkit.GameMode;
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
        World playerWorld = player.getWorld();
        String playerWorldName = playerWorld.getName();
        Location spawnPoint = playerWorld.getSpawnLocation();

        player.teleport(spawnPoint);
        //BossbarManager welcomerBossBar = new BossbarManager();
        //welcomerBossBar.displayBossBar(player, "PowerTok MCP - Dev Server", NamedTextColor.WHITE, 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

        if(playerWorldName.equals("world")) {
            player.setGameMode(GameMode.CREATIVE);
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin stopped");
    }
    
}
