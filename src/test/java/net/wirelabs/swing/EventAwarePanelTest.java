package net.wirelabs.swing;

import net.wirelabs.eventbus.*;
import org.junit.jupiter.api.Test;

import javax.swing.JTextField;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static net.wirelabs.eventbus.EventTypes.EVENT_3;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created 9/12/23 by Michał Szwaczko (mikey@wirelabs.net)
 */
class EventAwarePanelTest extends BaseTest {

    @Test
    void testEventAwarePanel() {

        final JTextField textfield = new JTextField();

        final EventAwarePanel panel = new EventAwarePanel() {
            @Override
            protected void onEvent(Event evt) {
                textfield.setText(evt.getPayload().toString());
            }

            @Override
            protected Collection<IEventType> subscribeEvents() {
                return Collections.singleton(EVENT_3);
            }
        };

        panel.add(textfield);
        assertThat(textfield.getText()).isNullOrEmpty();

        EventBus.publish(EVENT_3,"123");

        waitUntilAsserted(Duration.ofSeconds(1), () -> {

            assertThat(panel.eventListener.queueSize()).isZero();
            assertThat(textfield.getText()).isEqualTo("123");
        });
    }
}
