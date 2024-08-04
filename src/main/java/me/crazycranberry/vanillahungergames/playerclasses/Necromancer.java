package me.crazycranberry.vanillahungergames.playerclasses;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Necromancer implements PlayerClass {
    private static final int NUM_SKELETONS = 1;

    private Map<Player, List<Skeleton>> necromancers = new HashMap<>();

    public Necromancer() {}

    @Override
    public String getName() {
        return "Necromancer";
    }

    @Override
    public String getInfo() {
        return "Necromancer's summon " + NUM_SKELETONS + " skeleton to help them battle. Summon a skeleton by killing an entity.";
    }

    @Override
    public Material menuIcon() {
        return Material.IRON_HOE;
    }

    @EventHandler
    private void entityKilled(EntityDeathEvent event) {
        Player p = event.getEntity().getKiller();
        if (p != null && isCorrectClass(p)) {
            Skeleton skeleton = (Skeleton) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.SKELETON);
            skeleton.setCustomName(String.format("%s's Pet", p.getDisplayName()));
            skeleton.setCustomNameVisible(true);
            skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            List<Skeleton> skeletons = necromancers.get(p);
            if (skeletons == null) {
                necromancers.put(p, new ArrayList<>());
                skeletons = necromancers.get(p);
            } else if (skeletons.size() >= NUM_SKELETONS) {
                Skeleton oldSkeleton = skeletons.remove(0);
                oldSkeleton.damage(100);
            }
            skeletons.add(skeleton);
            necromancers.put(p, skeletons);
        }
    }

    @EventHandler
    private void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if(event.getTarget() instanceof Player && String.format("%s's Pet", ((Player)event.getTarget()).getDisplayName()).equals(event.getEntity().getCustomName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        String customName = event.getEntity().getCustomName();
        if(customName != null && customName.contains("'s Pet")) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}
