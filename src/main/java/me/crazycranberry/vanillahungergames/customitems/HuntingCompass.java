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

    private static double distanceSquared(Location loc1, Location loc2) {
        //intentionally don't want to factor in Y for distance calc
        double x1 = loc1.getX();
        double z1 = loc1.getZ();
        double x2 = loc2.getX();
        double z2 = loc2.getZ();
        return Math.pow(x1 - x2, 2) +  Math.pow(z1 - z2, 2);
    }

    public static void pointCompassToNearestPlayer(ItemStack compass, Player compassHolder) {
        if (tournamentParticipants().size() == 0) {
            return;
        } else if (tournamentParticipants().size() == 1) {
            compassHolder.sendMessage(String.format("%sYou've already won. There is no one else to look for.%s", ChatColor.GRAY, ChatColor.RESET));
            return;
        }
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        Location startingLoc = compassHolder.getLocation();
        Location closestLocation = tournamentParticipants().get(0).getPlayer().equals(compassHolder) ? tournamentParticipants().get(1).getPlayer().getLocation() : tournamentParticipants().get(0).getPlayer().getLocation();
        for (Participant p : tournamentParticipants()) {
            if (p.getPlayer().getGameMode() == GameMode.SURVIVAL && !p.getPlayer().getName().equals(compassHolder.getName())) {
                Location playerLoc = p.getPlayer().getLocation();
                double distanceSquared = distanceSquared(playerLoc, startingLoc);
                if (distanceSquared < distanceSquared(startingLoc, closestLocation)) {
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
