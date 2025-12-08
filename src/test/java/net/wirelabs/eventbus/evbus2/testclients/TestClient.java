package net.wirelabs.eventbus.evbus2.testclients;


import lombok.extern.slf4j.Slf4j;
import net.wirelabs.eventbus.EventBusClient;
import net.wirelabs.eventbus.OnEvent;
import net.wirelabs.eventbus.evbus2.events.AddUserEvent;
import net.wirelabs.eventbus.evbus2.events.DeleteUserEvent;

@Slf4j
public class TestClient extends EventBusClient {

    @OnEvent
    private void deleteUser(DeleteUserEvent evt) {
        String payload = evt.getPayload();
        log.info("Deleting {}", payload);
    }

    @OnEvent
    private void addUser(AddUserEvent evt) {
        String payload = evt.getPayload();
        log.info("Adding {}", payload);
    }

}
