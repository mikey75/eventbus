package net.wirelabs.eventbus.evbus2.events;


import net.wirelabs.eventbus.Event;

public class OtherEvent extends Event<Object> {
    public OtherEvent(Object payload) {
        super(payload);
    }
}
