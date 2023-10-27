package me.crazycranberry.vanillahungergames.commands;

import me.crazycranberry.vanillahungergames.Participant;
import me.crazycranberry.vanillahungergames.playerclasses.PlayerClass;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesParticipantManager.tournamentParticipants;
import static me.crazycranberry.vanillahungergames.managers.PlayerClassManager.possibleClasses;

public class SpectateTeleportCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("hgteleport")) {
            Player p = (Player) sender;
            if (!getPlugin().vanillaHungerGamesConfig().allowSpectateTeleport()) {
                p.sendMessage("This command is disabled on this server.");
                return false;
            }
            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                p.sendMessage("You can only teleport while in spectator mode.");
                return false;
            }
            if (args.length < 1) {
                p.sendMessage("You must provide a player to teleport to.");
                return false;
            }
            Optional<Player> teleportTarget = tournamentParticipants().stream().map(Participant::getPlayer).filter(player -> player.getName().equalsIgnoreCase(args[0]) || player.getName().toLowerCase().startsWith(args[0].toLowerCase())).findFirst();
            if (teleportTarget.isEmpty()) {
                p.sendMessage(String.format("%s is not a player in the hunger games.", args[0]));
                return false;
            }
            p.sendMessage(String.format("%sTeleporting to %s%s", ChatColor.GRAY, teleportTarget.get().getName(), ChatColor.RESET));
            p.teleport(teleportTarget.get().getLocation());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("hgclass") || command.getName().equalsIgnoreCase("hgclassinfo")) && args.length == 1) {
            return tournamentParticipants().stream().map(Participant::getPlayer).filter(Player::isOnline).map(Player::getName).filter(player -> player.equalsIgnoreCase(args[0]) || player.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
