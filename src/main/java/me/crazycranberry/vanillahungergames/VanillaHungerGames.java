package me.crazycranberry.vanillahungergames;

import me.crazycranberry.vanillahungergames.commands.ClassCommand;
import me.crazycranberry.vanillahungergames.commands.ClassInfoCommand;
import me.crazycranberry.vanillahungergames.commands.ClassesCommand;
import me.crazycranberry.vanillahungergames.commands.CreateHungerGamesCommand;
import me.crazycranberry.vanillahungergames.commands.JoinHungerGamesCommand;
import me.crazycranberry.vanillahungergames.commands.LeaveHungerGamesCommand;
import me.crazycranberry.vanillahungergames.commands.RefreshConfigCommand;
import me.crazycranberry.vanillahungergames.commands.SpectateTeleportCommand;
import me.crazycranberry.vanillahungergames.managers.EnchantmentManager;
import me.crazycranberry.vanillahungergames.managers.HungerGamesManager;
import me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager;
import me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager;
import me.crazycranberry.vanillahungergames.managers.PlayerClassManager;
import me.crazycranberry.vanillahungergames.managers.ScoreboardManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static me.crazycranberry.vanillahungergames.utils.FileUtils.loadConfig;

public final class VanillaHungerGames extends JavaPlugin implements Listener {
    private static Logger logger;
    private static VanillaHungerGames plugin;
    private VanillaHungerGamesConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getServer().getPluginManager().registerEvents(this, this);
        registerManagers();
        registerCommands();
        refreshConfigs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Logger logger() {
        return logger;
    }

    public static VanillaHungerGames getPlugin() {
        return plugin;
    }

    private void registerManagers() {
        getServer().getPluginManager().registerEvents(new HungerGamesManager(), this);
        getServer().getPluginManager().registerEvents(new HungerGamesParticipantManager(), this);
        getServer().getPluginManager().registerEvents(new HungerGamesWorldManager(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerClassManager(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
    }

    private void registerCommands() {
        setCommandManager("hgcreate", new CreateHungerGamesCommand());
        setCommandManager("hgjoin", new JoinHungerGamesCommand());
        setCommandManager("hgleave", new LeaveHungerGamesCommand());
        setCommandManager("hgteleport", new SpectateTeleportCommand());
        setCommandManager("hgclass", new ClassCommand());
        setCommandManager("hgclassInfo", new ClassInfoCommand());
        setCommandManager("hgclasses", new ClassesCommand());
        setCommandManager("hgrefresh", new RefreshConfigCommand());
    }

    private void setCommandManager(String command, CommandExecutor commandManager) {
        PluginCommand pc = getCommand(command);
        if (pc == null) {
            logger().warning(String.format("[ ERROR ] - Error loading the %s command", command));
        } else {
            pc.setExecutor(commandManager);
        }
    }

    public VanillaHungerGamesConfig vanillaHungerGamesConfig() {
        return config;
    }

    public String refreshConfigs() {
        try {
            config = new VanillaHungerGamesConfig(loadConfig("vanilla_hunger_games.yml"));
            return "Successfully loaded configs.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
