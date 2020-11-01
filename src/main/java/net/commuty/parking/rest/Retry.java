package net.commuty.parking.rest;

import org.slf4j.Logger;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

class Retry {
    private static final Logger LOG = getLogger(Retry.class);

    private final int count;
    private final int intervalInMs;

    Retry(int count, int intervalInMs) {
        this.count = count;
        this.intervalInMs = intervalInMs;
    }

    public int getCount() {
        return count;
    }

    public int getIntervalInMs() {
        return intervalInMs;
    }

    public boolean isOver() {
        return count <= 0;
    }

    public Retry next() {
        if (isOver()) {
            return this;
        } else {
            return new Retry(count - 1, intervalInMs);
        }
    }

    public void waitInterval() {
        if (!isOver()) {
            try {
                sleep(intervalInMs);
            } catch (InterruptedException e) {
                LOG.warn("Thread interrupted!", e);
                currentThread().interrupt();
            }
        }
    }
}
