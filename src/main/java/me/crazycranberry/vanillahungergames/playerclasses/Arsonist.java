package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Arsonist implements PlayerClass {

    @Override
    public String getName() {
        return "Arsonist";
    }

    @Override
    public String getInfo() {
        return "Arsonists get guaranteed TNT & redstone material drops from mobs. TNT go boom.";
    }

    @EventHandler
    private void dropGunPowderAndRedStone(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && isCorrectClass(Objects.requireNonNull(event.getEntity().getKiller()))) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.TNT, 1));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.REDSTONE, (int) (Math.random() * 2 + 1)));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.REPEATER, (int) (Math.random() * 2)));
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.STICKY_PISTON, (int) (Math.random() * 2)));
        }
    }
}
