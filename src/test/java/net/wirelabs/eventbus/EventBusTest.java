package net.wirelabs.eventbus;

import net.wirelabs.eventbus.common.BaseTest;
import net.wirelabs.eventbus.common.EventTypes;
import net.wirelabs.eventbus.testclients.TestClient;
import net.wirelabs.eventbus.testclients.TestClientWithInitialSubscription;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static net.wirelabs.eventbus.common.EventTypes.*;
import static org.assertj.core.api.Assertions.assertThat;

class EventBusTest extends BaseTest {


    private static final String EV1_PAYLOAD = "ev1";
    private static final String EV2_PAYLOAD = "ev2";
    private static final String EV3_PAYLOAD = "ev3";
    private static final String EV4_PAYLOAD = "ev4";



    @Test
    void eventSubscriptionTest() {
        // subscribe by class
        EventBusClient client1 = new TestClient();
        EventBusClient client2 = new TestClient();

        client1.subscribe(EVENT_1, EVENT_2, EVENT_4);
        client2.subscribe(EVENT_4, EVENT_3);

        // check if bus registered clients and their event subscriptions correctly
        assertThat(EventBus.getUniqueClients()).containsOnly(client1, client2);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_1)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_2)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_3)).containsOnly(client2);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_4)).containsOnly(client1, client2);

        // since no event were published - check if event queue is empty
        assertThat(client1.getEventsQueue()).isEmpty();
        assertThat(client2.getEventsQueue()).isEmpty();

    }

    @Test
    void shouldSubscribeOnceIfMultipleCallsWithSameEvent() {
        EventBusClient client1 = new TestClient();

        client1.subscribe(EventTypes.EVENT_1);
        client1.subscribe(EventTypes.EVENT_1);
        client1.subscribe(EventTypes.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_1)).containsOnly(client1);

    }

    @Test
    void deadEventTest() {
        // do not subscribe to anything
        EventBusClient client1 = new TestClient();

        // publish some events
        EventBus.publish(EVENT_1, EV1_PAYLOAD);
        EventBus.publish(EVENT_2, EV2_PAYLOAD);
        EventBus.publish(EVENT_3, EV3_PAYLOAD);

        // these events should land in deadevents collection
        // and client should not be registered in evbus
        assertThat(EventBus.getUniqueClients()).isEmpty();
        assertThat(client1.getEventsQueue()).isEmpty();

        assertThat(EventBus.getDeadEvents().stream()
                .map(Event::getEventType).toList())
                .isNotEmpty()
                .containsOnly(EVENT_1, EVENT_2, EVENT_3);

        // publish before client even exists
        EventBus.publish(EVENT_4, EV4_PAYLOAD);
        EventBusClient client2 = new TestClient();
        client2.subscribe(EVENT_4);

        assertThat(EventBus.getDeadEvents().stream()
                .map(Event::getEventType).toList())
                .isNotEmpty()
                .contains(EVENT_4);
    }

    @Test
    void twoClientsShouldRespondToTheSameEvent() {

        EventBusClient client1 = new TestClient();
        EventBusClient client2 = new TestClient();
        client1.subscribe(EVENT_1);
        client2.subscribe(EVENT_1);

        assertThat(EventBus.getUniqueClients()).containsOnly(client1, client2);
        assertThat(EventBus.getSubscribersByEventType().get(EVENT_1)).containsOnly(client1, client2);

        // publish event
        EventBus.publish(EVENT_1, EV1_PAYLOAD);

        // wait until all events are serviced
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            assertThat(client1.getEventsQueue()).isEmpty();
            assertThat(client2.getEventsQueue()).isEmpty();
        });

        logVerifier.verifyLogged(client2 + " reports: " + EVENT_1 + "-" + EV1_PAYLOAD);
        logVerifier.verifyLogged(client1 + " reports: " + EVENT_1 + "-" + EV1_PAYLOAD);

    }

    @Test
    void shouldSubscribeMultipleClientsToMultipleEventsAndReactToPublish() {

        // given
        EventBusClient client1 = new TestClient();
        EventBusClient client2 = new TestClient();
        EventBusClient client3 = new TestClient();

        // when
        client1.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_2, EventTypes.EVENT_3);
        client2.subscribe(EventTypes.EVENT_1);
        client3.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_4);

        //then
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_1)).containsOnly(client1, client2, client3);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_2)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_3)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_4)).containsOnly(client3);

        EventBus.publish(EVENT_1, EV1_PAYLOAD);

        EventBus.publish(EVENT_2, EV2_PAYLOAD);
        EventBus.publish(EVENT_3, EV3_PAYLOAD);
        EventBus.publish(EVENT_4, EV4_PAYLOAD);

        // wait until all events are serviced
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> {

            assertThat(client1.getEventsQueue()).isEmpty();
            assertThat(client1.getEventsQueue()).isEmpty();
            assertThat(client1.getEventsQueue()).isEmpty();
        });


        // check if the onEvent was called - cant use Mockito.spy here since the onevent is done on different thread
        logVerifier.verifyLogged(client1 + " reports: " + EVENT_1 + "-" + EV1_PAYLOAD);
        logVerifier.verifyLogged(client1 + " reports: " + EVENT_2 + "-" + EV2_PAYLOAD);
        logVerifier.verifyLogged(client1 + " reports: " + EVENT_3 + "-" + EV3_PAYLOAD);

        logVerifier.verifyLogged(client2 + " reports: " + EVENT_1 + "-" + EV1_PAYLOAD);

        logVerifier.verifyLogged(client3 + " reports: " + EVENT_1 + "-" + EV1_PAYLOAD);
        logVerifier.verifyLogged(client3 + " reports: " + EVENT_4 + "-" + EV4_PAYLOAD);
    }

    @Test
    void testClientWithInitialSubscription() {
        EventBusClient client = new TestClientWithInitialSubscription(); // client is subscribed to event1 and event2

        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_1)).containsOnly(client);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_2)).containsOnly(client);

    }
    @Test
    void shouldSubscribeMultipleClientsToMultipleEvents() {

        // given
        EventBusClient client1 = new TestClient();
        EventBusClient client2 = new TestClient();
        EventBusClient client3 = new TestClient();

        // when
        client1.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_2, EventTypes.EVENT_3);
        client2.subscribe(EventTypes.EVENT_1);
        client3.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_4);

        //then
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_1)).containsOnly(client1, client2, client3);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_2)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_3)).containsOnly(client1);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_4)).containsOnly(client3);

    }

    @Test
    void shouldRegisterTwoEvents() {
        // given
        EventBusClient client2 = new TestClient();
        EventBusClient client3 = new TestClient();

        // given
        client2.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_2);
        client3.subscribe(EventTypes.EVENT_1);

        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_1)).containsOnly(client2, client3);
        assertThat(EventBus.getSubscribersByEventType().get(EventTypes.EVENT_2)).containsOnly(client2);


    }

    @Test
    void shouldResetAndShutdownBus() {
        // initial state
        EventBusClient client2 = new TestClient();
        EventBusClient client3 = new TestClient();
        client2.subscribe(EventTypes.EVENT_1, EventTypes.EVENT_2);
        client3.subscribe(EventTypes.EVENT_1);
        // emit dead event
        EventBus.publish(EVENT_4,null);

        // assert state
        assertThat(EventBus.getUniqueClients()).isNotEmpty();
        assertThat(EventBus.getSubscribersByEventType()).isNotEmpty();
        assertThat(EventBus.getDeadEvents()).isNotEmpty();

        // emit reset
        EventBus.reset();

        // assert all reset
        Awaitility.waitAtMost(Duration.ofSeconds(2)).untilAsserted(() -> {
            assertThat(EventBus.getDeadEvents()).isEmpty();
            assertThat(EventBus.getUniqueClients()).isEmpty();
            assertThat(EventBus.getSubscribersByEventType()).isEmpty();
            logVerifier.verifyLogged("Resetting EventBus to initial state");
        });
    }

}
