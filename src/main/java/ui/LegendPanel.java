package ui;

import core.EstadoCrianca;
import javax.swing.*;
import java.awt.*;

public class LegendPanel extends JPanel {
    public LegendPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(180, 0));

        JLabel title = new JLabel("Legenda de Estados");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(15));

        addLegendItem(Color.GREEN, EstadoCrianca.BRINCANDO.getLabel());
        addLegendItem(Color.BLUE, EstadoCrianca.DESCANSANDO.getLabel());
        addLegendItem(Color.YELLOW, EstadoCrianca.PEGAR_BOLA.getLabel());
        addLegendItem(Color.RED, EstadoCrianca.GUARDAR_BOLA.getLabel());
        
        add(Box.createVerticalGlue());
    }

    private void addLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        item.setBackground(Color.WHITE);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(180, 30));
        
        JPanel colorCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(2, 2, 14, 14);
                g2.setColor(Color.BLACK);
                g2.drawOval(2, 2, 14, 14);
            }
        };
        colorCircle.setPreferredSize(new Dimension(20, 20));
        colorCircle.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        item.add(colorCircle);
        item.add(label);
        add(item);
        add(Box.createVerticalStrut(5));
    }
}
