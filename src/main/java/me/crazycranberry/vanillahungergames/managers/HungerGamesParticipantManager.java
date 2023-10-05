package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.customitems.HuntingCompass;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.InvincibilityEndedEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantAttemptToJoinEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantLeaveTournamentEvent;
import me.crazycranberry.vanillahungergames.events.TournamentEmptiedEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.customitems.HuntingCompass.pointCompassToNearestPlayer;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.spawnLoc;
import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.configFile;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.restoreStartingWorldConfig;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.saveStartingWorldConfig;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.startingWorldConfigExists;

public class HungerGamesParticipantManager implements Listener {
    private static List<Participant> tournamentParticipants = new ArrayList<>();

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        if (startingWorldConfigExists(event.getPlayer())) {
            //Server crashed mid-tourney or they left during tourney and they're startingWorldConfig still exists, gotta load it up for them
            System.out.println("[VanillaHungerGames] Tourney crash recovery initiated for " + event.getPlayer().getName());
            restoreStartingWorldConfig(event.getPlayer(), getParticipant(event.getPlayer()) == null ? null : getParticipant(event.getPlayer()).getStartingWorldConfig().getScoreboard());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (isTournamentParticipant(event.getPlayer()) && event.getPlayer().getWorld().equals(hungerGamesWorld())) {
            event.setRespawnLocation(event.getPlayer().getLocation());
        } else if (configFile(event.getPlayer()).exists()){
            //when a player doesn't respawn after dying in the hunger games and the tournament has already ended
            restoreStartingWorldConfig(event.getPlayer(), getParticipant(event.getPlayer()) == null ? null : getParticipant(event.getPlayer()).getStartingWorldConfig().getScoreboard());
            //idk why it happens, but if we don't switch the game mode and switch back, the player bugs out and can't move.
            //it has to do with the player trying to respawn in a world that doesn't exist, so we switch gamemodes 50ms after we've
            //teleported them to the main world
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                                GameMode gm = event.getPlayer().getGameMode();
                                event.getPlayer().setGameMode(GameMode.values()[gm.ordinal() + 1 % GameMode.values().length]);
                                event.getPlayer().setGameMode(gm);
                                return true;
                            });
                        }
                    },
                    50
            );
        }
    }

    @EventHandler
    public void onParticipantAttemptToJoinEvent(ParticipantAttemptToJoinEvent event) {
        if (hungerGamesWorld() == null) {
            return;
        }
        saveStartingWorldConfig(event.getParticipant());
        Player player = event.getParticipant().getPlayer();
        player.getInventory().clear();
        tournamentParticipants.add(event.getParticipant());
        player.teleport(hungerGamesWorld().getSpawnLocation());
        if (tournamentInProgress()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
            player.setInvulnerable(true);
        }
        player.setLevel(69);
        Bukkit.getPluginManager().callEvent(new ParticipantJoinTournamentEvent(event.getParticipant()));
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
            if (p.getPlayerClass() == null) {
                int randomClassIndex = (int) (Math.random() * possibleClasses().size());
                p.setPlayerClass(possibleClasses().get(randomClassIndex));
                p.getPlayer().sendMessage(String.format("You never selected a class, so now you've been randomly assigned a class. You got: %s%s%s", ChatColor.GREEN, p.getPlayerClass().getName(), ChatColor.RESET));
            }
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
    }

    private void sendEveryoneHomeHappy() {
        for (Participant participant : tournamentParticipants()) {
            if (!participant.getPlayer().isDead()) {
                restoreStartingWorldConfig(participant.getPlayer(), participant == null ? null : participant.getStartingWorldConfig().getScoreboard());
                tournamentParticipants.remove(participant);
            }
        }
        for (Participant participant : tournamentParticipants()) {
            System.out.println("[VanillaHungerGames] Uh oh " + participant.getPlayer().getName() + " got stuck in the hunger games after it ended. We'll attempt to restore their config when they do respawn.");
            tournamentParticipants.remove(participant);
        }
        Bukkit.getPluginManager().callEvent(new TournamentEmptiedEvent());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isTournamentParticipant(event.getPlayer())) {
            // We'll handle restoring their starting configs when they rejoin the server
            tournamentParticipants.remove(getParticipant(event.getPlayer()));
        }
    }

    @EventHandler
    public void onParticipantLeaveTournament(ParticipantLeaveTournamentEvent event) {
        tournamentParticipants.remove(event.getParticipant());
        restoreStartingWorldConfig(event.getParticipant().getPlayer(), event.getShouldTeleport(), event.getParticipant() == null ? null : event.getParticipant().getStartingWorldConfig().getScoreboard());
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
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (isTournamentParticipant(event.getPlayer()) && event.getFrom().getWorld().equals(hungerGamesWorld()) && !event.getTo().getWorld().equals(hungerGamesWorld())) {
            Bukkit.getPluginManager().callEvent(new ParticipantLeaveTournamentEvent(getParticipant(event.getPlayer()), false));
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
