package net.wirelabs.eventbus;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.*;

public class EventBusSwingClient extends JPanel {

    private final Map<Class<? extends Event<?>>, Method> eventHandlers = new HashMap<>();

    protected EventBusSwingClient() {
        scanEventHandlers();
        EventBus.registerClient(this); // auto-register
    }

    private void scanEventHandlers() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                    throw new IllegalArgumentException("@OnEvent methods must have exactly one Event parameter");
                }
                @SuppressWarnings("unchecked")
                Class<? extends Event<?>> eventClass = (Class<? extends Event<?>>) params[0];
                method.setAccessible(true);
                eventHandlers.put(eventClass, method);
            }
        }
    }

    public Collection<Class<? extends Event<?>>> getSubscribedEvents() {
        return Collections.unmodifiableSet(eventHandlers.keySet());
    }

    void dispatch(Event<?> event) {


        List<Method> handlers = new ArrayList<>();
        for (Map.Entry<Class<? extends Event<?>>, Method> entry : eventHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(event.getClass())) {
                handlers.add(entry.getValue());
            }
        }

        for (Method method : handlers) {
            Runnable eventTask = Util.createTask(this,method,event);
            Util.serviceEvent(eventTask);
        }
    }

}