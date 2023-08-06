package me.crazycranberry.vanillahungergames.managers;

import me.crazycranberry.vanillahungergames.customenchantments.BigKnockBack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;

public class EnchantmentManager implements Listener {
    private static Map<String, Enchantment> custom_enchants;
    private final Plugin plugin;

    public EnchantmentManager() {
        this.plugin = getPlugin();
        custom_enchants = new HashMap<>();
        registerEnchantments();
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableEnchantments();
        }
    }

    private void registerEnchantments() {
        BigKnockBack bigKnockBack = new BigKnockBack();
        registerEnchantment("BigKnockBack", bigKnockBack);
        Bukkit.getServer().getPluginManager().registerEvents(bigKnockBack, plugin);
    }

    private void registerEnchantment(String enchantName, Enchantment enchantment) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
            custom_enchants.put(enchantName, enchantment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Enchantment getEnchantmentObject(String name) {
        return custom_enchants.get(name);
    }

    private void disableEnchantments() {
        for (Map.Entry<String, Enchantment> enchantment : custom_enchants.entrySet()) {
            disableEnchantment(enchantment.getValue());
        }
    }

    private void disableEnchantment(Enchantment enchantment) {
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            if(byKey.containsKey(enchantment.getKey())) {
                byKey.remove(enchantment.getKey());
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            if(byName.containsKey(enchantment.getName())) {
                byName.remove(enchantment.getName());
            }
        } catch (Exception ignored) { }

    }
}
