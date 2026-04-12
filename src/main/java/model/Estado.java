package model;

public enum Estado {
    AGUARDANDO_BOLA("Aguardando bola"),
    BRINCANDO("Brincando"),
    AGUARDANDO_ESPACO("Aguardando espaço"),
    DESCANSANDO("Descansando");

    private final String label;

    Estado(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
