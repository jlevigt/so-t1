package simulation;

import core.Cesto;
import core.Crianca;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    private final List<Crianca> criancas = new ArrayList<>();
    private Cesto cesto;
    private boolean iniciada = false;

    public void adicionarCrianca(Crianca crianca) {
        criancas.add(crianca);
    }

    public void iniciar(int capacity) {
        if (iniciada) return;

        cesto = new Cesto(capacity); 
        iniciada = true;
    }

    public List<Crianca> getCriancas() {
        return criancas;
    }

    public Cesto getCesto() {
        return cesto;
    }

    public boolean isIniciada() {
        return iniciada;
    }
}
