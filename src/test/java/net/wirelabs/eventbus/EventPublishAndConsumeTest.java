package net.wirelabs.eventbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class EventPublishAndConsumeTest {


    @BeforeEach
    void before() {
        EventBus.getSubscribersByEventType().clear();
        EventBus.getUniqueClients().clear();
        EventBus.getDeadEvents().clear();
    }

    @Test
    void shouldPublishEventToAllSubscribersByEventType() {

        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();

        // test subscription by eventtype
        client1.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);
        client2.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);

        client3.subscribe(TestUtils.EVENT_1);

        EventBus.publish(TestUtils.EVENT_1,"x");
        EventBus.publish(TestUtils.EVENT_2,"x");
        EventBus.publish(TestUtils.EVENT_2,"x");
        EventBus.publish(TestUtils.EVENT_2,"x");
        EventBus.publish(TestUtils.EVENT_2,"x");

        Sleeper.sleepSeconds(1);
        List<Object> client1EventTypes = client1.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());
        List<Object> client2EventTypes = client2.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());
        List<Object> client3EventTypes = client3.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());

        assertThat(client1EventTypes).containsExactly(TestUtils.EVENT_1, TestUtils.EVENT_2, TestUtils.EVENT_2, TestUtils.EVENT_2, TestUtils.EVENT_2);
        assertThat(client2EventTypes).containsExactly(TestUtils.EVENT_1, TestUtils.EVENT_2, TestUtils.EVENT_2, TestUtils.EVENT_2, TestUtils.EVENT_2);
        assertThat(client3EventTypes).containsOnly(TestUtils.EVENT_1);
        TestUtils.shutdownAndAssertFinishedClients(client1,client2,client3);

    }

    @Test
    void shouldPublishEventToAllSubscribersByEventObject() {
        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();
        Event ev1 = new Event(TestUtils.EVENT_1,"ev1");
        Event ev2 = new Event(TestUtils.EVENT_2,"ev2");

        // test subscription by eventtype
        client1.subscribe(ev1, ev2);
        client2.subscribe(ev1, ev2);
        client3.subscribe(ev1);

        EventBus.publish(ev1);
        EventBus.publish(ev2);
        EventBus.publish(ev2);
        EventBus.publish(ev2);
        EventBus.publish(ev2);

        Sleeper.sleepSeconds(1);
        assertThat(client1.eventsConsumed).containsExactly(ev1,ev2,ev2,ev2,ev2);
        assertThat(client2.eventsConsumed).containsExactly(ev1,ev2,ev2,ev2,ev2);
        assertThat(client3.eventsConsumed).containsOnly(ev1);
        TestUtils.shutdownAndAssertFinishedClients(client1,client2,client3);
    }

    @Test
    void shouldRegisterDeadEventWhenPublishingBeforeASubscriberIsSubscribed() {
        Object obj = "1234";
        Event ev = new Event(TestUtils.EVENT_1, obj);
        EventBus.publish(ev);
        assertThat(EventBus.getDeadEvents()).hasSize(1);
        assertThat(EventBus.getDeadEvents().get(0).getEventType()).isEqualTo(TestUtils.EVENT_1);
        assertThat(EventBus.getDeadEvents().get(0).getPayload()).isEqualTo(obj);

    }

    @Test
    void testStop() {

        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();

        // first subscribe some unique clients
        client1.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);
        client2.subscribe(TestUtils.EVENT_1, TestUtils.EVENT_2);
        client3.subscribe(TestUtils.EVENT_1);

        assertThat(EventBus.getUniqueClients()).hasSize(3);
        TestUtils.shutdownAndAssertFinishedClients(client1,client2,client3);

    }
}
