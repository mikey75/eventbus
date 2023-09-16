package net.wirelabs.eventbus;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static net.wirelabs.eventbus.EventTypes.*;
import static org.assertj.core.api.Assertions.assertThat;

class EventPublishAndConsumeTest extends BaseTest {

    @Test
    void shouldPublishEventToAllSubscribers() {

        // given
        EventListener listener1 = new EventListener();
        EventListener listener2 = new EventListener();
        EventListener listener3 = new EventListener();

        // when
        listener1.subscribe(EVENT_1, EVENT_2);
        listener2.subscribe(EVENT_1, EVENT_3);
        listener3.subscribe(EVENT_1);

        EventBus.publish(ev1, ev2, ev2, ev2, ev3, ev4);

        // then
        waitAllEventsProcessed(listener1, listener2, listener3);

        // check nothing consumed ev4 (nothing subscribed to it)
        assertNotConsumed(listener1, ev4);
        assertNotConsumed(listener2, ev4);
        assertNotConsumed(listener3, ev4);

        // now check if processed events are what we subscribed to
        assertConsumed(listener1, ev1, ev2, ev2, ev2);
        assertConsumed(listener2, ev1, ev3);
        assertConsumed(listener3, ev1);


        stopListeners(listener1, listener2, listener3);
    }

    @Test
    void shouldNotServiceUnsubscribedEvent() {
        // check ignore event if not subscribed initially
        EventBus.publish(ev1);

        EventListener listener1 = new EventListener();
        listener1.subscribe(EVENT_1, EVENT_2);
        EventBus.publish(ev1, ev2);

        listener1.unsubscribe(EVENT_1);
        EventBus.publish(ev1);

        waitAllEventsProcessed(listener1);
        assertConsumed(listener1, ev1, ev2);

        stopListeners(listener1);
    }

    @Test
    void checkPublishWithEventTypeAndPayload() {
        EventListener listener1 = new EventListener();
        listener1.subscribe(EVENT_1);

        EventBus.publish(EVENT_1, "123");

        waitAllEventsProcessed(listener1);

        // since event object (from type+paylod constructor)
        // is created dynamically in event bus, we dont have a reference here
        // so need to inspect exact consumed object
        assertThat(listener1.eventsConsumed).hasSize(1);
        assertThat(listener1.eventsConsumed.get(0).getPayload()).isEqualTo("123");

        stopListeners(listener1);
    }

    @Test
    void stop() {
        EventListener listener1 = new EventListener();
        EventListener listener2 = new EventListener();
        EventListener listener3 = new EventListener();

        EventBus.stop();
        stopListeners(listener1, listener2, listener3);

        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            assertThat(listener1.listenerThread).isDone();
            assertThat(listener2.listenerThread).isDone();
            assertThat(listener3.listenerThread).isDone();
        });
    }

    @Test
    void testSubscriptionInsideListener() {

        EventListener listener = new EventListener() {

            @Override
            protected Collection<IEventType> subscribeTo() {
                return Arrays.asList(EVENT_1, EVENT_2);
            }
        };

        EventBus.publish(ev1, ev2);

        waitAllEventsProcessed(listener);
        assertConsumed(listener, ev1, ev2); //ev3 not consumed since not subscribed to

        stopListeners(listener);
    }
}
