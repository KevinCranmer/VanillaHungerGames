package me.crazycranberry.vanillahungergames;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.VanillaHungerGames.logger;
import static me.crazycranberry.vanillahungergames.utils.FileUtils.loadOriginalConfig;

public class VanillaHungerGamesConfig {
    public static final String PREGAME_LOBBY_DEFAULT_NAME = "hg_default_world";
    private final YamlConfiguration originalConfig;
    private List<String> commandsToRunAfterMatch;
    private boolean allowSpectateTeleport;
    private boolean requireAdminToCreateGames;
    private boolean usePreGameLobby;
    private String preGameLobbyWorldName;

    public VanillaHungerGamesConfig(YamlConfiguration config) {
        originalConfig = loadOriginalConfig("vanilla_hunger_games.yml");
        updateOutOfDateConfig(config);
        loadConfig(config);
    }

    private void updateOutOfDateConfig(YamlConfiguration config) {
        boolean madeAChange = false;
        for (String key : originalConfig.getKeys(true)) {
            if (!config.isString(key) && !config.isConfigurationSection(key) && !config.isBoolean(key) && !config.isDouble(key) && !config.isInt(key) && !config.isList(key)) {
                logger().info("The " + key + " is missing from vanilla_hunger_games.yml, adding it now.");
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
        allowSpectateTeleport = config.getBoolean("allow_spectate_teleporting", true);
        requireAdminToCreateGames = config.getBoolean("require_admin_to_create", true);
        usePreGameLobby = config.getBoolean("pre_game_lobby.use", false);
        preGameLobbyWorldName = config.getString("pre_game_lobby.world_name", PREGAME_LOBBY_DEFAULT_NAME).trim();
    }

    public List<String> commandsToRunAfterMatch() {
        return commandsToRunAfterMatch;
    }

    public boolean allowSpectateTeleport() {
        return allowSpectateTeleport;
    }

    public boolean requireAdminToCreateGames() {
        return requireAdminToCreateGames;
    }

    public boolean usePreGameLobby() {
        return usePreGameLobby;
    }

    public String preGameLobbyWorldName() {
        return preGameLobbyWorldName;
    }
}
