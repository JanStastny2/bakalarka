package cz.uhk.grainweight.service;

import org.springframework.stereotype.Service;

@Service
public class WorkSimulator {

    public void simulateIoDelay(long delayMs) {
        if (delayMs <= 0) return;
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}

