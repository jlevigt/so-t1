package ui;

import config.Config;
import core.Crianca;
import simulation.Engine;
import util.Logger;
import monitor.ResourceMonitor;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Engine engine = new Engine();
    // private final ResourceMonitor resourceMonitor = new ResourceMonitor();
    
    private final SimulationPanel simulationPanel;
    private final ControlPanel controlPanel;
    private final ConsolePanel consolePanel;
    // private final MonitorPanel monitorPanel;
    private int idCounter = 1;

    public MainFrame() {
        setTitle("Brincadeira de Crianças - Simulação de SO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Iniciar monitoramento (Comentado para remover da UI)
        // resourceMonitor.start();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.85);
        setSize(width, height);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // 1. Centro: Grid (Original)
        simulationPanel = new SimulationPanel(engine);
        // monitorPanel = new MonitorPanel(resourceMonitor);
        
        // JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simulationPanel, monitorPanel);
        // centerSplit.setResizeWeight(0.8);
        // centerSplit.setDividerLocation((int)(width * 0.8));
        // add(centerSplit, BorderLayout.CENTER);
        
        add(simulationPanel, BorderLayout.CENTER);

        // 2. Base: Console + Controles
        consolePanel = new ConsolePanel();
        controlPanel = new ControlPanel(engine, 
            (k, modo) -> startSimulation(k, modo),
            params -> addCrianca(params)
        );
        
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consolePanel, controlPanel);
        bottomSplit.setResizeWeight(0.6);
        bottomSplit.setDividerLocation((int)(width * 0.6));
        
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setPreferredSize(new Dimension(width, 220));
        bottomContainer.add(bottomSplit, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

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
