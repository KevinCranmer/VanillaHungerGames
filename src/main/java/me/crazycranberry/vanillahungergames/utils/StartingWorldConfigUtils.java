package me.crazycranberry.vanillahungergames.utils;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.getParticipant;

public class StartingWorldConfigUtils {
    static final String CONFIG_FOLDER_NAME = "startingWorldConfigs";

    //We load from a config file because we want to be able to maintain the player state in the event of a server crash
    public static void restoreStartingWorldConfig(Player p) {
        File f = configFile(p);
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        ItemStack[] content = ((List<ItemStack>) c.get("inventory.armor")).toArray(new ItemStack[0]);
        p.getInventory().setArmorContents(content);
        content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
        p.getInventory().setContents(content);
        p.teleport(c.getLocation("location"));
        p.setGameMode(GameMode.values()[c.getInt("gameMode")]);
        for (PotionEffect hungerGamesPotionEffect : p.getActivePotionEffects()) {
            p.removePotionEffect(hungerGamesPotionEffect.getType());
        }
        p.addPotionEffects((List<PotionEffect>) c.get("activePotionEffects"));
        p.setBedSpawnLocation(findNearbyBed(c.getLocation("bedSpawnLocation")));
        Participant participant = getParticipant(p);
        if (participant != null && participant.getStartingWorldConfig() != null) {
            //If we still have the previous scoreboard from memory
            p.setScoreboard(participant.getStartingWorldConfig().getScoreboard());
        }
        List<Method> playerSetMethods = Arrays.stream(Player.class.getMethods()).filter(m -> m.getName().startsWith("set")).toList();
        for (Field field : Participant.ParticipantStartingWorldConfig.class.getDeclaredFields()) {
            if (List.of("inventory", "location", "gameMode", "activePotionEffects", "scoreboard", "bedSpawnLocation").contains(field.getName())) {
                continue;
            }
            Optional<Method> playerSetMethod = playerSetMethods.stream().filter(m -> setterMatch(field, m)).findFirst();
            if (playerSetMethod.isEmpty()) {
                continue;
            }
            try {
                playerSetMethod.get().invoke(p, c.get(field.getName()));
            } catch (IllegalArgumentException e) {
                if (c.get(field.getName()) instanceof Double) {
                    //Some types load as doubles but we want them to be floats. So give it a try converting it to a float
                    try {
                        playerSetMethod.get().invoke(p, ((Double) c.get(field.getName())).floatValue());
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        deleteStartingWorldConfig(p);
    }

    private static Location findNearbyBed(Location maybeSpawn) {
        if (maybeSpawn == null || maybeSpawn.getBlock().getBlockData() instanceof Bed) {
            return maybeSpawn;
        } else {
            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 3; j++) {
                    for (int k = -2; k < 3; k++) {
                        Location maybeBedLocation = maybeSpawn.clone().add(i, j, k);
                        if (maybeBedLocation.getBlock().getBlockData() instanceof Bed) {
                            return maybeBedLocation;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void saveStartingWorldConfig(Participant p) {
        Participant.ParticipantStartingWorldConfig config = p.getStartingWorldConfig();
        File f = configFile(p.getPlayer());
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.armor", config.getInventory().getArmorContents());
        c.set("inventory.content", config.getInventory().getContents());
        c.set("gameMode", config.getGameMode().ordinal());
        List<Method> configMethods = Arrays.asList(config.getClass().getDeclaredMethods());
        for (Field field : Participant.ParticipantStartingWorldConfig.class.getDeclaredFields()) {
            if (List.of("inventory", "gameMode", "scoreboard").contains(field.getName())) {
                continue;
            }
            Optional<Method> configMethod = configMethods.stream().filter(m -> getterMatch(field, m)).findFirst();
            if (configMethod.isEmpty()) {
                continue;
            }
            try {
                c.set(field.getName(), configMethod.get().invoke(config));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            c.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteStartingWorldConfig(Player p) {
        File f = configFile(p);
        if (!f.exists()) {
            return;
        }
        f.delete();
    }

    private static boolean getterMatch(Field field, Method method) {
        return method.getName().equalsIgnoreCase("get" + field.getName()) || method.getName().equalsIgnoreCase("is" + field.getName());
    }

    private static boolean setterMatch(Field field, Method method) {
        return method.getName().equalsIgnoreCase("set" + field.getName());
    }

    public static File configFile(Player p) {
        return new File(getPlugin().getDataFolder().getAbsolutePath() + File.separatorChar + CONFIG_FOLDER_NAME, p.getDisplayName() + ".yml");
    }

    public static boolean startingWorldConfigExists(Player p) {
        return configFile(p).exists();
    }
}