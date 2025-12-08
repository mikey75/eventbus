package net.wirelabs.eventbus;


import net.wirelabs.eventbus.common.BaseTest;
import net.wirelabs.eventbus.evbus2.events.MapChangedEvent;
import net.wirelabs.eventbus.evbus2.testclients.TestSwingClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class EventAwarePanelTest extends BaseTest {

    @Test
    void testEventAwarePanelEventSubscription() {
        TestSwingClient panel = new TestSwingClient();
        // check client subscription and Evbus internals
        assertThat(panel.getSubscribedEvents()).hasSize(1);
        assertThat(EventBus.getSwingClients()).hasSize(1);
        assertThat(EventBus.getSwingClients()).containsOnly(panel);

        assertThat(panel.getTextField().getText()).isNullOrEmpty();
        EventBus.publish(new MapChangedEvent("mapa #2"));
        Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            assertThat(panel.getTextField().getText()).isEqualTo("mapa #2");
        });
        EventBus.publish(new MapChangedEvent("mapa #3"));
        Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            assertThat(panel.getTextField().getText()).isEqualTo("mapa #3");
        });


    }
}


