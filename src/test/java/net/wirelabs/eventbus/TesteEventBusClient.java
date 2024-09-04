package net.wirelabs.eventbus;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class TesteEventBusClient extends EventBusClient {

    final List<Event> eventsConsumed = new ArrayList<>();

    @Override
    public void onEvent(Event evt) {
        log.info("Event received {}", evt.getEventType());
        eventsConsumed.add(evt);

    }


}
