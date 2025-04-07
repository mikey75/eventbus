package net.wirelabs.eventbus.testclients;

import lombok.Getter;
import net.wirelabs.eventbus.Event;
import net.wirelabs.eventbus.common.EventTypes;
import net.wirelabs.eventbus.IEventType;
import net.wirelabs.eventbus.swing.EventAwarePanel;

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
    protected Collection<IEventType> subscribeEvents() {
        return List.of(EventTypes.EVENT_1);
    }

}
