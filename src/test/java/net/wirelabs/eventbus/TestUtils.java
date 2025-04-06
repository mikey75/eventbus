package net.wirelabs.eventbus;

import org.assertj.core.api.Assertions;

/**
 * Created 8/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class TestUtils {

    public static void shutdownAndAssertFinishedClients(EventBusClient ... clients) {
        EventBus.shutdown();
        for (EventBusClient c: clients) {
            Assertions.assertThat(c.getThreadHandle().isDone()).isTrue();
            Assertions.assertThat(EventBus.getUniqueClients()).isEmpty();
            Assertions.assertThat(EventBus.getDeadEvents()).isEmpty();
            Assertions.assertThat(EventBus.getSubscribersByEventType()).isEmpty();
        }
    }
}
