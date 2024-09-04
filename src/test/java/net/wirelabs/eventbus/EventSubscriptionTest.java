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
    void shouldSubscribeMultipleClientsToMultipleEvents() {

        // given
        EventBusClient client1 = new TesteEventBusClient();
        EventBusClient client2 = new TesteEventBusClient();
        EventBusClient client3 = new TesteEventBusClient();

        // when
        client1.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2, TestUtils.EVENT_3);
        client2.subscribe(TestUtils.EVENT_1);
        client3.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_4);

        //then
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(client1, client2, client3);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_2)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_3)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_4)).containsOnly(client3);

        EventBus.shutdown();
        TestUtils.shutdownAndAssertFinishedClients(client1,client3,client2);


    }

    @Test
    void shouldRegisterTwoEvents() {
        // given
        EventBusClient client2 = new TesteEventBusClient();
        EventBusClient client3 = new TesteEventBusClient();

        // given
        client2.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);
        client3.subscribe(TestUtils.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(client2, client3);
        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_2)).containsOnly(client2);
        TestUtils.shutdownAndAssertFinishedClients(client2,client3);

    }

    @Test
    void shouldIgnoreSameEventTypeOnSubscriber() {
        EventBusClient client1 = new TesteEventBusClient();

        client1.subscribe(TestUtils.EVENT_1);
        client1.subscribe(TestUtils.EVENT_1);
        client1.subscribe(TestUtils.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(TestUtils.EVENT_1)).containsOnly(client1);
        TestUtils.shutdownAndAssertFinishedClients(client1);

    }

    @Test
    void shouldIgnoreRegistrationWithoutEvent() {
        EventBusClient client1 = new TesteEventBusClient();
        client1.subscribe(); // no event
        assertThat(client1.getEventsQueue()).isEmpty();
    }

}
