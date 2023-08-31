package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesWorldCreatedEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantLeaveTournamentEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantWonTournamentEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.hasSomeoneWonTournament;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.isTournamentParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.numAlivePlayers;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class ScoreboardManager implements Listener {
    private static Scoreboard scoreboard;

    @EventHandler
    public void onHungerGamesWorldCreate(HungerGamesWorldCreatedEvent event) {
        updateScoreboard();
    }

    @EventHandler
    public void onHungerGamesCompleted(HungerGamesCompletedEvent event) {
        for (String entry : scoreboard().getEntries()) {
            scoreboard().resetScores(entry);
        }
        scoreboard().clearSlot(DisplaySlot.SIDEBAR);
        scoreboard = null;
    }

    @EventHandler
    public void onParticipantJoin(ParticipantJoinTournamentEvent event) {
        event.getParticipant().getPlayer().setScoreboard(scoreboard());
        updateScoreboard();
    }

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().equals(hungerGamesWorld())) {
            event.getPlayer().setScoreboard(scoreboard());
        } else if (event.getPlayer().getScoreboard().equals(scoreboard())) {
            event.getPlayer().setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        }
        updateScoreboard();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updateScoreboard();
    }

    @EventHandler
    public void onParticipantLeave(ParticipantLeaveTournamentEvent event) {
        updateScoreboard();
    }

    private Scoreboard scoreboard() {
        if (scoreboard == null) {
            scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
            Objective objective = scoreboard().registerNewObjective("Players", Criteria.DUMMY, "Players");
            objective.setDisplayName("Players");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            updateScoreboard();
        }
        return scoreboard;
    }

    private void updateScoreboard() {
        int alivePlayers = numAlivePlayers();
        Objective objective = scoreboard().getObjective("Players");
        objective.getScore("Alive").setScore(alivePlayers);
        objective.getScore("Spectating").setScore(tournamentParticipants().size() - alivePlayers);
        if (tournamentInProgress() && alivePlayers == 1) {
            for (Participant p : tournamentParticipants()) {
                if (p.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && p.getPlayer().getWorld().equals(hungerGamesWorld()) && !hasSomeoneWonTournament()) {
                    Bukkit.getPluginManager().callEvent(new ParticipantWonTournamentEvent(p));
                    break;
                }
            }
        } else if (tournamentInProgress() && alivePlayers < 1 && !hasSomeoneWonTournament()) {
            //no idea how this would happen but reset anyway
            Bukkit.getPluginManager().callEvent(new HungerGamesCompletedEvent());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getWorld().equals(hungerGamesWorld())) {
            updateScoreboard();
        }
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (isTournamentParticipant(event.getPlayer())) {
            updateScoreboard();
        }
    }
}
