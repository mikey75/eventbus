package net.wirelabs.eventbus;

/**
 * Created 9/15/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
public interface EventBusClient {

    default void subsrcibeEvents(EventBusListener listener , IEventType... eventTypes) {
        EventBus.register(listener, eventTypes);
    }

    default void registerListener(EventBusListener listener) {
        EventBus.newListener(listener);
    }

    default void unsubscibeEvents(EventBusListener listener, IEventType... eventTypes){
        EventBus.unregister(listener, eventTypes);
    }
}
