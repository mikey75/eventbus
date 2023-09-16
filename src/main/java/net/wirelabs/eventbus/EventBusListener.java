package net.wirelabs.eventbus;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class EventBusListener extends EventBusClient {

    protected final CopyOnWriteArrayList<Event> eventsQueue = new CopyOnWriteArrayList<>();
    protected final CompletableFuture<Void> listenerThread;

    protected EventBusListener() {
        // subscribe to specified events
        for (IEventType eventType : subscribeTo()) {
            subsrcibeEvents(this,eventType);
        }
        // start listener
        listenerThread = EventBus.runAsync(() ->
        
                runContinuously(() -> {
                    Optional<Event> evt = eventsQueue.stream().findFirst();
                    evt.ifPresent(event -> {
                        onEvent(event);
                        eventsQueue.remove(event);
                    });
                }));

        // register listener on the bus
        registerListener(this);
    }

    protected abstract void onEvent(Event evt);

    protected Collection<IEventType> subscribeTo() {
        return Collections.emptyList();
    }

/**
     * Subscribe outside initialization (runtime on demand subscription)
     *
     * @param eventTypes event types to subscribe to
     */

    public void subscribe(IEventType... eventTypes) {
        subsrcibeEvents(this, eventTypes);
    }

    /**
     * Stop subscribing to event type on runtime
     * No events of the specified type will be serviced from now on
     *
     * @param eventTypes event types to unsubscibe
     */
    public void unsubscribe(IEventType... eventTypes) {
        unsubscibeEvents(this, eventTypes);
    }

    /**
     * return event queue size for this listener
     *
     * @return events in the queue
     */
    public long queueSize() {
        return eventsQueue.size();
    }

    /**
     * Add event to the queue
     *
     * @param evt event
     */
    void newEvent(Event evt) {
        eventsQueue.add(evt);
    }

}
