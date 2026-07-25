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

public class WorldManager {
    
    /**
     * Créé un nouveau monde
     * 
     * @param sender        Le joueur qui a exécuté la commande
     * @param minigameType  Le type du mini-jeu qui sera joué dans le monde
     * @param seed          La seed du monde
     * @param spawnXCrd     Les coordonnées X du spawnpoint
     * @param spawnYCrd     Les coordonnées Y du spawnpoint
     * @param spawnZCrd     Les coordonnées Z du spawnpoint
     * @return
     */
    public int createNewRaidWorld(CommandSender sender, String minigameType, long seed, double spawnXCrd, double spawnYCrd, double spawnZCrd) {

        Player player = (Player)sender;
        String playerName = player.getName().toLowerCase();
        World playerWorld = player.getWorld();

        if(playerWorld.getName().equals("raid." + playerName)) {
            player.sendMessage("§c[WorldManager] §fYou can't have more than 1 world");
            return(0);
        }

        player.sendMessage("§9[WorldManager] §fCreating your world...");

        String worldName = minigameType + playerName;
        WorldCreator creator = new WorldCreator(worldName);

        creator.type(WorldType.NORMAL);
        creator.seed(seed);
        creator.generateStructures(true);

        World world = Bukkit.createWorld(creator);
        Location spawnPoint = new Location(world, spawnXCrd, spawnYCrd, spawnZCrd);

        spawnPoint.getChunk().load();
        world.setSpawnLocation(spawnPoint);
        player.sendMessage("§a[WorldManager] §fWorld created");

        Location targetSpawn = world.getSpawnLocation();

        player.sendMessage("§9[WorldManager] §fTeleporting to your world...");
        player.teleport(targetSpawn);
        player.setGameMode(GameMode.SURVIVAL);
        player.setRespawnLocation(targetSpawn, true);

        return(1);

    }

    /**
     * Supprimer un monde existant
     * 
     * @param sender    Le joueur qui a exécuté la commande
     */
    public void deleteWorld(CommandSender sender) {

        Player player = (Player)sender;
        World playerWorld = player.getWorld();
        World overworld = Bukkit.getWorld("world");

        for(Player players : playerWorld.getPlayers()) {
            players.sendMessage("§9[WorldManager] §fTeleporting to lobby...");
            players.teleport(overworld.getSpawnLocation());
            players.setGameMode(GameMode.CREATIVE);
            players.setRespawnLocation(overworld.getSpawnLocation(), true);
        }

        player.sendMessage("§9[WorldManager] §fDeleting your world...");

        String worldName = playerWorld.getName();
        String worldPath = "world/dimensions/minecraft/" + worldName;
        File worldFolder = new File(Bukkit.getWorldContainer(), worldPath);

        Bukkit.unloadWorld(worldName, false);
        Bukkit.getScheduler().runTaskLater(
            JavaPlugin.getProvidingPlugin(getClass()), () -> {

                boolean success = deleteWorldFolder(worldFolder);

                if(success) {
                    player.sendMessage("§a[WorldManager] §fWorld deleted");
                }

                else {
                    player.sendMessage("§c[WorldManager] §fDeleting error");
                }

            },
            20L
        );

    }

    /**
     * Supprimer le répertoire d'un monde présente sur le serveur
     * 
     * @param worldFolder   Le répertoire du monde à supprimer
     * @return
     */
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
