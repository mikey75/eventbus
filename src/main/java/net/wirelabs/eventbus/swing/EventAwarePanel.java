package net.wirelabs.eventbus.swing;

import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBus;
import net.wirelabs.eventbus.EventBusClient;
import net.wirelabs.eventbus.IEventType;

import javax.swing.*;
import java.util.Collection;


public abstract class EventAwarePanel extends JPanel  {

    private transient EventBusClient eventBusClient;
    protected EventAwarePanel() {
        eventHandlerInitialize();
    }
    protected abstract void onEvent(Event evt);
    protected abstract Collection<IEventType> subscribeEvents();

    protected void subscribe(IEventType ...events) {
        for (IEventType evt: events) {
            EventBus.register(eventBusClient,evt);
        }
    }

    private void eventHandlerInitialize() {

        eventBusClient = new EventBusClient() {
            @Override
            public void onEvent(Event evt) {
                SwingUtilities.invokeLater(() -> EventAwarePanel.this.onEvent(evt));
            }
        };

        for (IEventType evt : subscribeEvents()) {
            EventBus.register(eventBusClient, evt);
        }

    }
}

