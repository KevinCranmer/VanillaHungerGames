package me.crazycranberry.vanillahungergames;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.utils.FileUtils.loadOriginalConfig;

public class VanillaHungerGamesConfig {
    private final YamlConfiguration originalConfig;
    private List<String> commandsToRunAfterMatch;

    public VanillaHungerGamesConfig(YamlConfiguration config) {
        originalConfig = loadOriginalConfig("vanilla_hunger_games.yml");
        updateOutOfDateConfig(config);
        loadConfig(config);
    }

    private void updateOutOfDateConfig(YamlConfiguration config) {
        boolean madeAChange = false;
        for (String key : originalConfig.getKeys(true)) {
            if (!config.isString(key) && !config.isConfigurationSection(key) && !config.isBoolean(key) && !config.isDouble(key) && !config.isInt(key) && !config.isList(key)) {
                System.out.println("[VanillaHungerGames] The " + key + " is missing from vanilla_hunger_games.yml, adding it now.");
                config.set(key, originalConfig.get(key));
                madeAChange = true;
            }
        }

        if (madeAChange) {
            try {
                config.save(getPlugin().getDataFolder() + "" + File.separatorChar + "vanilla_hunger_games.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig(YamlConfiguration config) {
        commandsToRunAfterMatch = config.getList("commands_to_run_after_match", List.of()).stream().filter(c -> c instanceof String).map(c -> (String)c).toList();
        System.out.println(commandsToRunAfterMatch);
    }

    public List<String> commandsToRunAfterMatch() {
        return commandsToRunAfterMatch;
    }
}
