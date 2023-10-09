package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.playerclasses.Archer;
import me.crazycranberry.vanillahungergames.playerclasses.Arsonist;
import me.crazycranberry.vanillahungergames.playerclasses.Assassin;
import me.crazycranberry.vanillahungergames.playerclasses.Barbarian;
import me.crazycranberry.vanillahungergames.playerclasses.Bard;
import me.crazycranberry.vanillahungergames.playerclasses.Cleric;
import me.crazycranberry.vanillahungergames.playerclasses.Cultist;
import me.crazycranberry.vanillahungergames.playerclasses.Cultivator;
import me.crazycranberry.vanillahungergames.playerclasses.Gambler;
import me.crazycranberry.vanillahungergames.playerclasses.Jockey;
import me.crazycranberry.vanillahungergames.playerclasses.Miner;
import me.crazycranberry.vanillahungergames.playerclasses.Necromancer;
import me.crazycranberry.vanillahungergames.playerclasses.Ninja;
import me.crazycranberry.vanillahungergames.playerclasses.PersonalSpaceGuy;
import me.crazycranberry.vanillahungergames.playerclasses.PlayerClass;
import me.crazycranberry.vanillahungergames.playerclasses.Poseidon;
import me.crazycranberry.vanillahungergames.playerclasses.Snowman;
import me.crazycranberry.vanillahungergames.playerclasses.Stomper;
import me.crazycranberry.vanillahungergames.playerclasses.Tamer;
import me.crazycranberry.vanillahungergames.playerclasses.Trapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;

public class PlayerClassManager implements Listener {
    private static List<PlayerClass> possibleClasses;
    private final Plugin plugin;

    public PlayerClassManager() {
        this.plugin = getPlugin();
        possibleClasses = new ArrayList<>();
        registerClasses();
    }

    private void registerClasses() {
        registerClass(new Archer());
        registerClass(new Arsonist());
        registerClass(new Assassin());
        registerClass(new Barbarian());
        registerClass(new Bard());
        registerClass(new Cleric());
        registerClass(new Cultist());
        registerClass(new Cultivator());
        registerClass(new Gambler());
        registerClass(new Jockey());
        registerClass(new Miner());
        registerClass(new Necromancer());
        registerClass(new Ninja());
        registerClass(new PersonalSpaceGuy());
        registerClass(new Poseidon());
        registerClass(new Snowman());
        registerClass(new Stomper());
        registerClass(new Tamer());
        registerClass(new Trapper());
    }

    private void registerClass(PlayerClass playerClass) {
        Bukkit.getServer().getPluginManager().registerEvents(playerClass, plugin);
        possibleClasses.add(playerClass);
    }

    public static List<PlayerClass> possibleClasses() {
        return possibleClasses;
    }
}
