package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Miner implements PlayerClass {

    @Override
    public String getName() {
        return "Miner";
    }

    @Override
    public String getInfo() {
        return "Miner's get an extra raw iron when mining iron.";
    }

    @EventHandler
    private void giveExtraIron(BlockBreakEvent event) {
        if (isCorrectClass(event.getPlayer()) && event.getBlock().getWorld().equals(hungerGamesWorld()) && event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.DEEPSLATE_IRON_ORE) {
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.RAW_IRON));
        }
    }
}
