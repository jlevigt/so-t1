package model;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Cesto {
    private int capacity;
    private final Semaphore mutex;
    private final Semaphore full;
    private final Semaphore empty;
    private final AtomicInteger ballCount;

    public Cesto(int capacity) {
        this.capacity = capacity;
        this.mutex = new Semaphore(1);
        this.full = new Semaphore(0);
        this.empty = new Semaphore(capacity);
        this.ballCount = new AtomicInteger(0);
    }

    public void aumentarCapacidade() {
        try {
            mutex.acquire();
            capacity++;
            empty.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
    }

    public void pegarBola() throws InterruptedException {
        full.acquire();
        mutex.acquire();
        try {
            ballCount.decrementAndGet();
        } finally {
            mutex.release();
            empty.release();
        }
    }

    public void colocarBola() throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        try {
            ballCount.incrementAndGet();
        } finally {
            mutex.release();
            full.release();
        }
    }

    public int getBallCount() {
        return ballCount.get();
    }

    public int getCapacity() {
        return capacity;
    }
}
