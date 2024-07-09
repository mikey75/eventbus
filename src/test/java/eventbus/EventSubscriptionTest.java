package eventbus;

import net.wirelabs.evbus.eventbus.EventBus;
import net.wirelabs.evbus.eventbus.EventBusClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static eventbus.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 6/21/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class EventSubscriptionTest {

    @BeforeEach
    void before() {
        EventBus.getSubscribersByEventType().clear();
    }

    @Test
    void shouldSubscribeMultipleListenersToMultipleEvents() {

        // given
        EventBusClient listener1 = new TesteEventBusClient();
        EventBusClient listener2 = new TesteEventBusClient();
        EventBusClient listener3 = new TesteEventBusClient();

        // when
        listener1.subscribe(EVENT_1,EVENT_2,EVENT_3);
        listener2.subscribe(EVENT_1);
        listener3.subscribe(EVENT_1,EVENT_4);

        //then
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_1)).containsOnly(listener1, listener2, listener3);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_2)).containsOnly(listener1);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_3)).containsOnly(listener1);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_4)).containsOnly(listener3);

        EventBus.shutdown();
        shutdownAndAssertFinishedClients(listener1,listener3,listener2);


    }

    @Test
    void shouldRegisterTwoEvents() {
        // given
        EventBusClient listener2 = new TesteEventBusClient();
        EventBusClient listener3 = new TesteEventBusClient();

        // given
        listener2.subscribe(EVENT_1, EVENT_2);
        listener3.subscribe(EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(EVENT_1)).containsOnly(listener2, listener3);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_2)).containsOnly(listener2);
        shutdownAndAssertFinishedClients(listener2,listener3);

    }

    @Test
    void shouldIgnoreSameEventTypeOnSubscriber() {
        EventBusClient listener1 = new TesteEventBusClient();

        listener1.subscribe(EVENT_1);
        listener1.subscribe(EVENT_1);
        listener1.subscribe(EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(EVENT_1)).containsOnly(listener1);
        shutdownAndAssertFinishedClients(listener1);

    }

    @Test
    void shouldIgnoreRegistrationWithoutEvent() {
        EventBusClient listener1 = new TesteEventBusClient();
        listener1.subscribe(); // no event
        assertThat(listener1.getEventsQueue()).isEmpty();
    }

}
