package ui;

import config.Config;
import simulation.Engine;
import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {
    private final Engine engine;
    private final BiConsumer<Integer, Config.ModoExecucao> onStartSimulation;
    private final Consumer<ChildParams> onAddChild;
    
    // Setup controls
    private JTextField inputK;
    private JComboBox<Config.ModoExecucao> comboModo;
    private JButton btnStart;
    
    // Simulation controls
    private JTextField inputTb, inputTd;
    private JCheckBox checkTemBola;
    private JButton btnAddCrianca;
    private JLabel lblTotalCriancas, lblConfig;

    public static class ChildParams {
        public double tb, td;
        public boolean temBola;
        public ChildParams(double tb, double td, boolean temBola) {
            this.tb = tb; this.td = td; this.temBola = temBola;
        }
    }

    public ControlPanel(Engine engine, BiConsumer<Integer, Config.ModoExecucao> onStart, Consumer<ChildParams> onAddChild) {
        this.engine = engine;
        this.onStartSimulation = onStart;
        this.onAddChild = onAddChild;
        
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Controles da Simulação"));
        
        showSetupUI();
    }

    private void showSetupUI() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Capacidade (k):"), gbc);
        gbc.gridx = 1;
        inputK = new JTextField("5", 5);
        add(inputK, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Modo:"), gbc);
        gbc.gridx = 1;
        comboModo = new JComboBox<>(Config.ModoExecucao.values());
        add(comboModo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        btnStart = new JButton("Iniciar Simulação");
        btnStart.addActionListener(e -> start());
        add(btnStart, gbc);

        revalidate();
        repaint();
    }

    private void start() {
        try {
            int k = Integer.parseInt(inputK.getText());
            Config.ModoExecucao modo = (Config.ModoExecucao) comboModo.getSelectedItem();
            onStartSimulation.accept(k, modo);
            showRunningUI(k, modo);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor de k inválido");
        }
    }

    private void showRunningUI(int k, Config.ModoExecucao modo) {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Seção 1: Configuração Atual (Resumo)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        lblConfig = new JLabel(String.format("Configuração: k=%d | Modo: %s", k, modo.getLabel()));
        lblConfig.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(lblConfig, gbc);

        // Seção 2: Parâmetros de Criação
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Tb (s):"), gbc);
        gbc.gridx = 1;
        inputTb = new JTextField(String.valueOf(Config.TB_DEFAULT), 4);
        add(inputTb, gbc);
        
        gbc.gridx = 2;
        add(new JLabel("Td (s):"), gbc);
        gbc.gridx = 3;
        inputTd = new JTextField(String.valueOf(Config.TD_DEFAULT), 4);
        add(inputTd, gbc);
        
        gbc.gridx = 4;
        checkTemBola = new JCheckBox("Bola?");
        add(checkTemBola, gbc);

        // Seção 3: Ações
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        btnAddCrianca = new JButton("Add Criança");
        btnAddCrianca.addActionListener(e -> addChild());
        add(btnAddCrianca, gbc);

        // Seção 4: Status
        gbc.gridx = 2; gbc.gridwidth = 3;
        lblTotalCriancas = new JLabel("Crianças: 0 / " + Config.MAX_CRIANCAS);
        lblTotalCriancas.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTotalCriancas, gbc);

        revalidate();
        repaint();
    }

    private void addChild() {
        try {
            double tb = Double.parseDouble(inputTb.getText());
            double td = Double.parseDouble(inputTd.getText());
            boolean temBola = checkTemBola.isSelected();
            onAddChild.accept(new ChildParams(tb, td, temBola));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tempos inválidos");
        }
    }

    public void updateTotal(int total) {
        if (lblTotalCriancas != null) {
            lblTotalCriancas.setText("Crianças: " + total + " / " + Config.MAX_CRIANCAS);
            if (total >= Config.MAX_CRIANCAS) {
                btnAddCrianca.setEnabled(false);
            }
        }
    }
}
