package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class EventBus {

    @Getter
    private static final Map<Object, Set<EventBusClient>> subscribersByEventType = new HashMap<>();
    @Getter
    private static final Set<EventBusClient> uniqueListeners = new HashSet<>();
    @Getter
    private static final List<Event> deadEvents = new ArrayList<>();
    @Getter
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void shutdown() {
        // stop all listeners

        for (EventBusClient listener : uniqueListeners) {
            log.info("Stopping listener: {}", listener);
            listener.stop();
            // wait for listener termination
            while (!listener.getThreadHandle().isDone()) {
                Sleeper.sleepMillis(100);
            }
        }

    }


    public static void register(EventBusClient listener, Object... eventTypes) {
        for (Object evt : eventTypes) {
            subscribersByEventType.computeIfAbsent(evt, k -> new HashSet<>());
            subscribersByEventType.get(evt).add(listener);
            uniqueListeners.add(listener);

        }

    }


    /**
     * Publish event
     *
     * @param event event object
     */
    public static void publish(Event event) {

        Object evt = event.getEventType();

        if (subscribersByEventType.containsKey(evt)) {
            Set<EventBusClient> subs = subscribersByEventType.get(evt);
            for (EventBusClient listener : subs) {
                listener.getEventsQueue().add(event);
            }
        } else {
            deadEvents.add(event);
        }
    }

    /**
     * Publish event
     *
     * @param eventType event type
     * @param payload   payload object
     */
    public static void publish(Object eventType, Object payload) {
        // get all listners subscribed to the event
        Event event = new Event(eventType, payload);
        publish(event);


    }
}

