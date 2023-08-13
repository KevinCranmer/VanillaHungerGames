package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Snowable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Snowman extends PlayerClassWithRecurringTasks implements PlayerClass {
    Plugin plugin;

    public Snowman() {
        this.plugin = getPlugin();
    }

    public String getName() {
        return "Snowman";
    }

    public String getInfo() {
        return "Snowmen run faster on snow, and snowballs now deal damage.";
    }

    @EventHandler
    private void anySnowman(TournamentStartedEvent event){
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                checkForSnowWalking(plugin);
                return;
            }
        }
    }

    @EventHandler
    private void snowballHit(EntityDamageByEntityEvent event) {
        if ("Snowman".equals(event.getDamager().getCustomName())) {
            event.setDamage(1.5);
        }
    }

    @EventHandler
    private void snowballThrown(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (isCorrectClass(shooter) && shooter.getWorld().equals(hungerGamesWorld())) {
                event.getEntity().setCustomName("Snowman");
            }
        }
    }

    @EventHandler
    private void onSuffocatingInSnow(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && isCorrectClass((Player) event.getEntity())) {
            Player player = (Player) event.getEntity();
            if ((event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION) && hungerGamesWorld().getBlockAt(player.getEyeLocation()).getType().equals(Material.POWDER_SNOW))
                || event.getCause().equals(EntityDamageEvent.DamageCause.FREEZE)) {
                event.setCancelled(true);
            }
        }
    }

    private void checkForSnowWalking(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            for (Participant p : tournamentParticipants()) {
                Block blockUnderPlayer = p.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (p.getPlayer().getWorld().equals(hungerGamesWorld()) &&
                        isCorrectClass(p.getPlayer()) &&
                        (blockUnderPlayer.getType() == Material.SNOW ||
                        blockUnderPlayer.getType() == Material.SNOW_BLOCK ||
                        blockUnderPlayer.getType() == Material.POWDER_SNOW ||
                            (blockUnderPlayer.getBlockData() instanceof Snowable && ((Snowable) blockUnderPlayer.getBlockData()).isSnowy()))) {
                    p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3, 0));
                }
            }
        }, 0 /*<-- the initial delay */, 20L * 2L /*<-- the interval */));
    }
}
