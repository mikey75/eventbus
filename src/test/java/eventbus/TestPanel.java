package eventbus;

import lombok.Getter;
import net.wirelabs.evbus.eventbus.Event;
import net.wirelabs.evbus.eventbus.swing.EventAwarePanel;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

@Getter
public class TestPanel extends EventAwarePanel {
    private final JTextField textfield;

    public TestPanel() {
        textfield = new JTextField();
        add(textfield);
        }

    @Override
    protected void onEvent(Event evt) {
        textfield.setText((String) evt.getPayload());
        add(textfield);
    }

    @Override
    protected Collection<Object> subscribeEvents() {
        return List.of(TestUtils.EVENT_1);
    }

    @Override
    protected void subscribe(Object... events) {
        super.subscribe(events);
    }
}
