package me.crazycranberry.vanillahungergames.events;

import me.crazycranberry.vanillahungergames.Participant;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ParticipantJoinTournamentEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Participant participant;

    public ParticipantJoinTournamentEvent(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
