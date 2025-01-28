package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class Gambler implements PlayerClass {
    private static final double ODDS_FOR_POSITIVE_EFFECT = 0.7;

    private static final List<GamblerPotion> badPotionEffects = List.of(
            new GamblerPotion(PotionEffectType.POISON, 0, 30),
            new GamblerPotion(PotionEffectType.BLINDNESS, 0, 3600),
            new GamblerPotion(PotionEffectType.NAUSEA, 0, 3600),
            new GamblerPotion(PotionEffectType.DARKNESS, 0, 3600),
            new GamblerPotion(PotionEffectType.INSTANT_DAMAGE, 0, 0),
            new GamblerPotion(PotionEffectType.HUNGER, 0, 3600),
            new GamblerPotion(PotionEffectType.LEVITATION, 0, 10),
            new GamblerPotion(PotionEffectType.SLOWNESS, 0, 3600),
            new GamblerPotion(PotionEffectType.MINING_FATIGUE, 0, 3600),
            new GamblerPotion(PotionEffectType.WEAKNESS, 0, 3600),
            new GamblerPotion(PotionEffectType.WITHER, 0, 30),
            new GamblerPotion(PotionEffectType.GLOWING, 0, 3600));

    private static final List<GamblerPotion> goodPotionEffects = List.of(
            new GamblerPotion(PotionEffectType.SPEED, 0, 3600),
            new GamblerPotion(PotionEffectType.HASTE, 0, 3600),
            new GamblerPotion(PotionEffectType.STRENGTH, 0, 3600),
            new GamblerPotion(PotionEffectType.INSTANT_HEALTH, 0, 0),
            new GamblerPotion(PotionEffectType.JUMP_BOOST, 1, 3600),
            new GamblerPotion(PotionEffectType.REGENERATION, 0, 3600),
            new GamblerPotion(PotionEffectType.RESISTANCE, 0, 3600),
            new GamblerPotion(PotionEffectType.FIRE_RESISTANCE, 0, 3600),
            new GamblerPotion(PotionEffectType.WATER_BREATHING, 0, 3600),
            new GamblerPotion(PotionEffectType.INVISIBILITY, 0, 3600),
            new GamblerPotion(PotionEffectType.NIGHT_VISION, 0, 3600),
            new GamblerPotion(PotionEffectType.HEALTH_BOOST, 0, 0),
            new GamblerPotion(PotionEffectType.ABSORPTION, 0, 3600),
            new GamblerPotion(PotionEffectType.SATURATION, 0, 3600),
            new GamblerPotion(PotionEffectType.SLOW_FALLING, 0, 3600),
            new GamblerPotion(PotionEffectType.CONDUIT_POWER, 0, 3600),
            new GamblerPotion(PotionEffectType.DOLPHINS_GRACE, 0, 3600));

    @Override
    public String getName() {
        return "Gambler";
    }

    @Override
    public String getInfo() {
        return "Gambler's get a random potion effect on every player kill (weighted to be more likely a positive one).";
    }

    @Override
    public Material menuIcon() {
        return Material.POTION;
    }

    @EventHandler
    private void givePotionEffect(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null && isInHungerGamesWorld(event.getEntity().getKiller().getWorld()) && isCorrectClass(event.getEntity().getKiller())) {
            Player gambler = event.getEntity().getKiller();
            List<GamblerPotion> potionMap = badPotionEffects;
            if (Math.random() < ODDS_FOR_POSITIVE_EFFECT) {
                potionMap = goodPotionEffects;
            }
            int index = (int) (Math.random() * potionMap.size());
            GamblerPotion potion = potionMap.get(index);
            gambler.addPotionEffect(new PotionEffect(potion.potion(), potion.secondsToLast() * 20, potion.amplifier()));
            applyPossibleInstantEffects(gambler, potion);
            gambler.sendMessage(String.format("%sYou have been granted the following potion effect: %s%s%s", ChatColor.GRAY, ChatColor.GOLD, potion.potion().getName(), ChatColor.RESET));
        }
    }

    private void applyPossibleInstantEffects(Player gambler, GamblerPotion potion) {
        if (potion.potion().equals(PotionEffectType.INSTANT_HEALTH)) {
            gambler.setHealth(Math.min(gambler.getHealth() + 8, gambler.getAttribute(Attribute.MAX_HEALTH).getValue()));
        } else if (potion.potion().equals(PotionEffectType.HEALTH_BOOST)) {
            gambler.setAbsorptionAmount(gambler.getAbsorptionAmount() + 6);
        } else if (potion.potion().equals(PotionEffectType.INSTANT_DAMAGE)) {
            gambler.damage(6);
        }
    }

    private static class GamblerPotion {
        private final PotionEffectType potion;
        private final int amplifier;
        private final int secondsToLast;

        public GamblerPotion(PotionEffectType potion, int amplifier, int secondsToLast) {
            this.potion = potion;
            this.amplifier = amplifier;
            this.secondsToLast = secondsToLast;
        }

        public PotionEffectType potion() {
            return this.potion;
        }

        public int amplifier() {
            return this.amplifier;
        }

        public int secondsToLast() {
            return this.secondsToLast;
        }
    }
}
