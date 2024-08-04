package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class Stomper implements PlayerClass {
    @Override
    public String getName() {
        return "Stomper";
    }

    @Override
    public String getInfo() {
        return "Stompers transfer fall damage to nearby Players (or mobs)";
    }

    @Override
    public Material menuIcon() {
        return Material.ANVIL;
    }

    @EventHandler
    private void fallDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isCorrectClass(player) && isInHungerGamesWorld(player.getWorld())) {
                double damage = event.getDamage();
                event.setDamage(0);
                event.setCancelled(true);
                for (Entity e : player.getNearbyEntities(3.5, 3.5, 3.5)) {
                    if (e instanceof LivingEntity) {
                        ((LivingEntity)e).damage(damage, player);
                    }
                }
            }
        }
    }
}
