package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBus {

    @Getter
    private static final ConcurrentHashMap<IEventType, ConcurrentHashMap.KeySetView<EventBusClient,Boolean>> subscribersByEventType = new ConcurrentHashMap<>();
    @Getter
    private static final ConcurrentHashMap.KeySetView<EventBusClient,Boolean> uniqueClients = ConcurrentHashMap.newKeySet();
    @Getter
    private static final List<Event> deadEvents = new CopyOnWriteArrayList<>();
    @Getter
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void shutdown() {
        log.info("Shutting down the EventBus");
        // stop all clients
        stopAllClients();
        // finally clear the lists (they're static) just in case
        clearState();
    }

    /**
     * Subscribe event types to react to
     * @param client client that is registering
     * @param eventTypes event types
     */
    public static void subscribe(EventBusClient client, IEventType... eventTypes) {
        for (IEventType evt : eventTypes) {
            subscribersByEventType.computeIfAbsent(evt, k -> ConcurrentHashMap.newKeySet()).add(client);
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
            for (EventBusClient client : subscribersByEventType.get(evt)) {
                client.getEventsQueue().add(event);
            }
        } else {
            deadEvents.add(event); // do we really need dead events? if nothing is subscribed to the event, just ignore it, disregard
        }
    }

    /**
     * Publish event
     *
     * @param eventType event type
     * @param payload   payload object
     */
    public static void publish(IEventType eventType, Object payload) {
        Event event = new Event(eventType, payload);
        publish(event);
    }

    private static void clearState() {
        uniqueClients.clear();
        deadEvents.clear();
        subscribersByEventType.clear();
    }

    private static void stopAllClients() {
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
}


