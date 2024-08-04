package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.commands.ClassInfoCommand.CLASS_INFO_CHAT_COLOR;
import static me.crazycranberry.vanillahungergames.customitems.HuntingCompass.getPlayersHuntingCompassMeta;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class Hunter extends PlayerClassWithRecurringTasks implements PlayerClass {
    Plugin plugin;

    public Hunter() {
        this.plugin = getPlugin();
    }

    @Override
    public String getName() {
        return "Hunter";
    }

    @Override
    public String getInfo() {
        return String.format("A Hunter's compass %salways%s updates and Hunters get a speed boost while moving in the compasses direction.", ChatColor.ITALIC, CLASS_INFO_CHAT_COLOR);
    }

    @Override
    public Material menuIcon() {
        return Material.COMPASS;
    }

    @EventHandler
    private void anyHunters(TournamentStartedEvent event){
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                checkForFacingCompassDirection(plugin);
                return;
            }
        }
    }

    private void checkForFacingCompassDirection(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            for (Participant p : tournamentParticipants()) {
                if(isInHungerGamesWorld(p.getPlayer().getWorld()) && isCorrectClass(p.getPlayer())) {
                    CompassMeta compassMeta = getPlayersHuntingCompassMeta(p.getPlayer());
                    if (compassMeta != null &&
                            compassMeta.getLodestone() != null &&
                            isFacingCompassDirection(p.getPlayer(), compassMeta.getLodestone())) {
                        p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3, 0));
                    }
                }
            }
        }, 0 /*<-- the initial delay */, 20L * 2L /*<-- the interval */));
    }

    private boolean isFacingCompassDirection(Player compassHolder, Location compassTargetLocation) {
        Location ctl = compassTargetLocation.clone();
        ctl.setY(0);
        Location chl = compassHolder.getLocation().clone();
        chl.setY(0);
        Vector locationDifference = ctl.toVector().subtract(chl.toVector());
        Vector compassHolderDirection = chl.getDirection();
        compassHolderDirection.setY(0);
        locationDifference.normalize();
        Vector difference = locationDifference.subtract(compassHolderDirection);
        return difference.lengthSquared() <= 0.1;
    }
}
