package net.wirelabs.eventbus.evbus2.events;


import net.wirelabs.eventbus.Event;

public class MapChangedEvent extends Event<String> {
    public MapChangedEvent(String payload) {
        super(payload);
    }
}
