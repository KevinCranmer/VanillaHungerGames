package me.crazycranberry.vanillahungergames;

import me.crazycranberry.vanillahungergames.events.BorderShrinkEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesCompletedEvent;
import me.crazycranberry.vanillahungergames.events.HungerGamesEvent;
import me.crazycranberry.vanillahungergames.events.InvincibilityEndedEvent;
import me.crazycranberry.vanillahungergames.events.TournamentStartedEvent;
import org.bukkit.ChatColor;

import java.util.List;

import static me.crazycranberry.vanillahungergames.managers.HungerGamesWorldManager.borderBoundaries;

public class HungerGamesEventList {
    private static final List<Integer> borderRadiuses = List.of(500, 400, 300, 200, 100, 50, 25, 10);
    private static int phase = 0;
    private static final int MIN = 60000;
    private static final int SEC = 1000;
    private static int eventIndex = 0;

    public static List<HungerGamesEvent> events() {
        return List.of(
                new HungerGamesEvent("Tournament will start in 2 minutes", MIN, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 1 minutes", 30 * SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 30 seconds", 15 * SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 15 seconds", 5 * SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 10 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 9 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 8 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 7 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 6 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 5 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 4 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 3 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 2 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent("Tournament will start in 1 seconds", SEC, null, eventIndex++),
                new HungerGamesEvent(String.format("%sLET THE TOURNAMENT BEGIN! GOODLUCK EVERYONE%s", ChatColor.AQUA, ChatColor.RESET), 0, new TournamentStartedEvent(), eventIndex++),
                new HungerGamesEvent("Tournament will start in 1 seconds", SEC, null, eventIndex++),
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
        );
    }

    public static Integer beginningBorderSize() {
        return borderRadiuses.get(0);
    }
}
