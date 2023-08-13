package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isWorldReady;

public class JoinHungerGamesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgjoin")) {
            Player p = (Player) sender;
            World hgWorld = hungerGamesWorld();
            if (hgWorld == null || !isWorldReady()) {
                p.sendMessage("There is not a hunger games tournament happening at the moment.");
            } else if (p.getWorld().equals(hgWorld)) {
                p.sendMessage("You're already in the hunger games tournament, you silly goose.");
            } else {
                p.sendMessage("Joining the hunger games...");
                Bukkit.getPluginManager().callEvent(new ParticipantJoinTournamentEvent(new Participant(p)));
                return false;
            }
        }
        return true;
    }
}
