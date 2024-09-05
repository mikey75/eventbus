package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class EventBus {

    @Getter
    private static final Map<Object, Set<EventBusClient>> subscribersByEventType = new HashMap<>();
    @Getter
    private static final Set<EventBusClient> uniqueClients = new HashSet<>();
    @Getter
    private static final List<Event> deadEvents = new ArrayList<>();
    @Getter
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void shutdown() {
        log.info("Shutting down the EventBus");
        // stop all clients
        for (EventBusClient client : uniqueClients) {
            log.info("Stopping client: {}", client);
            client.stop();
            // wait for client termination
            while (!client.getThreadHandle().isDone()) {
                Sleeper.sleepMillis(100);
            }
            // if pool is now still not empty -> shutdown executor
            if (!((ThreadPoolExecutor) executorService).getQueue().isEmpty()){
                executorService.shutdown();
                return;
            }
        }

    }


    public static void register(EventBusClient client, Object... eventTypes) {
        for (Object evt : eventTypes) {
            subscribersByEventType.computeIfAbsent(evt, k -> new HashSet<>());
            subscribersByEventType.get(evt).add(client);
            uniqueClients.add(client);

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
            for (EventBusClient client : subs) {
                client.getEventsQueue().add(event);
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

