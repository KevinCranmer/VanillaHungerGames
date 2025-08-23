package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Material;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class SkyJockey implements PlayerClass {
    private static final List<Material> harnesses = List.of(
        Material.WHITE_HARNESS,
        Material.ORANGE_HARNESS,
        Material.MAGENTA_HARNESS,
        Material.LIGHT_BLUE_HARNESS,
        Material.YELLOW_HARNESS,
        Material.LIME_HARNESS,
        Material.PINK_HARNESS,
        Material.GRAY_HARNESS,
        Material.LIGHT_GRAY_HARNESS,
        Material.CYAN_HARNESS,
        Material.PURPLE_HARNESS,
        Material.BLUE_HARNESS,
        Material.BROWN_HARNESS,
        Material.GREEN_HARNESS,
        Material.RED_HARNESS,
        Material.BLACK_HARNESS
    );

    @Override
    public String getName() {
        return "Sky Jockey";
    }

    @Override
    public String getInfo() {
        return "Sky Jockey's start the game with a harness and a Happy Ghast egg.";
    }

    @Override
    public Material menuIcon() {
        return Material.BROWN_HARNESS;
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                int index = (int) (Math.random() * harnesses.size());
                p.getPlayer().getInventory().addItem(new ItemStack(harnesses.get(index), 1));
                p.getPlayer().getInventory().addItem(new ItemStack(Material.HAPPY_GHAST_SPAWN_EGG, 1));
            }
        }
    }
}
