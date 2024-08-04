package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class Miner implements PlayerClass {

    @Override
    public String getName() {
        return "Miner";
    }

    @Override
    public String getInfo() {
        return "Miner's get an extra raw iron when mining iron.";
    }

    @Override
    public Material menuIcon() {
        return Material.STONE_PICKAXE;
    }

    @EventHandler
    private void giveExtraIron(BlockBreakEvent event) {
        if (isCorrectClass(event.getPlayer()) && isInHungerGamesWorld(event.getBlock().getWorld()) && event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.DEEPSLATE_IRON_ORE) {
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.RAW_IRON));
        }
    }
}
