package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Cleric extends PlayerClassWithRecurringTasks implements PlayerClass{
    private static final int HEAL_COOLDOWN = 25;
    private static final double HEAL_DURATION = 7.5;
    private static final double HEALING_RADIUS = 4;
    private static final double HEAL_AMOUNT_PER_HEAL = 0.55;
    private static final long TICKS_BETWEEN_HEALS = 10;
    private static final List<Vector> fireworkAdditionVector = List.of(
            new Vector(HEALING_RADIUS, 0, HEALING_RADIUS),
            new Vector(-HEALING_RADIUS, 0, HEALING_RADIUS),
            new Vector(HEALING_RADIUS, 0, -HEALING_RADIUS),
            new Vector(-HEALING_RADIUS, 0, -HEALING_RADIUS)
    );
    private Map<Player, ClericHealInfo> clerics = new HashMap<>();


    @Override
    public String getName() {
        return "Cleric";
    }

    @Override
    public String getInfo() {
        return "Clerics have the ability to heal themselves and nearby players.";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        boolean clericExists = false;
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack gold = new ItemStack(Material.GOLD_NUGGET, 1);
                ItemMeta meta = gold.getItemMeta();
                meta.setLore(List.of(String.format("%sUse this item to create a healing aura for %s seconds. Moving breaks the aura.%s", ChatColor.GOLD, HEAL_DURATION, ChatColor.RESET), String.format("%s%s second cooldown.%s", ChatColor.GRAY, HEAL_COOLDOWN, ChatColor.RESET)));
                gold.setItemMeta(meta);
                p.getPlayer().getInventory().addItem(gold);
                if (!clericExists) {
                    clericExists = true;
                    checkForHealing(getPlugin());
                }
            }
        }
    }

    @EventHandler
    public void onGoldNuggetUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isCorrectClass(player) && item != null && item.getType() == Material.GOLD_NUGGET) {
            player.setCooldown(Material.GOLD_NUGGET, 20 * HEAL_COOLDOWN);
            clerics.put(player, new ClericHealInfo(HEAL_DURATION, player.getLocation()));
        }
    }

    private void checkForHealing(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, this::heal, 0, TICKS_BETWEEN_HEALS));
    }

    private void heal() {
        for (Player p : clerics.keySet()) {
            if (isCorrectClass(p) && clerics.get(p).getHealTimeRemaining() > 0 && clerics.get(p).locMatches(p.getLocation())) {
                for (Entity e : p.getNearbyEntities(HEALING_RADIUS, HEALING_RADIUS, HEALING_RADIUS)) {
                    if (e instanceof LivingEntity) {
                        LivingEntity entityToBeHealed = (LivingEntity) e;
                        entityToBeHealed.setHealth(Math.min(entityToBeHealed.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), entityToBeHealed.getHealth() + HEAL_AMOUNT_PER_HEAL));
                    }
                }
                p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), p.getHealth() + HEAL_AMOUNT_PER_HEAL));
                clerics.get(p).decreaseHealthTimeRemaining(((double) TICKS_BETWEEN_HEALS) / 20L);
                summonFireWorks(p.getLocation());
            } else {
                clerics.remove(p);
            }
        }
    }

    private void summonFireWorks(Location location) {
        for (Vector v : fireworkAdditionVector) {
            Location spawnLoc = location.clone().add(v);
            Firework fw = (Firework) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(1);
            fwm.addEffect(FireworkEffect.builder().withColor(Color.RED).flicker(true).build());
            fwm.setDisplayName("ClericFirework");
            fw.setFireworkMeta(fwm);
            fw.detonate();
        }
    }

    @EventHandler
    private void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.getFireworkMeta().getDisplayName().equalsIgnoreCase("ClericFirework")) {
                event.setCancelled(true);
            }
        }
    }

    private static class ClericHealInfo {
        private Double healTimeRemaining;
        private Double locX;
        private Double locY;
        private Double locZ;

        public ClericHealInfo(Double healTimeRemaining, Location loc) {
            this.healTimeRemaining = healTimeRemaining;
            this.locX = loc.getX();
            this.locY = loc.getY();
            this.locZ = loc.getZ();
        }

        public Double getHealTimeRemaining() {
            return healTimeRemaining;
        }

        public void decreaseHealthTimeRemaining(Double amountToDecreaseBy) {
            healTimeRemaining -= amountToDecreaseBy;
        }

        public boolean locMatches(Location loc) {
            return locX == loc.getX() && locY == loc.getY() && locZ == loc.getZ();
        }
    }
}
