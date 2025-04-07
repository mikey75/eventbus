package net.wirelabs.eventbus;


import net.wirelabs.eventbus.common.BaseTest;
import net.wirelabs.eventbus.common.EventTypes;
import net.wirelabs.eventbus.testclients.TestPanel;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class EventAwarePanelTest extends BaseTest {

    @Test
    void testEventAwarePanelEventSubscription() {
        TestPanel panel = new TestPanel();   // event1 subscribed by overriden subscribe_events()
        panel.subscribe(EventTypes.EVENT_2); // event2 subscribed by subscribe() method

        // assert textfield is empty befor serving events
        assertThat(panel.getTextfield().getText()).isNullOrEmpty();

        EventBus.publish(EventTypes.EVENT_1, "123");
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(panel.getTextfield().getText()).isEqualTo("123"));
        EventBus.publish(EventTypes.EVENT_2, "321");
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(panel.getTextfield().getText()).isEqualTo("321"));
        shutdownAndAssertFinishedClients();
    }
}


