package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.Objects;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class Cultivator implements PlayerClass {

    private List<Material> plants = List.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.TORCHFLOWER_CROP,
            Material.PITCHER_CROP,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM,
            Material.SPRUCE_SAPLING,
            Material.OAK_SAPLING,
            Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.ACACIA_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.CHERRY_SAPLING
    );

    @Override
    public String getName() {
        return "Cultivator";
    }

    @Override
    public String getInfo() {
        return "Cultivators instantly grow plants.";
    }

    @EventHandler
    private void growPlant(BlockPlaceEvent event) {
        if(isCorrectClass(event.getPlayer()) && plants.contains(event.getBlockPlaced().getType()) && event.getBlockPlaced().getWorld().equals(hungerGamesWorld())) {
            if (event.getBlockPlaced().getType().equals(Material.TORCHFLOWER_CROP)) {
                event.getBlockPlaced().setType(Material.TORCHFLOWER);
            } else if (event.getBlockPlaced().getBlockData() instanceof Ageable) {
                Ageable crop = (Ageable) event.getBlockPlaced().getBlockData();
                crop.setAge(crop.getMaximumAge());
                event.getBlockPlaced().setBlockData(crop);
            } else if (event.getBlockPlaced().getBlockData() instanceof Sapling) {
                TreeType tree;
                switch (event.getBlockPlaced().getType()) {
                    case SPRUCE_SAPLING:
                        tree = TreeType.REDWOOD;
                        break;
                    case BIRCH_SAPLING:
                        tree = TreeType.BIRCH;
                        break;
                    case JUNGLE_SAPLING:
                        tree = TreeType.JUNGLE;
                        break;
                    case ACACIA_SAPLING:
                        tree = TreeType.ACACIA;
                        break;
                    case DARK_OAK_SAPLING:
                        tree = TreeType.DARK_OAK;
                        break;
                    case CHERRY_SAPLING:
                        tree = TreeType.CHERRY;
                        break;
                    default:
                        tree = TreeType.TREE;
                }
                Location location = event.getBlock().getLocation();
                event.getBlock().setType(Material.AIR);
                Objects.requireNonNull(location.getWorld()).generateTree(location, tree);
            }
        }
    }
}
