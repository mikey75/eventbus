package net.wirelabs.eventbus.testclients;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.EventBusClient;
import net.wirelabs.eventbus.IEventType;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@NoArgsConstructor
public class TestClient extends EventBusClient {

    @Override
    public void onEvent(Event evt) {
        log.info("{} reports: {}-{}", this ,evt.getEventType(), evt.getPayload());
    }

    @Override
    public Collection<IEventType> subscribeEvents() {
        return Collections.emptyList();
    }

}
