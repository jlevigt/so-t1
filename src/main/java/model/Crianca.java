package model;

import util.Logger;

public class Crianca extends Thread {
    private final int id;
    private final long tb; // tempo brincando (ms)
    private final long td; // tempo descansando (ms)
    private Cesto cesto;
    private boolean temBola = false;
    private volatile Estado estado = Estado.DESCANSANDO;

    public Crianca(int id, long tb, long td, Cesto cesto, boolean temBola) {
        this.id = id;
        this.tb = tb;
        this.td = td;
        this.cesto = cesto;
        this.temBola = temBola;
        this.estado = temBola ? Estado.BRINCANDO : Estado.DESCANSANDO;
        setDaemon(true);
    }

    public void setCesto(Cesto cesto) {
        this.cesto = cesto;
    }

    public boolean isTemBola() {
        return temBola;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (temBola) {
                    busyWork(tb);
                    setEstado(Estado.AGUARDANDO_ESPACO);
                    cesto.colocarBola();
                    temBola = false;
                    Logger.log("Criança " + id + " COLOCOU uma bola.");
                    setEstado(Estado.DESCANSANDO);
                } else {
                    busyWork(td);
                    setEstado(Estado.AGUARDANDO_BOLA);
                    cesto.pegarBola();
                    temBola = true;
                    Logger.log("Criança " + id + " PEGOU uma bola.");
                    setEstado(Estado.BRINCANDO);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void setEstado(Estado novoEstado) {
        if (this.estado != novoEstado) {
            this.estado = novoEstado;
            Logger.log("Criança " + id + " mudou para: " + novoEstado.getLabel());
        }
    }

    private void busyWork(long tempoMs) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < tempoMs) {
            // Math operations to simulate CPU work
            double x = Math.sin(Math.random()) * Math.cos(Math.random());
            Thread.yield();
        }
    }

    public int getCriancaId() {
        return id;
    }

    public long getTb() {
        return tb;
    }

    public long getTd() {
        return td;
    }

    public Estado getEstado() {
        return estado;
    }
}
