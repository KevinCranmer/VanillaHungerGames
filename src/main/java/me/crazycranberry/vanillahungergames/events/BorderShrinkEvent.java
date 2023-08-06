package me.crazycranberry.vanillahungergames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BorderShrinkEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final float newSize;

    public BorderShrinkEvent(float newSize) {
        this.newSize = newSize;
    }

    public float getNewSize() {
        return newSize;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
