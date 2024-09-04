package net.wirelabs.eventbus;


import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class SwingTest {

    @Test
    void testEventAwarePanelSubscriptionType1() {
        TestPanel panel = new TestPanel(); // PANEL SUBSCRIBED TO Event1 in subscribe_events()
        assertThat(panel.getTextfield().getText()).isNullOrEmpty();

        EventBus.publish(TestUtils.EVENT_1, "123");
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(panel.getTextfield().getText()).isEqualTo("123"));

    }

    @Test
    void testEventAwarePanelSubscriptionType2() {
        TestPanel panel = new TestPanel(); // PANEL SUBSCRIBED TO Event1 in subscribe_events()
        assertThat(panel.getTextfield().getText()).isNullOrEmpty();

        panel.subscribe(TestUtils.EVENT_2);
        EventBus.publish(TestUtils.EVENT_2, "321");
        Awaitility.waitAtMost(Duration.ofSeconds(5)).untilAsserted(() -> assertThat(panel.getTextfield().getText()).isEqualTo("321"));


    }
}


