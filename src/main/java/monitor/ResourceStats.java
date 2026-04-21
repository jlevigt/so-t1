package monitor;

/**
 * Objeto imutável contendo o snapshot das métricas do sistema.
 */
public record ResourceStats(
    double cpuLoad,
    int totalThreads,
    int runnable,
    int waiting,
    int blocked
) {
    public static ResourceStats empty() {
        return new ResourceStats(0.0, 0, 0, 0, 0);
    }
}
