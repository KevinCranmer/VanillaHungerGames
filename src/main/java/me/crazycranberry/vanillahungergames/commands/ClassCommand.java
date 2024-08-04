package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.playerclasses.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.getParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.isTournamentParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.DIAMOND;
import static org.bukkit.Material.DIAMOND_AXE;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.PAPER;

public class ClassCommand implements CommandExecutor, TabCompleter {
    public static final NamespacedKey MENU_KEY = new NamespacedKey(getPlugin(), "menu_item");

    public static final String CLASS_MENU_NAME_PREFIX = "Current Class: ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgclass")) {
            Player p = (Player) sender;
            if (!isInHungerGamesWorld(p.getWorld()) || !isTournamentParticipant(p)) {
                p.sendMessage("Bro, you're not in the hunger games tournament. You can't change class right now... smh.");
                return false;
            }
            if (args.length < 1) {
                if (tournamentInProgress()) {
                    p.sendMessage(String.format("You are currently: %s%s%s.", ChatColor.GREEN, getParticipant(p).getPlayerClass().getName(), ChatColor.RESET));
                } else {
                    p.openInventory(createHgClassInventory(getParticipant(p).getPlayerClass()));
                }
                return false;
            }
            if (tournamentInProgress()) {
                p.sendMessage("You cannot change classes now, the tournament has already started.");
                return false;
            }
            Optional<PlayerClass> playerClass = possibleClasses().stream().filter(c -> c.getName().equalsIgnoreCase(args[0]) || c.getName().toLowerCase().startsWith(args[0].toLowerCase())).findFirst();
            if (playerClass.isEmpty()) {
                p.sendMessage(String.format("%s is not a valid class name. Try /hgclasses to find available classes.", args[0]));
                return false;
            }
            getParticipant(p).setPlayerClass(playerClass.get());
            p.sendMessage(String.format("You are now class: %s%s%s", ChatColor.GREEN, playerClass.get().getName(), ChatColor.RESET));
            return false;
        }
        return true;
    }

    public static Inventory createHgClassInventory(PlayerClass playerClass) {
        Inventory tcgInv = Bukkit.createInventory(null, 27, String.format("%s%s", CLASS_MENU_NAME_PREFIX, playerClass == null ? "None" : playerClass.getName()));
        for (PlayerClass clazz : possibleClasses()) {
            tcgInv.addItem(createClassMenuItem(clazz));
        }
        return tcgInv;
    }

    private static ItemStack createClassMenuItem(PlayerClass clazz) {
        String command = "hgclass " + clazz.getName();
        ItemStack item = new ItemStack(clazz.menuIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(String.format("%s/%s%s", AQUA, command, RESET));
        meta.setLore(lore(clazz.getInfo()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, command.replace("/", ""));
        item.setItemMeta(meta);
        return item;
    }

    private static List<String> lore(String info) {
        List<String> lore = new ArrayList<>();
        int maxLineLength = 40;
        int infoIndex = 0;
        while (infoIndex + maxLineLength <= info.length()) {
            String infoLine = info.substring(infoIndex, infoIndex + maxLineLength);
            int lastSpaceIndex = infoLine.lastIndexOf(" ");
            lore.add(String.format("%s%s%s", GRAY, info.substring(infoIndex, infoIndex + lastSpaceIndex), RESET));
            infoIndex = infoIndex + lastSpaceIndex + 1;
        }
        lore.add(String.format("%s%s%s", GRAY, info.substring(infoIndex), RESET));
        return lore;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("hgclass") || command.getName().equalsIgnoreCase("hgclassinfo")) && args.length == 1) {
            return possibleClasses().stream().map(PlayerClass::getName).filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
