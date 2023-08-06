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

public class LeaveHungerGamesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            World hgWorld = hungerGamesWorld();
            if (hgWorld == null || !p.getWorld().equals(hgWorld)) {
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
