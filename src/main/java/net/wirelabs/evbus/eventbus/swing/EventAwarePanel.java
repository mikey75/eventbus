package net.wirelabs.evbus.eventbus.swing;

import net.wirelabs.evbus.eventbus.Event;
import net.wirelabs.evbus.eventbus.EventBus;
import net.wirelabs.evbus.eventbus.EventBusClient;

import javax.swing.*;
import java.util.Collection;


public abstract class EventAwarePanel extends JPanel  {

    private EventBusClient eventListener;
    protected EventAwarePanel() {
        eventHandlerInitialize();
    }
    protected abstract void onEvent(Event evt);
    protected abstract Collection<Object> subscribeEvents();

    protected void subscribe(Object ...events) {
        for (Object evt: events) {
            EventBus.register(eventListener,evt);
        }
    }

    private void eventHandlerInitialize() {

        eventListener = new EventBusClient() {
            @Override
            public void onEvent(Event evt) {
                SwingUtilities.invokeLater(() -> EventAwarePanel.this.onEvent(evt));
            }
        };

        for (Object evt : subscribeEvents()) {
            EventBus.register(eventListener, evt);
        }

    }
}

