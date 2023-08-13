package me.crazycranberry.vanillahungergames.playerclasses;

import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/** Only used as a parent to other PlayerClasses. Can't be an interface because it uses `taskIds`. */
public class PlayerClassWithRecurringTasks implements Listener {
    List<Integer> taskIds = new ArrayList<>();

    @EventHandler
    public void onTournamentCompleted(HungerGamesCompletedEvent event) {
        for (Integer i : taskIds) {
            Bukkit.getServer().getScheduler().cancelTask(i);
        }
        taskIds = new ArrayList<>();
    }

    public void addTask(BukkitTask task) {
        taskIds.add(task.getTaskId());
    }
}
