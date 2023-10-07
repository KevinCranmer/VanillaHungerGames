package me.crazycranberry.vanillahungergames.events;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ParticipantAttemptToJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Participant participant;

    private final boolean overwriteStartingConfig;

    public ParticipantAttemptToJoinEvent(Participant participant, boolean overwriteStartingConfig) {
        this.participant = participant;
        this.overwriteStartingConfig = overwriteStartingConfig;
    }

    public Participant getParticipant() {
        return participant;
    }

    public boolean getOverwriteStartingConfig() {
        return overwriteStartingConfig;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
