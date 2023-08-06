package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.playerclasses.PlayerClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;

public class ClassesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("classes")) {
            Player p = (Player) sender;
            p.sendMessage(String.format("Available classes are: %s%s%s", ChatColor.GREEN, String.join(", ", possibleClasses().stream().map(PlayerClass::getName).toList()), ChatColor.RESET));
            return false;
        }
        return true;
    }
}
