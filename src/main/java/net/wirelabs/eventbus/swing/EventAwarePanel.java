package net.wirelabs.eventbus.swing;

import net.wirelabs.eventbus.*;

import javax.swing.*;
import java.util.Collection;


public abstract class EventAwarePanel extends JPanel  {

    protected abstract void onEvent(Event evt);
    protected abstract Collection<IEventType> subscribeEvents();

    private final transient EventBusClient client;

    protected EventAwarePanel() {

        client = new EventBusClient() {

            @Override
            public void onEvent(Event evt) {
                SwingUtilities.invokeLater(() -> EventAwarePanel.this.onEvent(evt));
            }

            @Override
            public Collection<IEventType> subscribeEvents() {
                return EventAwarePanel.this.subscribeEvents();
            }

        };
    }

    public void subscribe(IEventType ... eventTypes) {
        EventBus.register(client, eventTypes);
    }
}

