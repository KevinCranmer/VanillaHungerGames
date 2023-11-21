package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.events.ParticipantAttemptToJoinEvent;
import me.crazycranberry.vanillahungergames.events.ParticipantJoinTournamentEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isInHungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.isWorldReady;
import static me.crazycranberry.vanillahungergames.utils.StartingWorldConfigUtils.startingWorldConfigExists;

public class JoinHungerGamesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgjoin")) {
            Player p = (Player) sender;
            World hgWorld = hungerGamesWorld();
            if (hgWorld == null || !isWorldReady()) {
                p.sendMessage("There is not a hunger games tournament happening at the moment.");
            } else if (isInHungerGamesWorld(p.getWorld())) {
                p.sendMessage("You're already in the hunger games tournament, you silly goose.");
            } else if (startingWorldConfigExists(p) && (args.length < 1 || (!args[0].equals("keep") && !args[0].equals("overwrite")))) {
                p.sendMessage("Your inventory/location of when you last joined the hunger games is still saved. Please type one of the following:");
                p.sendMessage("\"/hgjoin keep\" - This will keep your previous inventory/location that you had when you last joined the hunger games.");
                p.sendMessage("\"/hgjoin overwrite\" - This will overwrite and your current inventory/location will be saved. (Note: OLD ITEMS MIGHT BE LOST FOREVER)");
            } else if (startingWorldConfigExists(p) && args[0].equals("keep")) {
                p.sendMessage("Joining the hunger games... And keeping your previous inventory/location as your return configuration.");
                Bukkit.getPluginManager().callEvent(new ParticipantAttemptToJoinEvent(new Participant(p), false));
            } else if (startingWorldConfigExists(p) && args[0].equals("overwrite")) {
                p.sendMessage("Joining the hunger games... And overwriting your old inventory/location with your current one.");
                Bukkit.getPluginManager().callEvent(new ParticipantAttemptToJoinEvent(new Participant(p), true));
            } else {
                p.sendMessage("Joining the hunger games...");
                Bukkit.getPluginManager().callEvent(new ParticipantAttemptToJoinEvent(new Participant(p), true));
            }
        }
        return true;
    }
}
