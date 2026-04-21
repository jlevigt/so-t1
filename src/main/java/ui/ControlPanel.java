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
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    
    // Setup controls
    private JTextField inputK;
    private JComboBox<Config.ModoExecucao> comboModo;
    
    // Simulation controls
    private JTextField inputTb, inputTd;
    private JCheckBox checkTemBola;
    private JButton btnAddCrianca;
    private JLabel lblTotalCriancas, lblConfig;
    private JProgressBar progressCriancas;

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
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        setupCards();
        add(cards, BorderLayout.CENTER);
    }

    private void setupCards() {
        cards.add(createSetupPanel(), "SETUP");
        cards.add(createRunningPanel(), "RUNNING");
        cardLayout.show(cards, "SETUP");
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Configuração Inicial"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título/Dica
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel hint = new JLabel("Configure os parâmetros base do cesto");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panel.add(hint, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Capacidade (k):"), gbc);
        gbc.gridx = 1;
        inputK = new JTextField("5", 5);
        inputK.setHorizontalAlignment(JTextField.CENTER);
        panel.add(inputK, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Modo:"), gbc);
        gbc.gridx = 1;
        comboModo = new JComboBox<>(Config.ModoExecucao.values());
        panel.add(comboModo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8); // Reduzido de 20 para 12
        JButton btnStart = new JButton("🚀 Iniciar Simulação");
        btnStart.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnStart.setBackground(new Color(70, 130, 180));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e -> start());
        panel.add(btnStart, gbc);

        return panel;
    }

    private JPanel createRunningPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Controles da Simulação"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Status (Lote 1)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        lblConfig = new JLabel("Configuração: k=? | Modo: ?");
        lblConfig.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblConfig.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblConfig, gbc);

        // Formulário (Lote 2)
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Tb (s):"), gbc);
        gbc.gridx = 1;
        inputTb = new JTextField(String.valueOf(Config.TB_DEFAULT), 4);
        mainPanel.add(inputTb, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Td (s):"), gbc);
        gbc.gridx = 3;
        inputTd = new JTextField(String.valueOf(Config.TD_DEFAULT), 4);
        mainPanel.add(inputTd, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        checkTemBola = new JCheckBox("Bola?");
        mainPanel.add(checkTemBola, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2;
        btnAddCrianca = new JButton("➕ Adicionar");
        btnAddCrianca.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAddCrianca.addActionListener(e -> addChild());
        mainPanel.add(btnAddCrianca, gbc);

        // Estatísticas (Lote 3)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 4, 4, 4);
        progressCriancas = new JProgressBar(0, Config.MAX_CRIANCAS);
        progressCriancas.setStringPainted(true);
        mainPanel.add(progressCriancas, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 4, 4, 4);
        lblTotalCriancas = new JLabel("Crianças: 0 / " + Config.MAX_CRIANCAS);
        lblTotalCriancas.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalCriancas.setFont(new Font("SansSerif", Font.ITALIC, 11));
        mainPanel.add(lblTotalCriancas, gbc);

        return mainPanel;
    }

    private void start() {
        try {
            int k = Integer.parseInt(inputK.getText());
            if (k <= 0) throw new NumberFormatException();
            Config.ModoExecucao modo = (Config.ModoExecucao) comboModo.getSelectedItem();
            
            onStartSimulation.accept(k, modo);
            lblConfig.setText(String.format("k=%d | %s", k, modo.getLabel()));
            cardLayout.show(cards, "RUNNING");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A capacidade (k) deve ser um número inteiro positivo.", 
                "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addChild() {
        try {
            double tb = Double.parseDouble(inputTb.getText().replace(",", "."));
            double td = Double.parseDouble(inputTd.getText().replace(",", "."));
            if (tb <= 0 || td <= 0) throw new NumberFormatException();
            
            boolean temBola = checkTemBola.isSelected();
            onAddChild.accept(new ChildParams(tb, td, temBola));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Os tempos Tb e Td devem ser números positivos.", 
                "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTotal(int total) {
        if (lblTotalCriancas != null) {
            lblTotalCriancas.setText("Crianças: " + total + " / " + Config.MAX_CRIANCAS);
            progressCriancas.setValue(total);
            if (total >= Config.MAX_CRIANCAS) {
                btnAddCrianca.setEnabled(false);
                btnAddCrianca.setText("Limite Atingido");
            }
        }
    }
}
