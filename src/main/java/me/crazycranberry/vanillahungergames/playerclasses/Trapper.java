package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Trapper extends PlayerClassWithRecurringTasks implements PlayerClass {
    private final int NUM_TRAPS = 24;
    Plugin plugin;
    List<Block> trapBlocks;

    public Trapper() {
        this.plugin = getPlugin();
        this.trapBlocks  = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Trapper";
    }

    @Override
    public String getInfo() {
        return "Trappers get " + NUM_TRAPS + " grass blocks to start. If another player walks within 2 blocks of these grass blocks, the blocks will vanish.";
    }

    @EventHandler
    private void anyTrappers(TournamentStartedEvent event){
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                checkForTrapsTriggering(plugin);
                return;
            }
        }
    }

    private String lore(String playerName) {
        return "Trap block for " + playerName;
    }

    private ItemStack trapGrassBlocks(String playerName, int numGrassBlocks) {
        ItemStack grass = new ItemStack(Material.GRASS_BLOCK, numGrassBlocks);
        ItemMeta meta = grass.getItemMeta();
        assert meta != null;
        meta.setLore(List.of(lore(playerName)));
        grass.setItemMeta(meta);
        return grass;
    }

    @EventHandler
    private void giveGrassBlocks(TournamentStartedEvent event) {
        for (Participant p : tournamentParticipants()) {
            if (isCorrectClass(p.getPlayer())) {
                p.getPlayer().getInventory().addItem(trapGrassBlocks(p.getPlayer().getDisplayName(), NUM_TRAPS));
            }
        }
    }

    @EventHandler
    private void placeTrapBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack grass = event.getItemInHand();
        if (isCorrectClass(player) && grass.getItemMeta() != null && grass.getItemMeta().getLore() != null && grass.getItemMeta().getLore().contains(lore(player.getDisplayName()))) {
            event.getBlockPlaced().setMetadata("lore", new FixedMetadataValue(plugin, lore(player.getDisplayName())));
            trapBlocks.add(event.getBlockPlaced());
        }
    }

    private boolean isPlayersTrapBlock(Player player, Block block) {
        return block.getMetadata("lore").stream().map(MetadataValue::asString).anyMatch(m -> m.equals(lore(player.getDisplayName())));
    }

    @EventHandler
    private void getBlockBack(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isCorrectClass(player) && block.getType() == Material.GRASS_BLOCK && isPlayersTrapBlock(player, block)) {
            player.getWorld().dropItemNaturally(block.getLocation(), trapGrassBlocks(player.getDisplayName(), 1));
            event.setDropItems(false);
        }
    }

    // Will not return a player's own trap blocks
    private List<Block> getTrapBlocksNextToPlayer(Player player) {
        Location playerLoc = player.getLocation();
        List<Block> closeTraps = new ArrayList<>();
        for (Block block : trapBlocks) {
            if (!isPlayersTrapBlock(player, block) && playerLoc.distanceSquared(block.getLocation()) < 7 && block.getWorld().equals(hungerGamesWorld())) {
                closeTraps.add(block);
            }
        }
        return closeTraps;
    }

    private void checkForTrapsTriggering(Plugin plugin) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        addTask(scheduler.runTaskTimer(plugin, () -> {
            for (Participant p : tournamentParticipants()) {
                if (p.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    List<Block> closeTraps = getTrapBlocksNextToPlayer(p.getPlayer());
                    for (Block block : closeTraps) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }, 0 /*<-- the initial delay */, 5L /*<-- the interval */));
    }
}
