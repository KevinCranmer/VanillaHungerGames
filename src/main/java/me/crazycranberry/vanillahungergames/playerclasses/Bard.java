package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class Bard implements PlayerClass {
    private final int HORN_COOLDOWN_SECONDS = 17;
    private final int HORN_BUFF_DURATION_SECONDS = 5;
    private final List<String> HORN_TYPES = List.of("ponder_goat_horn", "sing_goat_horn", "yearn_goat_horn", "dream_goat_horn");

    @Override
    public String getName() {
        return "Bard";
    }

    @Override
    public String getInfo() {
        return "Bards get " + HORN_TYPES.size() + " different Goat Horns that grant them various effects.";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                for (MusicInstrument instrument : bardHorns()) {
                    ItemStack item = new ItemStack(Material.GOAT_HORN, 1);
                    MusicInstrumentMeta meta = (MusicInstrumentMeta) item.getItemMeta();
                    meta.setInstrument(instrument);
                    meta.setLore(List.of(String.format("%s%s%s", ChatColor.GOLD, hornLoreString(instrument.getKey().getKey()), ChatColor.RESET)));
                    item.setItemMeta(meta);
                    p.getPlayer().getInventory().addItem(item);
                }
            }
        }
    }

    @EventHandler
    public void onGoatHornSound(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isInHungerGamesWorld(event.getPlayer().getWorld()) && item != null && item.getType() == Material.GOAT_HORN && !player.hasCooldown(Material.GOAT_HORN)) {
            MusicInstrumentMeta meta = (MusicInstrumentMeta) item.getItemMeta();
            if (meta != null && meta.getInstrument() != null) {
                MusicInstrument horn = meta.getInstrument();
                hungerGamesWorld().playSound(player.getLocation(), hornSound(horn.getKey().getKey()), 1, 1);
                if (isCorrectClass(player)) {
                    player.setCooldown(Material.GOAT_HORN, 20 * HORN_COOLDOWN_SECONDS);
                    applyBardEffect(player, horn.getKey().getKey());
                }
            }
        }
    }

    private List<MusicInstrument> bardHorns() {
        return MusicInstrument.values().stream().filter(i -> HORN_TYPES.contains(i.getKey().getKey())).toList();
    }

    private String hornLoreString(String hornType) {
        switch(hornType) {
            case "ponder_goat_horn":
                return "Haste Horn";
            case "sing_goat_horn":
                return "Heal Horn";
            case "yearn_goat_horn":
                return "Strength Horn";
            case "dream_goat_horn":
                return "Speed Horn";
            default:
                return "";
        }
    }

    private Sound hornSound(String hornType) {
        switch(hornType) {
            case "ponder_goat_horn":
                return Sound.ITEM_GOAT_HORN_SOUND_0;
            case "sing_goat_horn":
                return Sound.ITEM_GOAT_HORN_SOUND_1;
            case "yearn_goat_horn":
                return Sound.ITEM_GOAT_HORN_SOUND_6;
            case "dream_goat_horn":
                return Sound.ITEM_GOAT_HORN_SOUND_7;
            default:
                return Sound.ITEM_GOAT_HORN_PLAY;
        }
    }

    private void applyBardEffect(Player bard, String hornType) {
        switch (hornType) {
            case "ponder_goat_horn":
                bard.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * HORN_BUFF_DURATION_SECONDS, 1));
                break;
            case "sing_goat_horn":
                bard.setHealth(Math.min(bard.getHealth() + 1.5, bard.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                break;
            case "yearn_goat_horn":
                bard.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * HORN_BUFF_DURATION_SECONDS, 1));
                break;
            case "dream_goat_horn":
                bard.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * HORN_BUFF_DURATION_SECONDS, 1));
                break;
        }
    }
}
