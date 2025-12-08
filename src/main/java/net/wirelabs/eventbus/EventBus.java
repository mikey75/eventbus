package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventBus {
    @Getter
    private static final Set<EventBusClient> clients = Collections.synchronizedSet(new HashSet<>());
    @Getter
    private static final Set<EventBusSwingClient> swingClients = Collections.synchronizedSet(new HashSet<>());
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // non-swing clients
    public static void registerClient(EventBusClient client) {
        clients.add(client);
    }
    // swing clients
    public static void registerClient(EventBusSwingClient client) {
        swingClients.add(client);
    }

    public static CompletableFuture<Void> publish(Event<?> evt) {
        return CompletableFuture.runAsync(() -> dispatch(evt), executor);
    }

    public static void dispatch(Event<?> event) {
        synchronized (clients) {
            for (EventBusClient client : clients) {
                if (client.getSubscribedEvents().stream().anyMatch(cls -> cls.isAssignableFrom(event.getClass()))) {
                    client.dispatch(event);
                }
            }
        }
        synchronized (swingClients) {
            for (EventBusSwingClient client : swingClients) {
                if (client.getSubscribedEvents().stream().anyMatch(cls -> cls.isAssignableFrom(event.getClass()))) {
                    client.dispatch(event);
                }
            }
        }
    }

    static void submit(Runnable task) {
        executor.submit(task);
    }

    public static void reset() {
        clients.clear();
        swingClients.clear();

    }

    public static List<Object> getAllClients() {
        List<Object> list = new ArrayList<>();
        list.addAll(clients);
        list.addAll(swingClients);
        return list;
    }
}