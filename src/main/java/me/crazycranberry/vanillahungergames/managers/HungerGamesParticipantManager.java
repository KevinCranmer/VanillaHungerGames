package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.customitems.HuntingCompass;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.InvincibilityEndedEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantLeaveTournamentEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.vanillahungergames.customitems.HuntingCompass.pointCompassToNearestPlayer;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.spawnLoc;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.restoreStartingWorldConfig;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.saveStartingWorldConfig;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.startingWorldConfigExists;

public class HungerGamesParticipantManager implements Listener {
    private static List<Participant> tournamentParticipants = new ArrayList<>();

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        event.getPlayer().setInvulnerable(false); // delete me
        if (tournamentInProgress() && isTournamentParticipant(event.getPlayer())) {
            //if they dc'd while in a tournament, sucks to suck but put them in spectator (no combat logging)
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().teleport(hungerGamesWorld().getSpawnLocation());
        } else if (startingWorldConfigExists(event.getPlayer())) {
            //Server crashed mid-tourney or they left during tourney lobby and they're startingWorldConfig still exists, gotta load it up for them
            System.out.println("[VanillaHungerGames] Tourney crash recovery initiated for " + event.getPlayer().getDisplayName());
            restoreStartingWorldConfig(event.getPlayer());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isTournamentParticipant(event.getPlayer()) && hungerGamesWorld() != null) {
            event.setRespawnLocation(event.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onParticipantJoinTournament(ParticipantJoinTournamentEvent event) {
        if (hungerGamesWorld() == null) {
            return;
        }
        saveStartingWorldConfig(event.getParticipant());
        Player player = event.getParticipant().getPlayer();
        player.getInventory().clear();
        tournamentParticipants.add(event.getParticipant());
        if (tournamentInProgress()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
            player.setInvulnerable(true);
        }
        player.setLevel(69);
        player.teleport(hungerGamesWorld().getSpawnLocation());
    }

    @EventHandler
    public void onTournamentStart(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants) {
            Player player = p.getPlayer();
            player.setHealth(20);
            player.setExhaustion(0);
            player.setSaturation(1);
            player.setFoodLevel(20);
            for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
            player.setAbsorptionAmount(0);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.teleport(spawnLoc());
            player.getInventory().setItem(8, HuntingCompass.getHuntingCompass());
        }
    }

    @EventHandler
    public void onInvincibilityEnd(InvincibilityEndedEvent event) {
        for (Participant p : tournamentParticipants()) {
            p.getPlayer().setInvulnerable(false);
        }
    }

    @EventHandler
    public void onTournamentCompleted(HungerGamesCompletedEvent event) {
        sendEveryoneHomeHappy();
        tournamentParticipants = new ArrayList<>();
    }

    private void sendEveryoneHomeHappy() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(hungerGamesWorld())) {
                restoreStartingWorldConfig(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isTournamentParticipant(event.getPlayer())) {
            onParticipantLeaveTournament(new ParticipantLeaveTournamentEvent(getParticipant(event.getPlayer())));
        }
    }

    @EventHandler
    public void onParticipantLeaveTournament(ParticipantLeaveTournamentEvent event) {
        restoreStartingWorldConfig(event.getParticipant().getPlayer());
        if (!event.getParticipant().getPlayer().getWorld().equals(hungerGamesWorld())) {
            // successfully brought the player back to their world
            tournamentParticipants.remove(event.getParticipant());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getWorld().equals(hungerGamesWorld())) {
            event.getEntity().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!tournamentInProgress() && event.getPlayer().getWorld().equals(hungerGamesWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().equals(hungerGamesWorld()) && event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
            pointCompassToNearestPlayer(event.getItem(), event.getPlayer());
        }
    }

    public static List<Participant> tournamentParticipants() {
        return List.copyOf(tournamentParticipants);
    }

    public static boolean isTournamentParticipant(Player p) {
        return tournamentParticipants().stream().anyMatch(participant -> participant.getPlayer().equals(p));
    }

    public static Participant getParticipant(Player p) {
        return tournamentParticipants().stream().filter(participant -> participant.getPlayer().equals(p)).findFirst().orElse(null);
    }

    public static int numAlivePlayers() {
        int alivePlayers = 0;
        for (Participant p : tournamentParticipants()) {
            if (p.getPlayer().getGameMode() != GameMode.SPECTATOR) {
                alivePlayers++;
            }
        }
        return alivePlayers;
    }
}
