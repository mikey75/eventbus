package net.wirelabs.eventbus.evbus2.events;


import net.wirelabs.eventbus.Event;

public class DeleteUserEvent extends Event<String> {
    public DeleteUserEvent(String payload) {
        super(payload);
    }
}
