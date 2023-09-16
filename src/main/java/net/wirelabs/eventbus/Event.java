package net.wirelabs.eventbus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Event {
    private final IEventType eventType;
    private final Object payload;
}
