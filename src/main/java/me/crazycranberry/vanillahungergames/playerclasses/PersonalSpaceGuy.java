package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.EnchantmentManager.getEnchantmentObject;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class PersonalSpaceGuy implements PlayerClass {
    @Override
    public String getName() {
        return "PersonalSpaceGuy";
    }

    @Override
    public String getInfo() {
        return "PersonalSpaceGuy start the tourney with a super Knock Back stick. Stay out of my personal space!";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack knockbackStick = new ItemStack(Material.STICK, 1);
                knockbackStick.addEnchantment(getEnchantmentObject("BigKnockBack"), 5);
                knockbackStick.addEnchantment(Enchantment.VANISHING_CURSE, Enchantment.VANISHING_CURSE.getMaxLevel());
                ItemMeta meta = knockbackStick.getItemMeta();
                assert meta != null;
                meta.setLore(List.of("Get out of my personal space!"));
                knockbackStick.setItemMeta(meta);
                p.getPlayer().getInventory().addItem(knockbackStick);
            }
        }
    }
}
