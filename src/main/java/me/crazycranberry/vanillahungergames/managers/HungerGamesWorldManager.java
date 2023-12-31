package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.events.BorderShrinkEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesWorldCreateCommandExecutedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesWorldCreatedEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import static me.crazycranberry.vanillahungergames.HungerGamesEventList.beginningBorderSize;
import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.VanillaHungerGames.logger;
import static me.crazycranberry.vanillahungergames.VanillaHungerGamesConfig.PREGAME_LOBBY_DEFAULT_NAME;
import static me.crazycranberry.vanillahungergames.utils.FileUtils.deleteRecursively;

public class HungerGamesWorldManager implements Listener {
    private static final String HUNGER_GAMES_WORLD_NAME = "hungryworld";
    private static World hungerGamesWorld;
    private static World pregameLobby;
    private static Location spawnLoc;
    private static WorldBorder border;
    private final Plugin plugin;
    private static boolean worldReady = false;

    public HungerGamesWorldManager() {
        this.plugin = getPlugin();
    }

    public static Location spawnLoc() {
        return spawnLoc;
    }

    public static boolean isInHungerGamesWorld(World world) {
        if (world == null) {
            return false;
        }
        if (world.equals(hungerGamesWorld()) || world.equals(pregameLobbyWorld())) {
            return true;
        }
        return false;
    }

    public static World hungerGamesWorld() {
        if (hungerGamesWorld == null && Bukkit.getServer().getWorld(HUNGER_GAMES_WORLD_NAME) != null) {
            hungerGamesWorld = Bukkit.getServer().getWorld(HUNGER_GAMES_WORLD_NAME);
        }
        return hungerGamesWorld;
    }

    public static World pregameLobbyWorld() {
        if (pregameLobby == null && Bukkit.getServer().getWorld(getPlugin().vanillaHungerGamesConfig().preGameLobbyWorldName()) != null) {
            pregameLobby = Bukkit.getServer().getWorld(getPlugin().vanillaHungerGamesConfig().preGameLobbyWorldName());
        }
        return pregameLobby;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().equals(plugin) && hungerGamesWorld() != null) {
            logger().info("Attempting to delete the hunger games world...");
            Bukkit.getServer().unloadWorld(hungerGamesWorld(), true);
            deleteRecursively(hungerGamesWorld().getWorldFolder());
            worldReady = false;
        }
    }

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent event) {
        if (isInHungerGamesWorld(event.getPlayer().getWorld())) {
            event.getPlayer().sendMessage(String.format("%sSorry mate, no other dimensions during the hunger games. Props to you for having the chance though!%s", ChatColor.GRAY, ChatColor.RESET));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin) && hungerGamesWorld() != null) {
            logger().info("Attempting to delete the hunger games world...");
            Bukkit.getServer().unloadWorld(hungerGamesWorld(), true);
            deleteRecursively(hungerGamesWorld().getWorldFolder());
            worldReady = false;
        }
    }

    @EventHandler
    public void onHungerGamesWorldCreateCommandExecuted(HungerGamesWorldCreateCommandExecutedEvent event) {
        WorldCreator God = new WorldCreator(HUNGER_GAMES_WORLD_NAME);
        hungerGamesWorld = God.createWorld();
        hungerGamesWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        hungerGamesWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        maybeGeneratePregameLobby();
        Bukkit.getPluginManager().callEvent(new HungerGamesWorldCreatedEvent());
    }

    private void maybeGeneratePregameLobby() {
        if (!getPlugin().vanillaHungerGamesConfig().usePreGameLobby()) {
            return;
        }
        WorldCreator God;
        if (!getPlugin().vanillaHungerGamesConfig().preGameLobbyWorldName().equals(PREGAME_LOBBY_DEFAULT_NAME)) {
            God = new WorldCreator(getPlugin().vanillaHungerGamesConfig().preGameLobbyWorldName());
        } else {
            God = new WorldCreator(getPlugin().vanillaHungerGamesConfig().preGameLobbyWorldName()).generator(new PreGameLobbyGenerator());
        }
        pregameLobby = God.createWorld();
        pregameLobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        pregameLobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        pregameLobby.setDifficulty(Difficulty.PEACEFUL);
    }

    @EventHandler
    public void onHungerGamesWorldCreated(HungerGamesWorldCreatedEvent event) {
        spawnLoc = Objects.requireNonNull(hungerGamesWorld).getSpawnLocation();
        border = Objects.requireNonNull(hungerGamesWorld).getWorldBorder();
        border.setCenter(spawnLoc());
        border.setSize(beginningBorderSize() * 2);
        Bukkit.getServer().broadcastMessage(String.format("%sA hunger games tournament has started!! Type /hgjoin to join the blood bath!%s", ChatColor.AQUA, ChatColor.RESET));
        worldReady = true;
    }

    @EventHandler
    public void onHungerGamesCompleted(HungerGamesCompletedEvent event) {
        logger().info("Attempting to delete the hunger games world...");
        Bukkit.getServer().unloadWorld(hungerGamesWorld(), true);
        File worldFolder = hungerGamesWorld().getWorldFolder();
        deleteRecursively(worldFolder);
        worldReady = false;
        hungerGamesWorld = null;
    }

    @EventHandler
    public void onTournamentStart(TournamentStartedEvent event) {
        if (hungerGamesWorld() == null) {
            //possible to get here because an empty tournament will call a HungerGamesCompletedEvent which will delete the world
            return;
        }
        for (Entity e : hungerGamesWorld().getEntities()) {
            if (e instanceof Item) {
                e.remove();
            }
        }
    }

    public static boolean isWorldReady() {
        return worldReady;
    }

    public static String borderBoundaries(float size) {
        double lowerX = spawnLoc().getX() - size;
        double upperX = spawnLoc().getX() + size;
        double lowerZ = spawnLoc().getZ() - size;
        double upperZ = spawnLoc().getZ() + size;
        return String.format("x: [%s, %s]   y: ~   z: [%s, %s]", lowerX, upperX, lowerZ, upperZ);
    }

    @EventHandler
    public static void shrinkBorder(BorderShrinkEvent event) {
        border.setSize(event.getNewSize() * 2, 120);
    }

    public static class PreGameLobbyGenerator extends ChunkGenerator {
        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateBedrock() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }

        @Override
        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
            if (chunkX == 0 && chunkZ == 0) {
                generateLobbyFloor(chunkData);
                generateLobbyWalls(chunkData);
            }
        }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 7, world.getMaxHeight(), 7);
        }

        private void generateLobbyFloor(@NotNull ChunkData chunkData) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    chunkData.setBlock(i, chunkData.getMaxHeight()-5, j, Material.BEDROCK);
                }
            }
        }

        private void generateLobbyWalls(@NotNull ChunkData chunkData) {
            for (int i = 0; i < 16; i++) {
                chunkData.setBlock(i, chunkData.getMaxHeight()-4, 0, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMaxHeight()-3, 0, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMaxHeight()-2, 0, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMaxHeight()-4, 15, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMaxHeight()-3, 15, Material.BEDROCK);
                chunkData.setBlock(i, chunkData.getMaxHeight()-2, 15, Material.BEDROCK);
                chunkData.setBlock(0, chunkData.getMaxHeight()-4, i, Material.BEDROCK);
                chunkData.setBlock(0, chunkData.getMaxHeight()-3, i, Material.BEDROCK);
                chunkData.setBlock(0, chunkData.getMaxHeight()-2, i, Material.BEDROCK);
                chunkData.setBlock(15, chunkData.getMaxHeight()-4, i, Material.BEDROCK);
                chunkData.setBlock(15, chunkData.getMaxHeight()-3, i, Material.BEDROCK);
                chunkData.setBlock(15, chunkData.getMaxHeight()-2, i, Material.BEDROCK);
            }
        }
    }
}
