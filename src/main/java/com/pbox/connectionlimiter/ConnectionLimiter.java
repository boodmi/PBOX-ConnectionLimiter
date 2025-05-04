package com.pbox.connectionlimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;

public class ConnectionLimiter {
    private final ConfigManager config;
    private final Deque<Instant> timestamps = new LinkedList<>();
    private Instant lockStart = null;
    private boolean enabled = true;

    public ConnectionLimiter(ConfigManager config) {
        this.config = config;
    }

    public synchronized boolean tryConnect() {
        if (!enabled) return true;

        Instant now = Instant.now();
        Duration interval = Duration.ofSeconds(config.periodSeconds());

        if (lockStart != null) {
            if (now.isAfter(lockStart.plus(interval))) {
                timestamps.clear();
                lockStart = null;
            } else {
                return false;
            }
        }

        timestamps.addLast(now);
        if (timestamps.size() > config.maxConnections()) {
            lockStart = now;
            return false;
        }

        return true;
    }

    public synchronized String getDenyMessage() {
        if (lockStart == null) return config.denyMessage().replace("{time}", "некоторое время");

        Duration remaining = Duration.between(Instant.now(), lockStart.plusSeconds(config.periodSeconds()));
        long minutes = remaining.toMinutes();
        long seconds = remaining.minusMinutes(minutes).getSeconds();
        String timeStr = String.format("%d минут %d секунд", minutes, seconds);
        return config.denyMessage().replace("{time}", timeStr);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
