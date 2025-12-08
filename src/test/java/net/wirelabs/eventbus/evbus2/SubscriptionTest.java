package net.wirelabs.eventbus.evbus2;


import net.wirelabs.eventbus.EventBus;
import net.wirelabs.eventbus.Sleeper;
import net.wirelabs.eventbus.common.BaseTest;
import net.wirelabs.eventbus.evbus2.events.*;
import net.wirelabs.eventbus.evbus2.testclients.TestClient;
import net.wirelabs.eventbus.evbus2.testclients.TestSwingClient;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


class SubscriptionTest extends BaseTest {




    @Test
    void subscribeEvents() {
        EventBus.reset();
        TestClient t = new TestClient(); // test client subscribes two adduser and deleteuser events
        // check client subscription and Evbus internals
        assertThat(t.getSubscribedEvents()).hasSize(2);
        assertThat(EventBus.getClients()).hasSize(1);
        assertThat(EventBus.getClients()).containsOnly(t);


        // publish the events
        EventBus.publish(new AddUserEvent("Jasiek"));
        EventBus.publish(new DeleteUserEvent("Ewa"));

        // verify events serviced
        Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            logVerifier.verifyLogged("Adding Jasiek");
            logVerifier.verifyLogged("Deleting Ewa");
        });

        // send an event client does not expect  - client should remain silent, and not take it
        assertThat(t.getSubscribedEvents().stream()).anyMatch(evt -> !evt.isAssignableFrom(OtherEvent.class));
        EventBus.publish(new OtherEvent("kaka"));
        Awaitility.waitAtMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            logVerifier.verifyNeverLogged("Adding kaka");
        });


    }



    @Test
    void testAllClientsAtOnce() {
        EventBus.reset();
        TestSwingClient testPanel = new TestSwingClient();
        TestClient testClient1 = new TestClient();
        TestClient testClient2 = new TestClient();

        assertThat(EventBus.getAllClients()).hasSize(3);
        assertThat(EventBus.getClients()).hasSize(2);
        assertThat(EventBus.getClients()).contains(testClient1,testClient2);
        assertThat(EventBus.getSwingClients()).hasSize(1);
        assertThat(EventBus.getSwingClients()).containsOnly(testPanel);

        assertThat(testPanel.getSubscribedEvents()).hasSize(1);
        assertThat(testClient1.getSubscribedEvents()).hasSize(2);

        for (int i = 0; i< 100; i++) {
            EventBus.publish(new MapChangedEvent("mapa #3"));
            EventBus.publish(new AddUserEvent("joe"));
            EventBus.publish(new DeleteUserEvent("mark"));
        }
        Sleeper.sleepSeconds(2);

    }

}
