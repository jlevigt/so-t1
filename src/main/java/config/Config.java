package config;

public class Config {
    public enum ModoExecucao {
        BLOQUEANTE("Bloqueante"),
        BUSY_WAIT("Busy-Wait");

        private final String label;

        ModoExecucao(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static volatile ModoExecucao modoAtual = ModoExecucao.BLOQUEANTE;
    public static final int FPS_TARGET = 30;
    public static final int MAX_CRIANCAS = 20;

    // Novas configurações de Grid
    public static final int GRID_SIZE = 17;
    public static final int BASKET_SIZE = 5;
    
    // Configurações de tempo padrão
    public static final double TB_DEFAULT = 1.0;
    public static final double TD_DEFAULT = 1.0;
}
