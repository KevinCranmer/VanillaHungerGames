package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.events.ParticipantLeaveTournamentEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.getParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;

public class LeaveHungerGamesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgleave")) {
            Player p = (Player) sender;
            if (!isInHungerGamesWorld(p.getWorld())) {
                p.sendMessage("You're not in a tournament, how could you leave it?");
            } else {
                p.sendMessage("Leaving the hunger games...");
                Bukkit.getPluginManager().callEvent(new ParticipantLeaveTournamentEvent(getParticipant(p)));
                return false;
            }
        }
        return true;
    }
}
