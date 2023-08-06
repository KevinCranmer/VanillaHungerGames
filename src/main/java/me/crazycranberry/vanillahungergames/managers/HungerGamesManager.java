package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesEvent;
import me.crazycranberry.vanillahungergames.HungerGamesEventList;
import me.crazycranberry.vanillahungergames.events.HungerGamesWorldCreatedEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantWonTournamentEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class HungerGamesManager implements Listener {
    private static List<HungerGamesEvent> events;
    private int eventIndex = 0;
    private static boolean playerHasJoined = false;
    private static boolean tournamentEnded = false;
    private static boolean tournamentInProgress = false;

    @EventHandler
    void onHungerGamesEvent(HungerGamesEvent event) {
        if (event.getEventIndex() > eventIndex || tournamentEnded) {
            //A tournament finished and a new tournament started before this event ever executed
            //or
            //The tournament has ended.
            //This event should DIE.
            return;
        }
        if (event.getEventToTrigger() != null) {
            Bukkit.getPluginManager().callEvent(event.getEventToTrigger());
        }
        broadcastToHungerGamesParticipants(event.getMessage());
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            Bukkit.getPluginManager().callEvent(events().get(eventIndex++));
                            return true;
                        });
                    }
                },
                event.getMsTillNextEvent()
        );
    }

    @EventHandler
    void onTournamentCompleted(HungerGamesCompletedEvent event) {
        eventIndex = 0;
        playerHasJoined = false;
        tournamentEnded = true;
        tournamentInProgress = false;
    }

    public static boolean hasSomeoneWonTournament() {
        return tournamentEnded;
    }

    @EventHandler
    void onVictoryRoyale(ParticipantWonTournamentEvent event) {
        tournamentEnded = true;
        if (event.getParticipant() != null) {
            Bukkit.getServer().broadcastMessage(String.format("%sCongratulations to %s for winning the hunger games!%s", ChatColor.LIGHT_PURPLE, event.getParticipant().getPlayer().getDisplayName(), ChatColor.RESET));
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                                Bukkit.getPluginManager().callEvent(new HungerGamesCompletedEvent());
                                return true;
                            });
                        }
                    },
                    30000
            );
        }
    }

    private List<HungerGamesEvent> events() {
        if (events == null) {
            events = HungerGamesEventList.events();
        }
        return events;
    }

    @EventHandler
    void onHungerGamesWorldCreate(HungerGamesWorldCreatedEvent event) {
        eventIndex = 0;
        playerHasJoined = false;
        tournamentEnded = false;
        tournamentInProgress = false;
    }

    @EventHandler
    void onPlayerJoinTournament(ParticipantJoinTournamentEvent event) {
        if (!playerHasJoined) {
            playerHasJoined = true;
            Bukkit.getPluginManager().callEvent(events().get(eventIndex++));
        }
    }

    @EventHandler
    public static void tournamentStarted(TournamentStartedEvent event) {
        tournamentInProgress = true;
        if (tournamentParticipants().isEmpty()) {
            Bukkit.getPluginManager().callEvent(new HungerGamesCompletedEvent());
        } else if (tournamentParticipants().size() == 1) {
            Bukkit.getPluginManager().callEvent(new ParticipantWonTournamentEvent(tournamentParticipants().get(0)));
        }
    }

    public static boolean tournamentInProgress() {
        return tournamentInProgress;
    }

    public static void broadcastToHungerGamesParticipants(String message) {
        for (Participant player : tournamentParticipants()) {
            player.getPlayer().sendMessage(message);
        }
    }
}
