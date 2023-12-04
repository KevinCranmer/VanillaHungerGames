package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.CustomItemSpawn;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class HungerGamesChestsManager implements Listener {
    private final String playerCreatedChestCustomName = "VanillaHungerGamesPlayerPlacedChest";

    @EventHandler
    private void openChest(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST) && isInHungerGamesWorld(event.getClickedBlock().getWorld()) && !getPlugin().vanillaHungerGamesConfig().useVanillaItemsInChests()) {
            if (event.getClickedBlock().getState().getMetadata(playerCreatedChestCustomName).stream().noneMatch(m -> "true".equals(m.value()))) {
                wipeChest((Chest) event.getClickedBlock().getState());
                if (tournamentInProgress()) {
                    setChestContents((Chest) event.getClickedBlock().getState());
                    event.getClickedBlock().getState().setMetadata(playerCreatedChestCustomName, new FixedMetadataValue(getPlugin(), "true"));
                }
            }
        }
    }

    private void wipeChest(Chest chest) {
        Inventory inv = chest.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }
    }

    @EventHandler
    private void chestPlaced(BlockPlaceEvent event) {
        if (isInHungerGamesWorld(event.getBlockPlaced().getWorld()) && event.getBlockPlaced().getType().equals(Material.CHEST)) {
            event.getBlockPlaced().getState().setMetadata(playerCreatedChestCustomName, new FixedMetadataValue(getPlugin(), "true"));
        }
    }

    private void setChestContents(Chest chest) {
        Inventory inv = chest.getInventory();
        List<Integer> availableChestSlots = new ArrayList<>(IntStream.range(0, inv.getSize()).boxed().toList());
        for (CustomItemSpawn customItemSpawn : getPlugin().vanillaHungerGamesConfig().customItemSpawnsInChests()) {
            double rand = Math.random();
            if (rand < customItemSpawn.chance()) {
                int quantity = (int)(Math.random() * (customItemSpawn.max() - customItemSpawn.min())) + customItemSpawn.min();
                for (int i = quantity; i > 0; i = i - customItemSpawn.material().getMaxStackSize()) {
                    int stackSize = Math.min(i, customItemSpawn.material().getMaxStackSize());
                    if (availableChestSlots.size() == 0) {
                        return;
                    }
                    int availableChestSlotsIndex = (int)(Math.random() * availableChestSlots.size());
                    int chestIndex = availableChestSlots.get(availableChestSlotsIndex);
                    availableChestSlots.remove(availableChestSlotsIndex);
                    inv.setItem(chestIndex, new ItemStack(customItemSpawn.material(), stackSize));
                }
            }
        }
    }
}
