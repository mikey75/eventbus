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
    private static final Map<IEventType, Set<EventBusClient>> subscribersByEventType = new HashMap<>();
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
            // actually in normal work it won't happen since all clients
            // will be stopped and removed from queue but for completeness
            while (!client.getThreadHandle().isDone()) {
                Sleeper.sleepMillis(100);
            }

        }
        // if pool is still not empty -> shutdown executor the hard way
        if (!((ThreadPoolExecutor) executorService).getQueue().isEmpty()){
            executorService.shutdown();
        }

    }


    public static void register(EventBusClient client, IEventType... eventTypes) {
        for (IEventType evt : eventTypes) {
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

        IEventType evt = event.getEventType();

        if (subscribersByEventType.containsKey(evt)) {
            Set<EventBusClient> subs = subscribersByEventType.get(evt);
            for (EventBusClient client : subs) {
                client.getEventsQueue().add(event);
            }
        } else {
            deadEvents.add(event); // do we really need dead events? if nothing is subscribed to the event, just ignore it, diregard
        }
    }

    /**
     * Publish event
     *
     * @param eventType event type
     * @param payload   payload object
     */
    public static void publish(IEventType eventType, Object payload) {
        // get all listners subscribed to the event
        Event event = new Event(eventType, payload);
        publish(event);


    }
}

