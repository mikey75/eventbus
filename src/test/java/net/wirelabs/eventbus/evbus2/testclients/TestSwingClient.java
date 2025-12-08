package net.wirelabs.eventbus.evbus2.testclients;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.EventBusSwingClient;
import net.wirelabs.eventbus.OnEvent;
import net.wirelabs.eventbus.evbus2.events.MapChangedEvent;

import javax.swing.*;

@Slf4j
public class TestSwingClient extends EventBusSwingClient {
    @Getter
    private final JTextField textField = new JTextField();

    public TestSwingClient() {
        add(textField);
    }

    @OnEvent
    private void mapChanged(MapChangedEvent evt) {
        textField.setText(evt.getPayload());
    }
}
