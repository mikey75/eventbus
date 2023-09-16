package net.wirelabs.eventbus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runs code in a interruptable infinite loop
 */
public class LoopRunner {
    
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);
    
    protected void loopUntilStopped(Runnable serviceCode) {
        
        while (!shouldStop.get()) {
            serviceCode.run();
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
    }

    protected void stop() {
        shouldStop.set(true);
    }
}
