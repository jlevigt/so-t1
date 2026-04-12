package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Cesto;
import model.Crianca;
import util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {

    @FXML private TextField inputTb;
    @FXML private TextField inputTd;
    @FXML private CheckBox checkTemBola;
    @FXML private Button btnIniciar;
    @FXML private Button btnCriarCrianca;
    @FXML private Label lblCestoStatus;
    @FXML private TableView<Crianca> tableCriancas;
    @FXML private TableColumn<Crianca, String> colId;
    @FXML private TableColumn<Crianca, Number> colTb;
    @FXML private TableColumn<Crianca, Number> colTd;
    @FXML private TableColumn<Crianca, String> colTemBola;
    @FXML private TableColumn<Crianca, String> colEstado;
    @FXML private TextArea txtLog;

    private Cesto cesto;
    private final ObservableList<Crianca> criancas = FXCollections.observableArrayList();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private boolean simulacaoIniciada = false;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCriancaId())));
        colTb.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getTb()));
        colTd.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getTd()));
        colTemBola.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isTemBola() ? "Sim" : "Não"));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado().getLabel()));

        tableCriancas.setItems(criancas);

        // Timeline for polling state
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> updateUI()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void criarCrianca() {
        try {
            long tb = Long.parseLong(inputTb.getText());
            long td = Long.parseLong(inputTd.getText());
            boolean temBola = checkTemBola.isSelected();
            if (tb < 0 || td < 0) throw new NumberFormatException();
            
            Crianca crianca = new Crianca(idCounter.getAndIncrement(), tb, td, cesto, temBola);
            criancas.add(crianca);
            btnIniciar.setDisable(simulacaoIniciada); // Disable start button if simulation already running
            
            if (simulacaoIniciada) {
                if (temBola) {
                    cesto.aumentarCapacidade();
                }
                crianca.start();
                Logger.log("Criança " + crianca.getCriancaId() + " adicionada EM TEMPO REAL (" + (temBola ? "com bola" : "sem bola") + ").");
            } else {
                btnIniciar.setDisable(false);
                Logger.log("Criança " + crianca.getCriancaId() + " adicionada (" + (temBola ? "com bola" : "sem bola") + ").");
            }
        } catch (NumberFormatException e) {
            showAlert("Erro", "Tb e Td devem ser números positivos.");
        }
    }

    @FXML
    public void iniciarSimulacao() {
        if (criancas.isEmpty() || simulacaoIniciada) return;

        int capacity = 0;
        for (Crianca c : criancas) {
            if (c.isTemBola()) {
                capacity++;
            }
        }

        if (capacity == 0) {
            showAlert("Erro", "Pelo menos uma criança deve começar com uma bola para definir a capacidade do cesto.");
            return;
        }

        cesto = new Cesto(capacity);
        for (Crianca c : criancas) {
            c.setCesto(cesto);
            c.start();
        }

        simulacaoIniciada = true;
        btnIniciar.setDisable(true);
        // btnCriarCrianca is NOT disabled anymore
        Logger.log("Simulação iniciada. Capacidade do cesto calculada: " + capacity);
    }

    private void updateUI() {
        if (cesto != null) {
            lblCestoStatus.setText("Bolas no cesto: " + cesto.getBallCount() + " / " + cesto.getCapacity());
        }
        
        // Refresh table to show updated states
        tableCriancas.refresh();

        // Update log area
        String msg;
        StringBuilder newLogs = new StringBuilder();
        while ((msg = Logger.poll()) != null) {
            newLogs.append(msg).append("\n");
        }
        if (newLogs.length() > 0) {
            txtLog.appendText(newLogs.toString());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
