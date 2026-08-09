package com.skl.powertok.mcc.managers;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
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

    /**
     * Create a world from a Slime
     * 
     * @param player        Command executor player
     * @param minigameType  The type of mini-game that will be played in the world
     * @param spawnXCrd     Spawnpoint X-coordinates
     * @param spawnYCrd     Spawnpoint Y-coordinates
     * @param spawnZCrd     Spawnpoint Z-coordinates           
     */
    public void createNewWorld(Player player, String minigameType, double spawnXCrd, double spawnYCrd, double spawnZCrd) {

        String playerName = player.getName().toLowerCase();
        World playerWorld = player.getWorld();
        String templateName = "template_" + minigameType;
        String worldName = minigameType + "." + playerName;

        if(playerWorld.getName().contains(playerName)) {
            player.sendMessage("§c[AWM] §fVous ne pouvez pas posséder plus d'un monde à la fois. Quittez votre monde actuel et réessayez");
            return;
        }

        player.sendMessage("§9[AWM] §fCréation de votre monde...");

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

                            player.sendMessage("§a[AWM] §fMonde créé");
                            player.sendMessage("§9[AWM] §fTéléportation dans votre monde...");
                            player.teleport(targetSpawn);
                            player.setGameMode(GameMode.ADVENTURE);
                            player.setRespawnLocation(targetSpawn, true);

                        }

                    }.runTask(plugin);

                }

                catch(UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException error) {

                    player.sendMessage("§c[AWM] §fUne erreur s'est produite lors de la création du monde");

                    if(player.isOp()) {
                        player.sendMessage("§c" + error.getMessage());
                    }

                    else {
                        player.sendMessage("§fVeuillez contacter un administrateur");
                    }

                    error.printStackTrace();

                }
            }

        }.runTaskAsynchronously(plugin);

        return;

    }

    /**
     * Delete a world
     * 
     * @param player Command executor player
     */
    public void deleteWorld(Player player) {

        World playerWorld = player.getWorld();
        World overworld = Bukkit.getWorld("world");

        if(overworld == null) {
            return;
        }

        for(Player players : playerWorld.getPlayers()) {
            players.sendMessage("§9[AWM] §fTéléportation vers le lobby...");
            players.teleport(overworld.getSpawnLocation());
            players.setGameMode(GameMode.CREATIVE);
            players.setRespawnLocation(overworld.getSpawnLocation(), true);
        }

        player.sendMessage("§9[AWM] §fSuppression de votre monde...");
        String worldName = playerWorld.getName();

        boolean unloaded = Bukkit.unloadWorld(worldName, false);
        System.gc();

        if(unloaded) {
            player.sendMessage("§a[AWM] §fMonde supprimé");
        }

        else {

            player.sendMessage("§c[AWM] §fUne erreur s'est produite lors de la suppression du monde");

            if(!(player.isOp())) {
                player.sendMessage("§fVeuillez contacter un administrateur");
            }

        }

    }

    /**
     * Convert an Anvil world to Slime
     * 
     * @param sender Commande sender
     * @param worldName Anvil world name to convert
     * @param slimeName Slime name to give
     */
    public void createSlime(CommandSender sender, String worldName, String slimeName) {

        new BukkitRunnable() {
            
            @Override
            public void run() {

                try {

                    sender.sendMessage("§9[AWM] §fConversion du monde Anvil...");

                    AdvancedSlimePaperAPI asp = AdvancedSlimePaperAPI.instance();
                    File anvilFolder = new File(Bukkit.getServer().getWorldContainer(), "anvilStorage/" + worldName);
                    File slimesDirectory = new File(plugin.getDataFolder().getParentFile(), "slime_worlds");
                    SlimeLoader loader = new FileLoader(slimesDirectory);
                    SlimeWorld tempSlimeWorld = asp.readVanillaWorld(anvilFolder, worldName, loader);
                    SlimeWorld slimeWorld = tempSlimeWorld.clone(slimeName);

                    asp.saveWorld(slimeWorld);
                    
                    sender.sendMessage("§a[AWM] §fMonde convertit");

                }

                catch(InvalidWorldException | WorldLoadedException | WorldTooBigException | WorldAlreadyExistsException | IOException | IllegalArgumentException error) {
                    
                    sender.sendMessage("§c[AWM] §fUne erreur s'est produite lors de la conversion du monde");

                    if(sender instanceof Player) {

                        Player player = (Player)sender;

                        if(player.isOp()) {
                            player.sendMessage("§c" + error.getMessage());
                        }

                        else {
                            player.sendMessage("§fVeuillez contacter un administrateur");
                        }

                    }
                    
                    error.printStackTrace();

                }

            }

        }.runTaskAsynchronously(plugin);

    }

}
