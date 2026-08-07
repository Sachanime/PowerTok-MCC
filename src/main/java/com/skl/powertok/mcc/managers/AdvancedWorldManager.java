package com.skl.powertok.mcc.managers;

import java.io.File;
import java.io.IOException;
import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.exceptions.UnknownWorldException;
import com.infernalsuite.asp.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.asp.api.exceptions.WorldLoadedException;
import com.infernalsuite.asp.api.exceptions.WorldTooBigException;
import com.infernalsuite.asp.api.exceptions.CorruptedWorldException;
import com.infernalsuite.asp.api.exceptions.InvalidWorldException;
import com.infernalsuite.asp.api.exceptions.NewerFormatException;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.loaders.file.FileLoader;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.GameMode;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdvancedWorldManager {
    
    private final JavaPlugin plugin;
    private final SlimeLoader loader;

    public AdvancedWorldManager() {
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());
        File worldDirectory = new File(plugin.getDataFolder().getParentFile(), "slime_worlds");
        this.loader = new FileLoader(worldDirectory);
    }

    public int createNewWorld(CommandSender sender, String minigameType, double spawnXCrd, double spawnYCrd, double spawnZCrd) {

        Player player = (Player)sender;
        String playerName = player.getName().toLowerCase();
        World playerWorld = player.getWorld();
        String templateName = "template_" + minigameType;
        String worldName = minigameType + "." + playerName;

        if(playerWorld.getName().contains(playerName)) {
            player.sendMessage("§c[AWM] §fYou can't have more than 1 world");
            return(0);
        }

        player.sendMessage("§9[AWM] §fCreating your world...");

        SlimePropertyMap propertyMap = new SlimePropertyMap();
        propertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        propertyMap.setValue(SlimeProperties.ALLOW_MONSTERS, true);
        propertyMap.setValue(SlimeProperties.SPAWN_X, (int)spawnXCrd);
        propertyMap.setValue(SlimeProperties.SPAWN_Y, (int)spawnYCrd);
        propertyMap.setValue(SlimeProperties.SPAWN_Z, (int)spawnZCrd);

        AdvancedSlimePaperAPI asp = AdvancedSlimePaperAPI.instance();

        new BukkitRunnable() {

            @Override
            public void run() {
                try {

                    SlimeWorld template = asp.readWorld(loader, templateName, true, propertyMap);
                    SlimeWorld clonedWorld = template.clone(worldName);

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            asp.loadWorld(clonedWorld, true);
                            World world = Bukkit.getWorld(worldName);
                            
                            if(world == null) {
                                return;
                            }

                            world.setGameRule(GameRules.ADVANCE_TIME, false);
                            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
                            world.setGameRule(GameRules.BLOCK_DROPS, false);
                            world.setGameRule(GameRules.ENTITY_DROPS, false);
                            world.setGameRule(GameRules.MOB_DROPS, false);
                            world.setGameRule(GameRules.MOB_GRIEFING, false);
                            world.setGameRule(GameRules.KEEP_INVENTORY, true);
                            world.setTime(1000L);
                            
                            Location worldBorderCenter = new Location(world, 81, 64, 38);
                            WorldBorder worldBorder = world.getWorldBorder();
                            worldBorder.setCenter(worldBorderCenter);
                            worldBorder.setSize(150.0);

                            Location targetSpawn = new Location(world, spawnXCrd, spawnYCrd, spawnZCrd);

                            player.sendMessage("§a[AWM] §fWorld created");
                            player.sendMessage("§9[AWM] §fTeleporting to your world...");
                            player.teleport(targetSpawn);
                            player.setGameMode(GameMode.ADVENTURE);
                            player.setRespawnLocation(targetSpawn, true);

                        }

                    }.runTask(plugin);

                }

                catch(UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException error) {
                    player.sendMessage("§c[AWM] §fAn error occurred while creating the world");
                    error.printStackTrace();
                }
            }

        }.runTaskAsynchronously(plugin);

        return(1);

    }

    public void deleteWorld(CommandSender sender) {

        Player player = (Player)sender;
        World playerWorld = player.getWorld();
        World overworld = Bukkit.getWorld("world");

        if(overworld == null) {
            return;
        }

        for(Player players : playerWorld.getPlayers()) {
            players.sendMessage("§9[AWM] §fTeleporting to lobby...");
            players.teleport(overworld.getSpawnLocation());
            players.setGameMode(GameMode.CREATIVE);
            players.setRespawnLocation(overworld.getSpawnLocation(), true);
        }

        player.sendMessage("§9[AWM] §fDeleting your world...");
        String worldName = playerWorld.getName();

        boolean unloaded = Bukkit.unloadWorld(worldName, false);
        System.gc();

        if(unloaded) {
            player.sendMessage("§a[AWM] §fWorld deleted successfully");
        }

        else {
            player.sendMessage("§c[AWM] §fAn error occurred while deleting the world");
        }

    }

    public void createSlime(CommandSender sender, String worldName, String slimeName) {

        new BukkitRunnable() {
            
            @Override
            public void run() {

                try {

                    sender.sendMessage("§9[AWM] §fConverting Anvil world...");

                    AdvancedSlimePaperAPI asp = AdvancedSlimePaperAPI.instance();
                    File worldFolder = new File(Bukkit.getServer().getWorldContainer(), "worldsStorage/" + worldName + "/dimensions/minecraft/overworld");
                    File slimesDirectory = new File(plugin.getDataFolder().getParentFile(), "slime_worlds");
                    SlimeLoader loader = new FileLoader(slimesDirectory);
                    SlimeWorld tempSlimeWorld = asp.readVanillaWorld(worldFolder, worldName, loader);
                    SlimeWorld slimeWorld = tempSlimeWorld.clone(slimeName);

                    asp.saveWorld(slimeWorld);
                    
                    sender.sendMessage("§a[AWM] §fWorld converted");

                }

                catch(InvalidWorldException | WorldLoadedException | WorldTooBigException | WorldAlreadyExistsException | IOException error) {
                    sender.sendMessage("§c[AWM] §fAn error occurred while converting the world");
                    error.printStackTrace();
                }

            }

        }.runTaskAsynchronously(plugin);

    }

}
