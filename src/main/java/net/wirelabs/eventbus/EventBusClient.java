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
public abstract class EventBusClient implements EventExecutor {

    private final CopyOnWriteArrayList<Event> eventsQueue = new CopyOnWriteArrayList<>(); // events queue for client
    private final AtomicBoolean shouldExit = new AtomicBoolean(false);         // thread exit flag
    private final CompletableFuture<Void> threadHandle = CompletableFuture.runAsync(this,EventBus.getExecutorService());     // thread handle

    protected EventBusClient() {
        for (IEventType e: subscribeEvents()) {
            EventBus.register(this, e);
        }
    }

    // subscribe by event type
    public void subscribe(IEventType... eventTypes) {
        EventBus.register(this, eventTypes);
    }

    public void run() {
        while(!shouldExit.get()) {
            Optional<Event> evt = eventsQueue.stream().findFirst();
            evt.ifPresent(event -> {
                onEvent(event);
                eventsQueue.remove(event);
            });
            Sleeper.sleepMillis(50);
        }
    }

    protected void stop() {
        shouldExit.set(true);
        threadHandle.join();
    }
}
