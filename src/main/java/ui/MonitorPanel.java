package ui;

import config.Config;
import monitor.ResourceMonitor;
import monitor.ResourceStats;
import javax.swing.*;
import java.awt.*;

public class MonitorPanel extends JPanel {
    private final ResourceMonitor monitor;
    private final JLabel lblCpu, lblThreads, lblRunnable, lblWaiting, lblBlocked, lblModo;

    public MonitorPanel(ResourceMonitor monitor) {
        this.monitor = monitor;
        
        setPreferredSize(new Dimension(220, 0));
        setMinimumSize(new Dimension(200, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recursos do Sistema"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        Font monoFont = new Font("Monospaced", Font.BOLD, 12);

        lblCpu = createMetricLabel(monoFont);
        lblThreads = createMetricLabel(monoFont);
        lblRunnable = createMetricLabel(monoFont);
        lblWaiting = createMetricLabel(monoFont);
        lblBlocked = createMetricLabel(monoFont);
        lblModo = createMetricLabel(monoFont);
        
        addMetric("CPU Uso  :", lblCpu);
        add(Box.createVerticalStrut(10));
        addMetric("Threads  :", lblThreads);
        addMetric(" - Run   :", lblRunnable);
        addMetric(" - Wait  :", lblWaiting);
        addMetric(" - Block :", lblBlocked);
        add(Box.createVerticalGlue());
        addMetric("MODO     :", lblModo);

        // Timer de atualização da UI (refreshMetrics para evitar conflito com updateUI() do Swing)
        new Timer(1000, e -> refreshMetrics()).start();
    }

    private JLabel createMetricLabel(Font font) {
        JLabel label = new JLabel("--");
        label.setFont(font);
        return label;
    }

    private void addMetric(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        JLabel t = new JLabel(title + " ");
        t.setFont(new Font("Monospaced", Font.PLAIN, 12));
        p.add(t);
        p.add(valueLabel);
        add(p);
    }

    private void refreshMetrics() {
        ResourceStats stats = monitor.getLatestStats();
        lblCpu.setText(String.format("%.2f%%", stats.cpuLoad()));
        lblThreads.setText(String.valueOf(stats.totalThreads()));
        lblRunnable.setText(String.valueOf(stats.runnable()));
        lblWaiting.setText(String.valueOf(stats.waiting()));
        lblBlocked.setText(String.valueOf(stats.blocked()));
        
        lblModo.setText(Config.modoAtual.getLabel());
        lblModo.setForeground(Config.modoAtual == Config.ModoExecucao.BUSY_WAIT ? Color.RED : new Color(0, 100, 0));
    }
}
