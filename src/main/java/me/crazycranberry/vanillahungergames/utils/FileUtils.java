package me.crazycranberry.vanillahungergames.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;

public class FileUtils {
    public static boolean deleteRecursively(File path) {
        if (path == null) {
            return false;
        }
        if(path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteRecursively(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }


    public static YamlConfiguration loadConfig(String configName) throws InvalidConfigurationException {
        File configFile = new File(getPlugin().getDataFolder() + "" + File.separatorChar + configName);
        if(!configFile.exists()){
            getPlugin().saveResource(configName, true);
            System.out.println(String.format("[VanillaHungerGames] %s not found! copied %s to %s", configName, configName, getPlugin().getDataFolder()));
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            throw new InvalidConfigurationException("[VanillaHungerGames] An error occured while trying to load " + configName);
        }
        return config;
    }

    public static YamlConfiguration loadOriginalConfig(String configName) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            InputStream defaultChanceConfigStream = getPlugin().getResource(configName);
            assert defaultChanceConfigStream != null;
            InputStreamReader defaultChanceConfigReader = new InputStreamReader(defaultChanceConfigStream);
            config.load(defaultChanceConfigReader);
        } catch (InvalidConfigurationException | IOException e) {
            System.out.println("[ VanillaHungerGames ] An error occured while trying to load the (default) " + configName + " file.");
            e.printStackTrace();
        }
        return config;
    }
}
