package eventbus;

import net.wirelabs.evbus.eventbus.Event;
import net.wirelabs.evbus.eventbus.EventBus;
import net.wirelabs.evbus.eventbus.Sleeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.stream.Collectors;

import static eventbus.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;


class EventPublishAndConsumeTest {


    @BeforeEach
    void before() {
        EventBus.getSubscribersByEventType().clear();
        EventBus.getUniqueListeners().clear();
        EventBus.getDeadEvents().clear();
    }

    @Test
    void shouldPublishEventToAllSubscribersByEventType() {

        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();

        // test subscription by eventtype
        client1.subscribe(EVENT_1, EVENT_2);
        client2.subscribe(EVENT_1, EVENT_2);

        client3.subscribe(EVENT_1);

        EventBus.publish(EVENT_1,"x");
        EventBus.publish(EVENT_2,"x");
        EventBus.publish(EVENT_2,"x");
        EventBus.publish(EVENT_2,"x");
        EventBus.publish(EVENT_2,"x");

        Sleeper.sleepSeconds(1);
        List<Object> client1EventTypes = client1.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());
        List<Object> client2EventTypes = client2.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());
        List<Object> client3EventTypes = client3.eventsConsumed.stream().map(Event::getEventType).collect(Collectors.toList());

        assertThat(client1EventTypes).containsExactly(EVENT_1,EVENT_2,EVENT_2,EVENT_2,EVENT_2);
        assertThat(client2EventTypes).containsExactly(EVENT_1,EVENT_2,EVENT_2,EVENT_2,EVENT_2);
        assertThat(client3EventTypes).containsOnly(EVENT_1);
        shutdownAndAssertFinishedClients(client1,client2,client3);

    }

    @Test
    void shouldPublishEventToAllSubscribersByEventObject() {
        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();
        Event ev1 = new Event(EVENT_1,"ev1");
        Event ev2 = new Event(EVENT_2,"ev2");

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
        shutdownAndAssertFinishedClients(client1,client2,client3);
    }

    @Test
    void shouldRegisterDeadEventWhenPublishingBeforeASubscriberIsSubscribed() {
        Object obj = "1234";
        Event ev = new Event(EVENT_1, obj);
        EventBus.publish(ev);
        assertThat(EventBus.getDeadEvents()).hasSize(1);
        assertThat(EventBus.getDeadEvents().get(0).getEventType()).isEqualTo(EVENT_1);
        assertThat(EventBus.getDeadEvents().get(0).getPayload()).isEqualTo(obj);

    }

    @Test
    void testStop() {

        TesteEventBusClient client1 = new TesteEventBusClient();
        TesteEventBusClient client2 = new TesteEventBusClient();
        TesteEventBusClient client3 = new TesteEventBusClient();

        // first subscribe some unique listeners
        client1.subscribe(EVENT_1, EVENT_2);
        client2.subscribe(EVENT_1, EVENT_2);
        client3.subscribe(EVENT_1);

        assertThat(EventBus.getUniqueListeners()).hasSize(3);
        shutdownAndAssertFinishedClients(client1,client2,client3);

    }
}
