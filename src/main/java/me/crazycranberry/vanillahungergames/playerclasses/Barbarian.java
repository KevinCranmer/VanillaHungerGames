package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.attribute.Attribute;

public class Barbarian implements PlayerClass {
    private final static double BASE_SCALE = 1.04;

    @Override
    public String getName() {
        return "Barbarian";
    }

    @Override
    public String getInfo() {
        return "Barbarian's do more damage the less health they have.";
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && isCorrectClass((Player) event.getDamager())) {
            Player barbarian = (Player) event.getDamager();
            event.setDamage(event.getDamage() * Math.pow(BASE_SCALE, barbarian.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - barbarian.getHealth()));
        }
    }
}
