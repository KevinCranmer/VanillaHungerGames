package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Archer implements PlayerClass {
    private final static int NUM_ARROWS = 16;

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public String getInfo() {
        return "Archer's start the game with a bow and " + NUM_ARROWS + " arrows.";
    }

    @Override
    public Material menuIcon() {
        return Material.BOW;
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().getInventory().addItem(new ItemStack(Material.BOW, 1));
                p.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, NUM_ARROWS));
            }
        }
    }
}
