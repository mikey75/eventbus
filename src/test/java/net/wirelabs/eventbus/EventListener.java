package net.wirelabs.eventbus;

import java.util.ArrayList;
import java.util.List;

public class EventListener extends EventBusListener {
    List<Event> eventsConsumed = new ArrayList<>();

    @Override
    protected void onEvent(Event evt) {
        eventsConsumed.add(evt);
    }
}