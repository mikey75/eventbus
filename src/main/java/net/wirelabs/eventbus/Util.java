package net.wirelabs.eventbus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class Util {

    public static void serviceEvent(Runnable task) {
        EventBus.submit(() -> {
            if (!GraphicsEnvironment.isHeadless()) {
                if (SwingUtilities.isEventDispatchThread()) {
                    task.run();
                } else {
                    SwingUtilities.invokeLater(task);
                }
            } else {
                task.run();    // Headless/backend environment
            }
        });
    }

    public static Runnable createTask(Object owner, Method method, Event<?> evt) {
        return () -> {
            try {
                method.invoke(owner, evt);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke event handler", e);
            }
        };
    }
}
