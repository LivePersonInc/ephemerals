package com.liveperson.ephemerals.deploy.wait;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by waseemh on 9/5/16.
 */
public abstract class Waiter {

    private Duration timeout;

    private Duration interval;

    public Waiter(Duration timeout, Duration interval) {
        this.timeout = timeout;
        this.interval=interval;
    }

    public Waiter() {
        this(Duration.ofSeconds(180),Duration.ofMillis(500));
    }

    public void start() throws TimeoutException {

        final CountDownLatch done = new CountDownLatch(1);

        new Thread(() -> {
            while(!isConditionMet()) {
                try {
                    Thread.sleep(interval.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            done.countDown();
        }).start();

        try {
            boolean isComplete = done.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if(!isComplete) {
                throw new TimeoutException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }

    protected abstract boolean isConditionMet();

}