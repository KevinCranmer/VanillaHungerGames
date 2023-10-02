package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Tamer implements PlayerClass {
    @Override
    public String getName() {
        return "Tamer";
    }

    @Override
    public String getInfo() {
        return "Tamers always successfully tame wolves and start with 3 bones, and 1 wolf egg.";
    }

    @EventHandler
    private void onTameAttempt(PlayerInteractEntityEvent event) {
        if (isCorrectClass(event.getPlayer()) && event.getPlayer().getWorld().equals(hungerGamesWorld()) && event.getRightClicked().getType() == EntityType.WOLF && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BONE) {
            Wolf wolf = (Wolf) event.getRightClicked();
            wolf.setOwner(event.getPlayer());
            wolf.setTamed(true);
            ItemStack bone = event.getPlayer().getInventory().getItemInMainHand();
            bone.setAmount(bone.getAmount() - 1);
        }
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().getInventory().addItem(new ItemStack(Material.BONE, 3));
                p.getPlayer().getInventory().addItem(new ItemStack(Material.WOLF_SPAWN_EGG, 1));
            }
        }
    }
}
