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
        // P(semBolas)
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            semBolas.acquire();
        } else {
            while (!semBolas.tryAcquire()) {
                spinWait();
            }
        }

        // P(mutex)
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            mutex.acquire();
        } else {
            while (!mutex.tryAcquire()) {
                spinWait();
            }
        }

        try {
            ballCount.decrementAndGet();
        } finally {
            mutex.release();
            semEspacos.release(); // V(semEspacos)
        }
    }

    public void colocarBola() throws InterruptedException {
        // P(semEspacos)
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            semEspacos.acquire();
        } else {
            while (!semEspacos.tryAcquire()) {
                spinWait();
            }
        }

        // P(mutex)
        if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
            mutex.acquire();
        } else {
            while (!mutex.tryAcquire()) {
                spinWait();
            }
        }

        try {
            ballCount.incrementAndGet();
        } finally {
            mutex.release();
            semBolas.release(); // V(semBolas)
        }
    }

    /**
     * Indica ao processador que a thread está em um loop de "spin" (espera ocupada).
     * Ao contrário de Thread.sleep() ou Object.wait(), o onSpinWait():
     * 1. NÃO cede o time-slice da thread (não coloca a thread em estado de espera no scheduler do SO).
     * 2. NÃO envolve troca de contexto (context switch), mantendo a thread ativa no processador.
     * 3. Permite que o hardware do processador otimize a execução do loop, reduzindo consumo
     *    de energia e melhorando a latência de saída do loop quando a condição é satisfeita.
     * Ref: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html#onSpinWait()
     */
    private void spinWait() {
        Thread.onSpinWait();
    }

    public int getBallCount() {
        return ballCount.get();
    }

    public int getCapacity() {
        return capacity;
    }
}
