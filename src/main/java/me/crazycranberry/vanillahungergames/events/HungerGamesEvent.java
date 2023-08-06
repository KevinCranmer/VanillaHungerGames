package me.crazycranberry.vanillahungergames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HungerGamesEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final String message;

    private final int msTillNextEvent;

    private final Event eventToTrigger;

    private final int eventIndex;

    public HungerGamesEvent(String message, int msTillNextEvent, Event eventToTrigger, int eventIndex) {
        this.message = message;
        this.msTillNextEvent = msTillNextEvent;
        this.eventToTrigger = eventToTrigger;
        this.eventIndex = eventIndex;
    }

    public String getMessage() {
        return message;
    }

    public int getMsTillNextEvent() {
        return msTillNextEvent;
    }

    public Event getEventToTrigger() {
        return eventToTrigger;
    }

    public int getEventIndex() {
        return eventIndex;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
