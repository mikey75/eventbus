package net.wirelabs.eventbus.testclients;

import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBusClient;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.eventbus.common.EventTypes;

import java.util.Collection;
import java.util.List;

@Slf4j
public class TestClientWithInitialSubscription extends EventBusClient {
    @Override
    public void onEvent(Event evt) {
        log.info("{} reports: {}-{}", this ,evt.getEventType(), evt.getPayload());
    }

    @Override
    public Collection<IEventType> subscribeEvents() {
        return List.of(EventTypes.EVENT_1,EventTypes.EVENT_2);
    }
}
