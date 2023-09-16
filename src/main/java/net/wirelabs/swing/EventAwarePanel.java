package net.wirelabs.swing;

import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBusListener;
import net.wirelabs.eventbus.IEventType;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.Collection;

public abstract class EventAwarePanel extends JPanel {

    final transient EventBusListener eventListener;

    protected EventAwarePanel() {
        eventListener = new EventBusListener() {
            @Override
            protected void onEvent(Event event) {
                SwingUtilities.invokeLater(() -> EventAwarePanel.this.onEvent(event));
            }
        };

        for (IEventType eventType : subscribeEvents()) {
            eventListener.subscribe(eventType);
        }
    }

    protected abstract void onEvent(Event evt);

    protected abstract Collection<IEventType> subscribeEvents();

}
