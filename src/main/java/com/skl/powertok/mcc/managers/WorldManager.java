package com.skl.powertok.mcc.managers;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import com.skl.powertok.mcc.enums.MapType;
import com.skl.powertok.mcc.enums.MinigameType;

public class WorldManager {
    
    public int createNewRaidWorld(CommandSender sender, MinigameType minigameType, MapType worldType) {

        Player player = (Player)sender;
        String playerName = player.getName().toLowerCase();
        World playerWorld = player.getWorld();
        String minigame = minigameType.getMinigame();
        long worldSeed = worldType.getSeed();

        if(playerWorld.getName().equals("raid." + playerName)) {
            player.sendMessage("§c[PowerTok WorldManager] §fYou can't have more than 1 world");
            return(0);
        }

        player.sendMessage("§9[PowerTok WorldManager] §fCreating your world...");

        String worldName = minigame + playerName;
        WorldCreator creator = new WorldCreator(worldName);

        creator.type(WorldType.NORMAL);
        creator.seed(worldSeed);
        creator.generateStructures(true);

        World world = Bukkit.createWorld(creator);
        int xCrd = worldType.getXCrd();
        int yCrd = worldType.getYCrd();
        int zCrd = worldType.getZCrd();
        Location spawnPoint = new Location(world, xCrd, yCrd, zCrd);
        spawnPoint.getChunk().load();
        world.setSpawnLocation(spawnPoint);
        player.sendMessage("§a[PowerTok WorldManager] §fWorld created");

        Location targetSpawn = world.getSpawnLocation();

        player.sendMessage("§9[PowerTok Worldanager] §fTeleporting to your world...");
        player.teleport(targetSpawn);
        player.setGameMode(GameMode.SURVIVAL);
        player.setRespawnLocation(targetSpawn);

        return(1);

    }

    public void deleteWorld(CommandSender sender) {

        Player player = (Player)sender;
        World playerWorld = player.getWorld();
        World overworld = Bukkit.getWorld("world");

        for(Player players : playerWorld.getPlayers()) {
            players.sendMessage("§9[PowerTok WorldManager] §fTeleporting to lobby...");
            players.teleport(overworld.getSpawnLocation());
            players.setGameMode(GameMode.CREATIVE);
            players.setRespawnLocation(overworld.getSpawnLocation());
        }

        player.sendMessage("§9[PowerTok WorldManager] §fDeleting your world...");

        String worldName = playerWorld.getName();
        String worldPath = "world/dimensions/minecraft/" + worldName;
        File worldFolder = new File(Bukkit.getWorldContainer(), worldPath);

        Bukkit.unloadWorld(worldName, false);
        Bukkit.getScheduler().runTaskLater(
            JavaPlugin.getProvidingPlugin(getClass()), () -> {

                boolean success = deleteWorldFolder(worldFolder);

                if(success) {
                    player.sendMessage("§a[PowerTok WorldManager] §fWorld deleted");
                }

                else {
                    player.sendMessage("§c[PowerTok WorldManager] §fDeleting error");
                }

            },
            20L
        );

    }

    private boolean deleteWorldFolder(File worldFolder) {

        if(worldFolder.isDirectory()) {

            File[] files = worldFolder.listFiles();

            for(File f : files) {
                deleteWorldFolder(f);
            }

        }

        return(worldFolder.delete());

    }

}
