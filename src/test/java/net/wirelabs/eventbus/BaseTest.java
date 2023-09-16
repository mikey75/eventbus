package net.wirelabs.eventbus;

import org.awaitility.core.ThrowingRunnable;

import java.time.Duration;

import static net.wirelabs.eventbus.EventTypes.*;
import static net.wirelabs.eventbus.EventTypes.EVENT_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public abstract class BaseTest {

    protected final Event ev1 = new Event(EVENT_1, "ev1");
    protected final Event ev2 = new Event(EVENT_2, "ev2");
    protected final Event ev3 = new Event(EVENT_3, "ev3");
    protected final Event ev4 = new Event(EVENT_4, "ev4");


    protected void waitUntilAsserted(Duration duration, ThrowingRunnable assertion) {
        await().atMost(duration).untilAsserted(assertion);
    }

    protected void stopListeners(EventBusListener... listeners) {
        for (EventBusListener listener: listeners) {
            listener.stop();
        }
        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            for (EventBusListener listener : listeners) {
                assertThat(listener.listenerThread).isDone();
            }
        });
    }

    protected void assertConsumed(EventListener listener, Event... events) {
        waitUntilAsserted(Duration.ofSeconds(1), () -> assertThat(listener.eventsConsumed)
                .containsOnly(events));
    }

    protected void assertNotConsumed(EventListener listener, Event... events) {
        waitUntilAsserted(Duration.ofSeconds(1), () -> assertThat(listener.eventsConsumed)
                .isNotEmpty()
                .doesNotContain(events));
    }

    protected void waitAllEventsProcessed(EventBusListener... listeners) {
        waitUntilAsserted(Duration.ofSeconds(1), () -> {
            for (EventBusListener listener : listeners) {
                assertThat(listener.queueSize()).isZero();
            }
        });
    }
}
