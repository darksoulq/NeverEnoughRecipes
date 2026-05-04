package com.github.darksoulq.ner.plugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class NerRegistrationEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Registration registration;

    public NerRegistrationEvent(Registration registration) {
        this.registration = registration;
    }

    public Registration getRegistration() {
        return registration;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}