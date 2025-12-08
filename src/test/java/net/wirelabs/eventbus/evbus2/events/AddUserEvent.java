package net.wirelabs.eventbus.evbus2.events;


import net.wirelabs.eventbus.Event;

public class AddUserEvent extends Event<String> {

    public AddUserEvent(String payload) {
        super(payload);
    }
}
