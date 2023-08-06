package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.events.HungerGamesWorldCreateCommandExecutedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;

public class CreateHungerGamesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("createhungergames")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (hungerGamesWorld() != null) {
                    p.sendMessage("There already is a tournament in progress, you trying to overload the server or something?");
                    return false;
                }
                p.sendMessage("One moment while we build the hunger games world for you.");
            }
            Bukkit.getServer().broadcastMessage("Building a hunger games world. Server is about to freeze for a moment. My apologies.");
            Bukkit.getPluginManager().callEvent(new HungerGamesWorldCreateCommandExecutedEvent());
            return false;
        }
        return true;
    }
}
