package net.wirelabs.eventbus.swing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.eventbus.*;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class EventAwarePanel extends JPanel {

    protected abstract void onEvent(Event evt);

    protected abstract Collection<IEventType> subscribeEvents();

    private final transient EventBusClient client = new EventBusClient() {
        @Override
        public void onEvent(Event evt) {
            EventAwarePanel.this.onEvent(evt);
        }

        @Override
        public Collection<IEventType> subscribeEvents() {
            Collection<IEventType> events = EventAwarePanel.this.subscribeEvents();
            return events != null ? events : Collections.emptyList();
        }
    };


    public void subscribe(IEventType... eventTypes) {
        EventBus.subscribe(client, eventTypes);
    }
}

