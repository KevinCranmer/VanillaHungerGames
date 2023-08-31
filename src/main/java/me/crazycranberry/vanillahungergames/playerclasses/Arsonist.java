package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;

public class Arsonist implements PlayerClass {
    private static final int INSTA_TNT_DURATION_SECONDS = 20;

    @Override
    public String getName() {
        return "Arsonist";
    }

    @Override
    public String getInfo() {
        return "Arsonists get guaranteed TNT & redstone material drops from mobs. TNT go boom.";
    }

    @EventHandler
    private void tournamentStarted(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                ItemStack blazePowder = new ItemStack(Material.BLAZE_POWDER, 1);
                ItemMeta meta = blazePowder.getItemMeta();
                meta.setLore(List.of(String.format("%sWhile this item is on cooldown,%s", ChatColor.GOLD, ChatColor.RESET), String.format("%sTNT ignites as soon as its placed!%s", ChatColor.GOLD, ChatColor.RESET)));
                blazePowder.setItemMeta(meta);
                p.getPlayer().getInventory().addItem(blazePowder);
            }
        }
    }

    @EventHandler
    public void onFireUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (isCorrectClass(player) && item != null && item.getType() == Material.BLAZE_POWDER) {
            player.setCooldown(Material.BLAZE_POWDER, 20 * INSTA_TNT_DURATION_SECONDS);
        }
    }

    @EventHandler
    public void onTntPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (isCorrectClass(player) && block.getType() == Material.TNT && player.hasCooldown(Material.BLAZE_POWDER)) {
            block.setType(Material.AIR);
            block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        }
    }

    @EventHandler
    private void dropGunPowderAndRedStone(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && isCorrectClass(Objects.requireNonNull(event.getEntity().getKiller()))) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.TNT, 1));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.REDSTONE, (int) (Math.random() * 2 + 1)));
            int numRepeaters = (int) (Math.random() * 2);
            if (numRepeaters > 0) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.REPEATER, numRepeaters));
            }
            int numPistons = (int) (Math.random() * 2);
            if (numPistons > 0) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.STICKY_PISTON, (int) (Math.random() * 2)));
            }
        }
    }
}
