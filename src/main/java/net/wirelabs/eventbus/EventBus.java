package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBus {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Map<IEventType, Set<EventBusListener>> subscribersByEventType = new HashMap<>();
    private static final Set<EventBusListener> listeners = new HashSet<>();

    /**
     * Subscribe listener to event(s)
     * @param listener listener
     * @param eventTypes event types
     */
    static void register(EventBusListener listener, IEventType... eventTypes) {
        for (IEventType evt : eventTypes) {
            subscribersByEventType.computeIfAbsent(evt, k -> new HashSet<>());
            subscribersByEventType.get(evt).add(listener);
        }
    }

    /**
     * Unsubscribe listener from event(s)
     * @param listener listener
     * @param eventTypes event types
     */
    static void unregister(EventBusListener listener, IEventType... eventTypes) {
        for (IEventType evt : eventTypes) {
            subscribersByEventType.get(evt).remove(listener);
        }
    }

    /**
     * Publish event object
     * @param event event object
     */
    public static void publish(Event event) {

        IEventType eventType = event.getEventType();
        Set<EventBusListener> subscribersOfEvent = subscribersByEventType.get(eventType);
        // if no subscribers of the event -> ignore
            if (subscribersOfEvent != null) {
                for (EventBusListener listener : subscribersOfEvent) {
                    listener.newEvent(event);
                }
            }
    }

    public static void publish(Event... events) {
        for (Event evt: events) {
            publish(evt);
        }
    }

    /**
     * Publish event as type + payload
     * @param eventType event type
     * @param payload   payload object
     */
    public static void publish(IEventType eventType, Object payload) {
        Event event = new Event(eventType, payload);
        publish(event);
    }

    /**
     * Stop the bus, ie all listener threads.
     */
    public static void stop() {
        for (EventBusListener listener: listeners) {
            listener.stop();
        }
    }

    /**
     * Register new listener
     * @param listener bus listener
     */
    static void newListener(EventBusListener listener) {
        listeners.add(listener);
    }

    static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

}
