package net.wirelabs.eventbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        listener1.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2, TestUtils.EVENT_3);
        listener2.subscribe(TestUtils.EVENT_1);
        listener3.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_4);

        //then
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(listener1, listener2, listener3);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_2)).containsOnly(listener1);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_3)).containsOnly(listener1);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_4)).containsOnly(listener3);

        EventBus.shutdown();
        TestUtils.shutdownAndAssertFinishedClients(listener1,listener3,listener2);


    }

    @Test
    void shouldRegisterTwoEvents() {
        // given
        EventBusClient listener2 = new TesteEventBusClient();
        EventBusClient listener3 = new TesteEventBusClient();

        // given
        listener2.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);
        listener3.subscribe(TestUtils.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(listener2, listener3);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_2)).containsOnly(listener2);
        TestUtils.shutdownAndAssertFinishedClients(listener2,listener3);

    }

    @Test
    void shouldIgnoreSameEventTypeOnSubscriber() {
        EventBusClient listener1 = new TesteEventBusClient();

        listener1.subscribe(TestUtils.EVENT_1);
        listener1.subscribe(TestUtils.EVENT_1);
        listener1.subscribe(TestUtils.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(listener1);
        TestUtils.shutdownAndAssertFinishedClients(listener1);

    }

    @Test
    void shouldIgnoreRegistrationWithoutEvent() {
        EventBusClient listener1 = new TesteEventBusClient();
        listener1.subscribe(); // no event
        assertThat(listener1.getEventsQueue()).isEmpty();
    }

}
