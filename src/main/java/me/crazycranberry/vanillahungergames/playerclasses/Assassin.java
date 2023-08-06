package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Assassin implements PlayerClass{
    @Override
    public String getName() {
        return "Assassin";
    }

    @Override
    public String getInfo() {
        return "Assassins gain the invisibility potion effect.";
    }

    @EventHandler
    private void giveInvisibility(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 3600, 0, true, true));
            }
        }
    }
}
