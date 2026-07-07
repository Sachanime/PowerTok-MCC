package com.skl.powertok.mcc;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;

import com.skl.powertok.mcc.managers.BossbarManager;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getLogger().info("Plugin started");
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        BossbarManager welcomerBossBar = new BossbarManager();
        welcomerBossBar.displayBossBar(player, "PowerTok MCP - Dev Server", NamedTextColor.WHITE, 1.0f, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin stopped");
    }
    
}
