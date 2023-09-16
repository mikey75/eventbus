package net.wirelabs.eventbus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class EventBusClient {
    
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);
    
    protected void runContinuously(Runnable serviceCode) {
        
        while (!shouldStop.get()) {
            serviceCode.run();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
    }

    protected void stop() {
        shouldStop.set(true);
    }

    protected void subsrcibeEvents(EventBusListener listener , IEventType... eventTypes) {
        EventBus.register(listener, eventTypes);
    }

    protected void registerListener(EventBusListener listener) {
        EventBus.newListener(listener);
    }

    protected void unsubscibeEvents(EventBusListener listener, IEventType... eventTypes){
        EventBus.unregister(listener, eventTypes);
    }
}
