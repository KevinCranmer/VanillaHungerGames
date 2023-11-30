package me.crazycranberry.vanillahungergames;

import me.crazycranberry.vanillahungergames.events.BorderShrinkEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesEvent;
import me.crazycranberry.vanillahungergames.events.InvincibilityEndedEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.vanillahungergames.VanillaHungerGames.getPlugin;
import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.borderBoundaries;

public class HungerGamesEventList {
    private static final List<Integer> borderRadiuses = List.of(500, 400, 300, 200, 100, 50, 25, 10);
    private static int phase = 0;
    private static final int MIN = 60000;
    private static final int SEC = 1000;
    private static int eventIndex = 0;

    public static List<HungerGamesEvent> events() {
        eventIndex = 0;
        phase = 0;
        List<HungerGamesEvent> events = new ArrayList<>(preGameCountdownEvents());
        events.addAll(List.of(
                new HungerGamesEvent(String.format("%sLET THE TOURNAMENT BEGIN! GOODLUCK EVERYONE%s", ChatColor.AQUA, ChatColor.RESET), 0, new TournamentStartedEvent(), eventIndex++),
                new HungerGamesEvent("The world border is " + borderBoundaries(borderRadiuses.get(phase)) + " it will shrink in 7 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 1 minute", 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 30 seconds", 15 * SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 15 seconds", 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 10 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 9 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 8 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 7 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 6 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 5 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 4 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 3 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 2 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Invincibility will wear off in 1 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent(String.format("%sKILL EACH OTHER NOW!!!%s", ChatColor.RED, ChatColor.RESET), 0, new InvincibilityEndedEvent(), eventIndex++),
                new HungerGamesEvent("The world border is " + borderBoundaries(borderRadiuses.get(phase++)) + " it will shrink in 5 minutes", 3 * MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("The border will shrink in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 1 minute to become " + borderBoundaries(borderRadiuses.get(phase)), 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 30 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 20 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border will shrink in 10 seconds to become " + borderBoundaries(borderRadiuses.get(phase)), 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("The border is now shrinking to " + borderBoundaries(borderRadiuses.get(phase)) + " over the next 2 minutes", 3 * MIN, new BorderShrinkEvent(borderRadiuses.get(phase++)), eventIndex++),
                new HungerGamesEvent("Somebody better win in the next minute or tourneys over.", MIN, null, eventIndex++),
                new HungerGamesEvent("ggs.", MIN, new HungerGamesCompletedEvent(), eventIndex++)
        ));
        return List.copyOf(events);
    }

    private static List<HungerGamesEvent> preGameCountdownEvents() {
        List<HungerGamesEvent> preGameCountdownEvents = new ArrayList<>();
        VanillaHungerGamesConfig config = getPlugin().vanillaHungerGamesConfig();
        int minutes = config.preGameCountdownMinutes();
        int seconds = config.preGameCountdownSeconds();
        if (minutes < 1) {
            if (seconds > 30) {
                preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in " + seconds + " seconds", (seconds - 30) * SEC, null, eventIndex++));
                preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 30 seconds", 15 * SEC, null, eventIndex++));
                preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 15 seconds", 5 * SEC, null, eventIndex++));
                preGameCountdownEvents.addAll(preGameCountdownEventsSeparatedByASecond(10));
            } else if (seconds > 15) {
                preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in " + seconds + " seconds", (seconds - 15) * SEC, null, eventIndex++));
                preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 15 seconds", 5 * SEC, null, eventIndex++));
                preGameCountdownEvents.addAll(preGameCountdownEventsSeparatedByASecond(10));
            } else {
                preGameCountdownEvents.addAll(preGameCountdownEventsSeparatedByASecond(seconds));
            }
            return preGameCountdownEvents;
        }
        if (seconds != 0) {
            preGameCountdownEvents.add(new HungerGamesEvent(String.format("Tournament will start in %s minutes and %s seconds", minutes, seconds), seconds * SEC, null, eventIndex++));
        }
        preGameCountdownEvents.addAll(preGameCountdownEventsSeparatedByAMinute(minutes));
        preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 1 minutes", 30 * SEC, null, eventIndex++));
        preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 30 seconds", 15 * SEC, null, eventIndex++));
        preGameCountdownEvents.add(new HungerGamesEvent("Tournament will start in 15 seconds", 5 * SEC, null, eventIndex++));
        preGameCountdownEvents.addAll(preGameCountdownEventsSeparatedByASecond(10));
        return preGameCountdownEvents;
    }

    private static List<HungerGamesEvent> preGameCountdownEventsSeparatedByASecond(int startingSecond) {
        List<HungerGamesEvent> events = new ArrayList<>();
        for (int i = startingSecond; i > 0; i--) {
            events.add(new HungerGamesEvent(String.format("Tournament will start in %s second%s", i, i == 1 ? "" : "s"), SEC, null, eventIndex++));
        }
        return events;
    }

    /** This excludes the final 1 minute warning event so that the time between it and the next event can be better refined. */
    private static List<HungerGamesEvent> preGameCountdownEventsSeparatedByAMinute(int startingMinute) {
        List<HungerGamesEvent> events = new ArrayList<>();
        for (int i = startingMinute; i > 1; i--) {
            events.add(new HungerGamesEvent(String.format("Tournament will start in %s minutes", i), MIN, null, eventIndex++));
        }
        return events;
    }

    public static Integer beginningBorderSize() {
        return borderRadiuses.get(0);
    }
}
