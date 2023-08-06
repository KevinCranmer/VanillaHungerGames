package me.crazycranberry.vanillahungergames.customitems;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.Objects;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class HuntingCompass {
    public static ItemStack getHuntingCompass() {
        ItemStack huntingCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) huntingCompass.getItemMeta();
        assert meta != null;
        meta.setLodestoneTracked(false);
        meta.setLodestone(new Location(hungerGamesWorld(), 1000, 1000, 1000));
        huntingCompass.setItemMeta(meta);
        return huntingCompass;
    }

    public static void pointCompassToNearestPlayer(ItemStack compass, Player compassHolder) {
        if (tournamentParticipants().size() == 0) {
            return;
        } else if (tournamentParticipants().size() == 1) {
            compassHolder.sendMessage(String.format("%sYou've already won. There is no one else to look for.%s", ChatColor.GRAY, ChatColor.RESET));
        }
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        Location startingLoc = compassHolder.getLocation();
        Location closestLocation = tournamentParticipants().get(0).getPlayer().getLocation();
        for (Participant p : tournamentParticipants()) {
            if (p.getPlayer().getGameMode() == GameMode.SURVIVAL && !p.getPlayer().getDisplayName().equals(compassHolder.getDisplayName())) {
                Location playerLoc = p.getPlayer().getLocation();
                playerLoc.setY(startingLoc.getY()); //don't want to factor in Y for distance calc
                double distance = startingLoc.distanceSquared(playerLoc);
                if (distance < startingLoc.distanceSquared(closestLocation)) {
                    closestLocation = playerLoc;
                }
            }
        }
        if (startingLoc.distanceSquared(closestLocation) < 2500) {
            compassHolder.sendMessage(String.format("%sYour target is too close (within 50 blocks). Your compass has %snot%s been updated%s", ChatColor.GRAY, ChatColor.DARK_RED, ChatColor.GRAY, ChatColor.RESET));
        } else {
            compassHolder.sendMessage(String.format("%sCompass updated.%s", ChatColor.GRAY, ChatColor.RESET));
            Objects.requireNonNull(compassMeta).setLodestone(closestLocation);
            Objects.requireNonNull(compassMeta).setLodestoneTracked(false);
        }
        compass.setItemMeta(compassMeta);
    }
}
