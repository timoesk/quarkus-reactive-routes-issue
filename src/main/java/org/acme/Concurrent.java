package org.acme;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Concurrent {

    private AtomicInteger count;

    private AtomicInteger max;

    public void inc() {
        var value = count.incrementAndGet();
        max.updateAndGet(current -> Math.max(current, value));
    }

    public void dec() {
        count.decrementAndGet();
    }

    public int getMax() {
        return max.get();
    }

    public void reset() {
        count.set(0);
        max.set(0);
    }

    @PostConstruct
    void init() {
        count = new AtomicInteger(0);
        max = new AtomicInteger(0);
    }
}
