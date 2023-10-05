package me.crazycranberry.vanillahungergames.events;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ParticipantLeaveTournamentEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Participant participant;

    private final boolean shouldTeleport;

    public ParticipantLeaveTournamentEvent(Participant participant) {
        this.participant = participant;
        this.shouldTeleport = true;
    }

    public ParticipantLeaveTournamentEvent(Participant participant, boolean shouldTeleport) {
        this.participant = participant;
        this.shouldTeleport = shouldTeleport;
    }

    public Participant getParticipant() {
        return participant;
    }

    public boolean getShouldTeleport() {
        return shouldTeleport;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
