package ui;

import config.Config;
import core.Crianca;
import simulation.Engine;
import util.Logger;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Engine engine = new Engine();
    private final SimulationPanel simulationPanel;
    private final ControlPanel controlPanel;
    private final ConsolePanel consolePanel;
    private int idCounter = 1;

    public MainFrame() {
        setTitle("Brincadeira de Crianças - Simulação Refinada");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Responsividade: 90% largura, 85% altura
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // 1. Grid (Centro)
        simulationPanel = new SimulationPanel(engine);
        add(simulationPanel, BorderLayout.CENTER);

        // 2. Painel Inferior (Console + Controles)
        consolePanel = new ConsolePanel();
        controlPanel = new ControlPanel(engine, 
            (k, modo) -> startSimulation(k, modo),
            params -> addCrianca(params)
        );
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consolePanel, controlPanel);
        splitPane.setResizeWeight(0.6); // 60% para o console
        splitPane.setDividerLocation((int)(width * 0.6));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(width, 220)); // Aumentado para acomodar os novos controles
        bottomPanel.add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startSimulation(int k, Config.ModoExecucao modo) {
        Config.modoAtual = modo;
        engine.iniciar(k);
        Logger.log("Simulação iniciada: k=" + k + ", modo=" + modo.getLabel());
    }

    private void addCrianca(ControlPanel.ChildParams params) {
        if (idCounter > Config.MAX_CRIANCAS) return;

        Crianca c = new Crianca(idCounter++, params.tb, params.td, engine.getCesto(), params.temBola);
        engine.adicionarCrianca(c);
        c.start();
        
        controlPanel.updateTotal(idCounter - 1);
        Logger.log("Criança " + (idCounter - 1) + " adicionada.");
    }
}
