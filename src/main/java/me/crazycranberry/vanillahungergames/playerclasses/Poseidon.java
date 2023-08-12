package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Poseidon extends PlayerClassWithRecurringTasks implements PlayerClass {
    Plugin plugin;


    public Poseidon() {
        this.plugin = getPlugin();
    }

    @Override
    public String getName() {
        return "Poseidon";
    }

    @Override
    public String getInfo() {
        return "Poseidon move/mine faster in water and their breath slowly regenerates (you can still drown, I couldn't figure out how to code respiration without a helmet).";
    }

    @EventHandler
    private void anyPoseidon(TournamentStartedEvent event){
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                checkForUnderwater(plugin);
                giveMoreAir(plugin);
                return;
            }
        }
    }

    @EventHandler
    private void giveDolphinsGrace(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 3600, 0, true, true));
            }
        }
    }

    private void checkForUnderwater(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            for (Participant p : tournamentParticipants()) {
                if (p.getPlayer().getWorld().equals(hungerGamesWorld()) && isCorrectClass(p.getPlayer()) && p.getPlayer().isInWater()) {
                    p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 3, 2));
                }
            }

        }, 0 /*<-- the initial delay */, 20L * 2L /*<-- the interval */));
    }

    private void giveMoreAir(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            for (Participant p : tournamentParticipants()) {
                if (p.getPlayer().getWorld().equals(hungerGamesWorld()) && isCorrectClass(p.getPlayer()) && p.getPlayer().isInWater()) {
                    p.getPlayer().setRemainingAir(p.getPlayer().getRemainingAir() + 24);
                }
            }
        }, 0 /*<-- the initial delay */, 20L * 3L /*<-- the interval */));
    }
}
