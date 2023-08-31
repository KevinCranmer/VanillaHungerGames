package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.broadcastToHungerGamesParticipants;
import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static org.bukkit.entity.EnderDragon.Phase.CHARGE_PLAYER;
import static org.bukkit.entity.EnderDragon.Phase.STRAFING;
import static org.bukkit.entity.EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET;

public class Cultist extends PlayerClassWithRecurringTasks implements PlayerClass {
    private static final int NUM_SACRIFICES = 30;

    private static final int WARNING_SACRIFICES = NUM_SACRIFICES * 5 / 6;

    private static final int SEC_BEFORE_DRAGON_SWITCHES_PHASE = 30;

    private Map<String, Integer> cultists = new HashMap<>();

    Plugin plugin;

    List<EnderDragon.Phase> phases = List.of(
            STRAFING,
            SEARCH_FOR_BREATH_ATTACK_TARGET,
            CHARGE_PLAYER
    );

    public Cultist() {
        this.plugin = getPlugin();
    }

    @Override
    public String getName() {
        return "Cultist";
    }

    @Override
    public String getInfo() {
        return "Cultists try to sacrifice (kill) " + NUM_SACRIFICES + " Living Entities to complete their ritual.";
    }

    @EventHandler
    private void entitySacrificed(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && isCorrectClass(event.getEntity().getKiller())) {
            Player cultist = event.getEntity().getKiller();
            cultists.put(cultist.getDisplayName(), cultists.getOrDefault(cultist.getDisplayName(), 0) + 1);
            if (cultists.getOrDefault(cultist.getDisplayName(), 0) < NUM_SACRIFICES) {
                cultist.sendMessage(String.format("%s%s/%s sacrifices%s", ChatColor.RED, cultists.get(cultist.getDisplayName()), NUM_SACRIFICES, ChatColor.RESET));
            }
            if (cultists.get(cultist.getDisplayName()) == WARNING_SACRIFICES) {
                broadcastToHungerGamesParticipants(String.format("%sA cultist is dangerously close to completing their ritual%s", ChatColor.RED, ChatColor.RESET));
            }
            if (cultists.get(cultist.getDisplayName()) == NUM_SACRIFICES) {
                if (Math.random() < 0.5) {
                    EnderDragon dragon = (EnderDragon) hungerGamesWorld().spawnEntity(hungerGamesWorld().getSpawnLocation(), EntityType.ENDER_DRAGON);
                    updateEnderDragonPhase(plugin);
                    dragon.setPhase(phases.get(0));
                    cultist.sendMessage(String.format("%sYour ritual was successful and you've summoned an %sEnder Dragon.%s", ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.RESET));
                } else {
                    Entity wither = hungerGamesWorld().spawnEntity(hungerGamesWorld().getSpawnLocation(), EntityType.WITHER);
                    int x = (int) wither.getLocation().getX();
                    int y = (int) wither.getLocation().getY();
                    int z = (int) wither.getLocation().getZ();
                    for (int i = -3; i < 4; i++) {
                        for (int j = -3; j < 4; j++) {
                            for (int k = -3; k < 4; k++) {
                                hungerGamesWorld().getBlockAt(x + i, y + j, z + k).setType(Material.AIR);
                            }
                        }
                    }
                    cultist.sendMessage(String.format("%sYour ritual was successful and you've summoned a %sWither.%s", ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, ChatColor.RESET));
                }
                broadcastToHungerGamesParticipants(String.format("%sA Cultist has completed their ritual. May God have mercy on our souls...%s", ChatColor.RED, ChatColor.RESET));
            }
        }
    }

    private void updateEnderDragonPhase(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            Collection<EnderDragon> dragons = Objects.requireNonNull(hungerGamesWorld()).getEntitiesByClass(EnderDragon.class);
            for (EnderDragon dragon : dragons) {
                int index = (int) (Math.random() * phases.size());
                dragon.setPhase(phases.get(index));
            }
        }, SEC_BEFORE_DRAGON_SWITCHES_PHASE /*<-- the initial delay */, 20L * SEC_BEFORE_DRAGON_SWITCHES_PHASE /*<-- the interval */));
    }
}
