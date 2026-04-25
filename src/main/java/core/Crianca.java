package core;

import config.Config;
import util.Logger;
import java.util.concurrent.locks.ReentrantLock;

public class Crianca extends Thread {
    private final int id;
    private final long tempoBrincarNano;
    private final long tempoDescansarNano;
    private final Cesto cesto;
    private volatile boolean temBola;
    private volatile EstadoCrianca estado;
    
    // UI - Posicionamento e Render
    private volatile int gridX, gridY;
    private int spawnX, spawnY;
    private int dxOut, dyOut;
    private volatile double tempoRestanteSec;
    private static final int TOTAL_FRAMES = 10;
    
    private final Object logicLock = new Object();
    private final ReentrantLock uiLock = new ReentrantLock();

    public Crianca(int id, double tbSec, double tdSec, Cesto cesto, boolean temBola) {
        this.id = id;
        this.tempoBrincarNano = (long) (tbSec * 1_000_000_000L);
        this.tempoDescansarNano = (long) (tdSec * 1_000_000_000L);
        this.cesto = cesto;
        this.temBola = temBola;
        this.estado = temBola ? EstadoCrianca.BRINCANDO : EstadoCrianca.PEGAR_BOLA;
        
        // Spawn determinístico baseado no ID (1-20)
        inicializarSpawnDeterministico(id);
        
        setDaemon(true);
    }

    private void inicializarSpawnDeterministico(int id) {
        int center = Config.GRID_SIZE / 2; // 8
        int ringDistance = (Config.BASKET_SIZE / 2) + 1; // 3
        
        int startX = center - ringDistance; // 5
        int endX = center + ringDistance;   // 11
        int startY = center - ringDistance; // 5
        int endY = center + ringDistance;   // 11

        int currentId = id - 1; // 0 to 19
        int side = currentId / 5;   // 0: top, 1: right, 2: bottom, 3: left
        int posInSide = currentId % 5; // 0 to 4
        
        switch (side) {
            case 0 -> { // Top row (excluding corners)
                gridX = startX + 1 + posInSide; // 6, 7, 8, 9, 10
                gridY = startY; // 5
                dxOut = 0; dyOut = -1;
            }
            case 1 -> { // Right side (excluding corners)
                gridX = endX; // 11
                gridY = startY + 1 + posInSide; // 6, 7, 8, 9, 10
                dxOut = 1; dyOut = 0;
            }
            case 2 -> { // Bottom row (excluding corners)
                gridX = endX - 1 - posInSide; // 10, 9, 8, 7, 6
                gridY = endY; // 11
                dxOut = 0; dyOut = 1;
            }
            case 3 -> { // Left side (excluding corners)
                gridX = startX; // 5
                gridY = endY - 1 - posInSide; // 10, 9, 8, 7, 6
                dxOut = -1; dyOut = 0;
            }
        }

        this.spawnX = gridX;
        this.spawnY = gridY;
    }

    private boolean isNoCesto(int x, int y) {
        int center = Config.GRID_SIZE / 2; // 8
        int halfSize = Config.BASKET_SIZE / 2; // 2
        return x >= (center - halfSize) && x <= (center + halfSize) &&
               y >= (center - halfSize) && y <= (center + halfSize);
    }

    @Override
    public void run() {
        try {
            // Se a criança inicia sem bola, ela deve aguardar no cesto antes de começar o ciclo
            if (!temBola) {
                setEstado(EstadoCrianca.PEGAR_BOLA);
                cesto.pegarBola();
                temBola = true;
            }

            while (!Thread.currentThread().isInterrupted()) {
                // 1. Brincar (Movimento de ida e volta)
                setEstado(EstadoCrianca.BRINCANDO);
                executarEstado(tempoBrincarNano);

                // 2. Guardar Bola (Bloqueia no cesto - Sem movimento)
                setEstado(EstadoCrianca.GUARDAR_BOLA);
                cesto.colocarBola();
                temBola = false;

                // 3. Descansar (Movimento de ida e volta)
                setEstado(EstadoCrianca.DESCANSANDO);
                executarEstado(tempoDescansarNano);

                // 4. Pegar Bola (Bloqueia no cesto - Sem movimento)
                setEstado(EstadoCrianca.PEGAR_BOLA);
                cesto.pegarBola();
                temBola = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void executarEstado(long tempoTotalNano) throws InterruptedException {
        long duracaoFrameNano = tempoTotalNano / TOTAL_FRAMES;
        long targetTotal = System.nanoTime() + tempoTotalNano;

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            // Atualizar tempo restante para UI
            tempoRestanteSec = (targetTotal - System.nanoTime()) / 1_000_000_000.0;
            if (tempoRestanteSec < 0) tempoRestanteSec = 0;

            // Movimento discreto na grade
            atualizarPosicaoGrid(i);

            if (Config.modoAtual == Config.ModoExecucao.BLOQUEANTE) {
                esperarBloqueante(duracaoFrameNano);
            } else {
                esperarBusyWait(duracaoFrameNano);
            }
        }
    }

    private void atualizarPosicaoGrid(int frame) {
        uiLock.lock();
        try {
            if (frame < 5) {
                // Indo para a extremidade (5 passos)
                gridX = spawnX + dxOut * (frame + 1);
                gridY = spawnY + dyOut * (frame + 1);
            } else {
                // Voltando para o cesto (5 passos)
                int passoVolta = frame - 4; // 1, 2, 3, 4, 5
                gridX = (spawnX + dxOut * 5) - (dxOut * passoVolta);
                gridY = (spawnY + dyOut * 5) - (dyOut * passoVolta);
            }
        } finally {
            uiLock.unlock();
        }
    }

    private void esperarBloqueante(long nanoTimeout) throws InterruptedException {
        synchronized (logicLock) {
            long ms = nanoTimeout / 1_000_000L;
            int ns = (int) (nanoTimeout % 1_000_000L);
            if (ms > 0 || ns > 0) logicLock.wait(ms, ns);
        }
    }

    private void esperarBusyWait(long nanoTimeout) {
        long target = System.nanoTime() + nanoTimeout;
        while (System.nanoTime() < target) {
            /*
             * Thread.onSpinWait() indica ao processador que a thread está em um loop de espera ocupada.
             * Diferente de sleep/wait, ele não cede o time-slice nem gera troca de contexto.
             * Ref: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html#onSpinWait()
             */
            Thread.onSpinWait();
        }
    }

    private void setEstado(EstadoCrianca novoEstado) {
        this.estado = novoEstado;
        // Resetar tempo restante ao entrar em estados de bloqueio (espera no cesto)
        if (novoEstado == EstadoCrianca.PEGAR_BOLA || novoEstado == EstadoCrianca.GUARDAR_BOLA) {
            this.tempoRestanteSec = 0;
        }
        Logger.log("Criança " + id + " -> " + novoEstado.getLabel());
    }

    // Getters sincronizados para UI
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public EstadoCrianca getEstado() { return estado; }
    public double getTempoRestanteSec() { return tempoRestanteSec; }
    public int getCriancaId() { return id; }
    public ReentrantLock getUiLock() { return uiLock; }
}
