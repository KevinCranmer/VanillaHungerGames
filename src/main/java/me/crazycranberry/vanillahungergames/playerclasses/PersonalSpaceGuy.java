package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.EnchantmentManager.getEnchantmentObject;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class PersonalSpaceGuy implements PlayerClass {
    private static final String PERSONAL_SPACE_LORE = "Get out of my personal space!";

    @Override
    public String getName() {
        return "PersonalSpaceGuy";
    }

    @Override
    public String getInfo() {
        return "PersonalSpaceGuy start the tourney with a super Knock Back stick. Stay out of my personal space!";
    }

    @EventHandler
    public void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack knockbackStick = new ItemStack(Material.STICK, 1);
                knockbackStick.addEnchantment(getEnchantmentObject("BigKnockBack"), 5);
                ItemMeta meta = knockbackStick.getItemMeta();
                assert meta != null;
                meta.setLore(List.of(PERSONAL_SPACE_LORE));
                knockbackStick.setItemMeta(meta);
                p.getPlayer().getInventory().addItem(knockbackStick);
            }
        }
    }

    @EventHandler
    //Only the PersonalSpaceGuy can demand his PersonalSpace
    public void onStickPickup(EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack.getEnchantments().containsKey(getEnchantmentObject("BigKnockBack")) && event.getEntity() instanceof Player && !isCorrectClass((Player) event.getEntity())) {
            event.getItem().getItemStack().removeEnchantment(getEnchantmentObject("BigKnockBack"));
        }
        if (itemStack.getType().equals(Material.STICK) && event.getEntity() instanceof Player && isCorrectClass((Player) event.getEntity())) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null && meta.getLore() != null && meta.getLore().contains(PERSONAL_SPACE_LORE)) {
                itemStack.addEnchantment(getEnchantmentObject("BigKnockBack"), 5);
            }
        }
    }
}
