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

import static me.crazycranberry.vanillahungergames.managers.HungerGamesManager.tournamentInProgress;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.getParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.isTournamentParticipant;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.hungerGamesWorld;
import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;

public class ClassCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgclass")) {
            Player p = (Player) sender;
            if (args.length < 1) {
                p.sendMessage("You must provide a class name (example: SNOWMAN)");
                return false;
            }
            Optional<PlayerClass> playerClass = possibleClasses().stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).findFirst();
            if (!playerClass.isEmpty()) {
                p.sendMessage(String.format("%s is not a valid class name. Try /hgclasses to find available classes.", args[0]));
                return false;
            }
            if (!p.getWorld().equals(hungerGamesWorld()) || !isTournamentParticipant(p)) {
                p.sendMessage("Bro, you're not in the hunger games tournament. You can't change class right now... smh.");
                return false;
            }
            if (tournamentInProgress()) {
                p.sendMessage("You cannot change classes now, the tournament has already started.");
                return false;
            }
            getParticipant(p).setPlayerClass(playerClass.get());
            p.sendMessage(String.format("You are now class: %s%s%s", ChatColor.GREEN, playerClass.get().getName(), ChatColor.RESET));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("hgclass") || command.getName().equalsIgnoreCase("hgclassinfo")) && args.length == 1) {
            return possibleClasses().stream().map(PlayerClass::getName).filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
