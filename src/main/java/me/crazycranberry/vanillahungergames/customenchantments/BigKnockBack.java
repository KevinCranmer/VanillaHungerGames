package me.crazycranberry.vanillahungergames.customenchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;

public class BigKnockBack extends Enchantment implements Listener {

    public BigKnockBack() {
        super();
    }

    @EventHandler
    private void knockThemBack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemMeta meta = damager.getInventory().getItemInMainHand().getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(this.getKey())) {
                int enchantmentLevel = meta.getPersistentDataContainer().get(this.getKey(), PersistentDataType.INTEGER);
                Vector knockBackVector = damager.getLocation().getDirection().multiply(enchantmentLevel);
                if (knockBackVector.getY() < enchantmentLevel * 0.4) {
                    knockBackVector.setY(enchantmentLevel * 0.4);
                } else if (knockBackVector.getY() > enchantmentLevel * 0.8) {
                    knockBackVector.setY(enchantmentLevel * 0.8);
                }
                event.getEntity().setVelocity(knockBackVector);
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "BigKnockBack";
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(getPlugin(), "bigknockback");
    }
}
