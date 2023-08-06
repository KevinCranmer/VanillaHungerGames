package me.crazycranberry.vanillahungergames;

import me.crazycranberry.vanillahungergames.commands.ClassCommand;
import me.crazycranberry.vanillahungergames.commands.ClassInfoCommand;
import me.crazycranberry.vanillahungergames.commands.ClassesCommand;
import me.crazycranberry.vanillahungergames.commands.CreateHungerGamesCommand;
import me.crazycranberry.vanillahungergames.commands.JoinHungerGamesCommand;
import me.crazycranberry.vanillahungergames.commands.LeaveHungerGamesCommand;
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

public final class VanillaHungerGames extends JavaPlugin implements Listener {
    private static VanillaHungerGames plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getServer().getPluginManager().registerEvents(this, this);
        registerManagers();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
        setCommandManager("createhungergames", new CreateHungerGamesCommand());
        setCommandManager("joinhungergames", new JoinHungerGamesCommand());
        setCommandManager("leavehungergames", new LeaveHungerGamesCommand());
        setCommandManager("class", new ClassCommand());
        setCommandManager("classInfo", new ClassInfoCommand());
        setCommandManager("classes", new ClassesCommand());
    }

    private void setCommandManager(String command, CommandExecutor commandManager) {
        PluginCommand pc = getCommand(command);
        if (pc == null) {
            System.out.println(String.format("[VanillaHungerGames][ ERROR ] - Error loading the %s command", command));
        } else {
            pc.setExecutor(commandManager);
        }
    }
}
