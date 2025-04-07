package net.wirelabs.eventbus;

import java.util.Collection;

public interface EventExecutor extends Runnable {
    void onEvent(Event evt);
    Collection<IEventType> subscribeEvents();
}
