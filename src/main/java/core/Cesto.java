package core;

import config.Config;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Cesto {
    private final int capacity;
    private final Semaphore semBolas;
    private final Semaphore semEspacos;
    private final Semaphore mutex;
    private final AtomicInteger ballCount;

    public Cesto(int capacity) {
        this.capacity = capacity;
        this.semBolas = new Semaphore(0); // Bolas disponíveis
        this.semEspacos = new Semaphore(capacity); // Espaços disponíveis
        this.mutex = new Semaphore(1); // Proteção do cesto
        this.ballCount = new AtomicInteger(0);
    }

    public void pegarBola() throws InterruptedException {
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            semBolas.acquire();
        } else {
            while (!semBolas.tryAcquire()) {
                Thread.onSpinWait(); // Busy-wait
            }
        }

        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            mutex.acquire();
        } else {
            while (!mutex.tryAcquire()) {
                Thread.onSpinWait();
            }
        }

        try {
            ballCount.decrementAndGet();
        } finally {
            mutex.release();
            semEspacos.release();
        }
    }

    public void colocarBola() throws InterruptedException {
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            semEspacos.acquire();
        } else {
            while (!semEspacos.tryAcquire()) {
                Thread.onSpinWait(); // Busy-wait
            }
        }

        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            mutex.acquire();
        } else {
            while (!mutex.tryAcquire()) {
                Thread.onSpinWait();
            }
        }

        try {
            ballCount.incrementAndGet();
        } finally {
            mutex.release();
            semBolas.release();
        }
    }

    public int getBallCount() {
        return ballCount.get();
    }

    public int getCapacity() {
        return capacity;
    }
}
