package net.wirelabs.eventbus;

import lombok.Getter;

@Getter
public abstract class Event<T> {
    private final T payload;
    protected Event(T payload) {
        this.payload = payload;
    }
}