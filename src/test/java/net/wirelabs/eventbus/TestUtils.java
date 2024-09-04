package net.wirelabs.eventbus;

import org.assertj.core.api.Assertions;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class TestUtils {

    public static final String EVENT_2 = "event2";
    public static final String EVENT_1 = "event1";
    public static final String EVENT_3 = "event3";
    public static final String EVENT_4 = "event4";

    public static void shutdownAndAssertFinishedClients(EventBusClient ... clients) {
        EventBus.shutdown();
        for (EventBusClient c: clients) {
            Assertions.assertThat(c.getThreadHandle().isDone()).isTrue();
        }
    }
}
