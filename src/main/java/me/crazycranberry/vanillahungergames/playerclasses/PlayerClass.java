package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.getParticipant;

public interface PlayerClass extends Listener {
    public String getName();
    public String getInfo();
    public Material menuIcon();

    default boolean isCorrectClass(Player player) {
        Participant participant = getParticipant(player);
        return participant != null && this.equals(participant.getPlayerClass());
    }
}
