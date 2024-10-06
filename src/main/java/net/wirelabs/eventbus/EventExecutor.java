package net.wirelabs.eventbus;

public interface EventExecutor extends Runnable {
    void onEvent(Event evt);
}
