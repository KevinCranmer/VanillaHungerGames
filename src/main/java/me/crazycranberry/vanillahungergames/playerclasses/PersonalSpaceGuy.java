package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.customenchantments.BigKnockBack;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getCustomEnchantment;
import static me.crazycranberry.vanillahungergames.VanillaHungerGames.logger;
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
                Enchantment bigKnockBack = getCustomEnchantment("bigknockback");
                ItemMeta meta = knockbackStick.getItemMeta();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.set(bigKnockBack.getKey(), PersistentDataType.INTEGER, 5);
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
        Enchantment ench = getCustomEnchantment("bigknockback");
        if (!(ench instanceof BigKnockBack bigKnockBack)) {
            logger().warning("Somehow getCustomEnchantment(\"bigknockback\") return an Enchantment of type: " + (ench.getClass() == null ? null : ench.getClass()));
            return;
        }
        ItemStack itemStack = event.getItem().getItemStack();
        ItemMeta itemMeta = event.getItem().getItemStack().getItemMeta();
        if (itemMeta != null && itemMeta.getPersistentDataContainer().has(bigKnockBack.getKey())  && event.getEntity() instanceof Player && !isCorrectClass((Player) event.getEntity())) {
            itemMeta.getPersistentDataContainer().remove(bigKnockBack.getKey());
            itemStack.setItemMeta(itemMeta);
        }
        if (event.getItem().getItemStack().getType().equals(Material.STICK) &&
              event.getEntity() instanceof Player &&
              isCorrectClass((Player) event.getEntity()) &&
              itemMeta != null &&
              itemMeta.getLore() != null &&
              itemMeta.getLore().contains(PERSONAL_SPACE_LORE)) {
                itemMeta.getPersistentDataContainer().set(bigKnockBack.getKey(), PersistentDataType.INTEGER, 5);
                itemStack.setItemMeta(itemMeta);
        }
    }
}
