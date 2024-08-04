package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Jockey implements PlayerClass {
    @Override
    public String getName() {
        return "Jockey";
    }

    @Override
    public String getInfo() {
        return "Jockey's start the game with a saddle and a horse egg. Neeeeigh!";
    }

    @Override
    public Material menuIcon() {
        return Material.SADDLE;
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().getInventory().addItem(new ItemStack(Material.SADDLE, 1));
                p.getPlayer().getInventory().addItem(new ItemStack(Material.HORSE_SPAWN_EGG, 1));
            }
        }
    }
}
