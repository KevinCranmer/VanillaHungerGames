package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.customitems.HuntingCompass;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.InvincibilityEndedEvent;
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
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

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
        event.getPlayer().setInvulnerable(false); // delete me
        if (tournamentInProgress() && isTournamentParticipant(event.getPlayer())) {
            //if they dc'd while in a tournament, sucks to suck but put them in spectator (no combat logging)
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().teleport(hungerGamesWorld().getSpawnLocation());
        } else if (isTournamentParticipant(event.getPlayer())) {
            //if they dc'd while in the lobby, bring them back to starting point
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
        } else if (hungerGamesWorld() == null && configFile(event.getPlayer()).exists()){
            //when a player doesn't respawn after dying in the hunger games and the tournament has already ended
            restoreStartingWorldConfig(event.getPlayer());
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
                restoreStartingWorldConfig(participant.getPlayer());
                tournamentParticipants.remove(participant);
            }
        }
        for (Participant participant : tournamentParticipants()) {
            System.out.println("[VanillaHungerGames] Uh oh " + participant.getPlayer().getDisplayName() + " got stuck in the hunger games after it ended. We'll attempt to restore their config when they do respawn.");
            tournamentParticipants.remove(participant);
        }
        Bukkit.getPluginManager().callEvent(new TournamentEmptiedEvent());
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
