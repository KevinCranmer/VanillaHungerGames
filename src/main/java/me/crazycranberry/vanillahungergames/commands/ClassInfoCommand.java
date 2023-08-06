package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.playerclasses.PlayerClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;

public class ClassInfoCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("classinfo")) {
            Player p = (Player) sender;
            if (args.length < 1) {
                p.sendMessage("You must provide a class name (example: SNOWMAN)");
                return false;
            }
            Optional<PlayerClass> playerClass = possibleClasses().stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst();
            if (!playerClass.isPresent()) {
                p.sendMessage(String.format("%s is not a valid class name. Try /classes to find available classes.", args[0]));
                return false;
            }
            p.sendMessage(String.format("%s%s%s", ChatColor.GOLD, playerClass.get().getInfo(), ChatColor.RESET));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("class") || command.getName().equalsIgnoreCase("classinfo")) && args.length == 1) {
            return possibleClasses().stream().map(PlayerClass::getName).filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
