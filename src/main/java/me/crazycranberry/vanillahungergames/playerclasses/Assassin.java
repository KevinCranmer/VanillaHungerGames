package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Assassin implements PlayerClass{
    private static final int INVISIBLE_DURATION_SEC = 5;
    private static final int INVISIBLE_COOLDOWN_SEC = 15;

    @Override
    public String getName() {
        return "Assassin";
    }

    @Override
    public String getInfo() {
        return "Assassins can go invisible for " + INVISIBLE_DURATION_SEC + " seconds every " + INVISIBLE_COOLDOWN_SEC + " seconds.";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack membrane = new ItemStack(Material.PHANTOM_MEMBRANE, 1);
                ItemMeta meta = membrane.getItemMeta();
                meta.setLore(List.of(String.format("%sUse this item for %s seconds of invisibility.%s", ChatColor.GOLD, INVISIBLE_DURATION_SEC, ChatColor.RESET), String.format("%s%s second cooldown.%s", ChatColor.GRAY, INVISIBLE_COOLDOWN_SEC, ChatColor.RESET)));
                membrane.setItemMeta(meta);
                p.getPlayer().getInventory().addItem(membrane);
            }
        }
    }

    @EventHandler
    public void onMembraneUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isCorrectClass(player) && item != null && item.getType() == Material.PHANTOM_MEMBRANE && !player.hasCooldown(Material.PHANTOM_MEMBRANE)) {
            player.setCooldown(Material.PHANTOM_MEMBRANE, 20 * INVISIBLE_COOLDOWN_SEC);
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * INVISIBLE_DURATION_SEC, 0, true, true));
        }
    }
}
