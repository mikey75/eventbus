package net.wirelabs.evbus.eventbus;

public interface EventExecutor {
    void onEvent(Event evt);
}
