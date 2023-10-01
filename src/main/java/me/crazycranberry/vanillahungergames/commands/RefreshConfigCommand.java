package me.crazycranberry.vanillahungergames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;

public class RefreshConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("hgrefresh")) {
            String refreshResponse = getPlugin().refreshConfigs();
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(refreshResponse);
            }
        }
        return true;
    }
}
