package core;

public enum EstadoCrianca {
    PEGAR_BOLA("Pegando Bola"),
    BRINCANDO("Brincando"),
    GUARDAR_BOLA("Guardando Bola"),
    DESCANSANDO("Descansando");

    private final String label;

    EstadoCrianca(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
