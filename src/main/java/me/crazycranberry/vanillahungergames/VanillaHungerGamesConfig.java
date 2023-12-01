package me.crazycranberry.vanillahungergames;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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
    private int minPlayersToStart;
    private int preGameCountdownMinutes;
    private int preGameCountdownSeconds;
    private boolean useVanillaItemsInChests;
    private List<CustomItemSpawn> customItemSpawnsInChests;

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
        minPlayersToStart = validateMinPlayersToStart(config.getInt("min_players_to_start", originalConfig.getInt("min_players_to_start")));
        preGameCountdownMinutes = validatePreGameCountdownMinutes(config.getInt("pre_game_countdown.minutes", originalConfig.getInt("pre_game_countdown.minutes")));
        preGameCountdownSeconds = validatePreGameCountdownSeconds(config.getInt("pre_game_countdown.seconds", originalConfig.getInt("pre_game_countdown.seconds")));
        useVanillaItemsInChests = config.getBoolean("chests.use_vanilla_items", originalConfig.getBoolean("chests.use_vanilla_items"));
        customItemSpawnsInChests = config.getList("chests.custom_item_spawns", List.of()).stream().map(c -> CustomItemSpawn.fromYaml((LinkedHashMap<String, ?>) c)).filter(Objects::nonNull).toList();
    }

    private int validateMinPlayersToStart(int configValue) {
        if (configValue < 0) {
            logger().warning("The min_players_to_start value was less than 1... Defaulting to 1.");
            return 1;
        }
        return configValue;
    }

    private int validatePreGameCountdownMinutes(int configValue) {
        if (configValue < 0) {
            logger().warning("The pre_game_countdown.minutes value was less than 0... Defaulting to 2.");
            return 2;
        }
        return configValue;
    }

    private int validatePreGameCountdownSeconds(int configValue) {
        if (configValue < 0 || configValue > 59) {
            logger().warning("The pre_game_countdown.seconds value was less than 0 or greater than 59... Defaulting to 0.");
            return 0;
        }
        return configValue;
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

    public int preGameCountdownMinutes() {
        return preGameCountdownMinutes;
    }

    public int preGameCountdownSeconds() {
        return preGameCountdownSeconds;
    }

    public int minPlayersToStart() {
        return minPlayersToStart;
    }

    public boolean useVanillaItemsInChests() {
        return useVanillaItemsInChests;
    }

    public List<CustomItemSpawn> customItemSpawnsInChests() {
        return customItemSpawnsInChests;
    }
}
