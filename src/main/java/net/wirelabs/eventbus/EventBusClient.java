package net.wirelabs.eventbus;

import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created 6/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Getter
public abstract class EventBusClient implements Runnable, EventExecutor {

    private final AtomicBoolean shouldExit = new AtomicBoolean(false);
    private final CompletableFuture<Void> threadHandle;

    // events queue for client
    private final CopyOnWriteArrayList<Event> eventsQueue = new CopyOnWriteArrayList<>();


    protected EventBusClient() {
        threadHandle =  CompletableFuture.runAsync(this, EventBus.getExecutorService());
    }

    public void run() {
        while(!shouldExit.get()) {
            processEvents();
        }
    }

    private void processEvents() {
        Optional<Event> evt = eventsQueue.stream().findFirst();
        evt.ifPresent(event -> {
            onEvent(event);
            eventsQueue.remove(event);
        });
        Sleeper.sleepMillis(50);
    }
    // subscribe by event type
    public void subscribe(IEventType... eventTypes) {
        EventBus.register(this, eventTypes);
    }
    // subscribe by event object
    public void subscribe(Event ... events) {
        for (Event ev: events) {
            EventBus.register(this, ev.getEventType());
        }
    }

    public void stop() {
        shouldExit.set(true);
        if (threadHandle != null) {
            threadHandle.join();
        }
    }

}
