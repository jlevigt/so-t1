package ui;

import config.Config;
import core.Crianca;
import core.EstadoCrianca;
import simulation.Engine;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationPanel extends JPanel {
    private final Engine engine;
    private int cellSize;

    public SimulationPanel(Engine engine) {
        this.engine = engine;
        setBackground(Color.WHITE);

        // Timer de Repaint (~60 FPS)
        new Timer(16, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcular cellSize dinâmico
        int availableWidth = getWidth();
        int availableHeight = getHeight();
        cellSize = Math.min(availableWidth / Config.GRID_SIZE, availableHeight / Config.GRID_SIZE);

        // 0. Desenhar Grade
        desenharGrade(g2);

        // 1. Desenhar Cesto
        desenharCesto(g2);

        // 2. Desenhar Crianças
        List<Crianca> criancas = engine.getCriancas();
        for (Crianca c : criancas) {
            c.getUiLock().lock();
            try {
                desenharCrianca(g2, c);
            } finally {
                c.getUiLock().unlock();
            }
        }
    }

    private void desenharGrade(Graphics2D g2) {
        g2.setColor(new Color(235, 235, 235));
        for (int i = 0; i <= Config.GRID_SIZE; i++) {
            int pos = i * cellSize;
            g2.drawLine(pos, 0, pos, Config.GRID_SIZE * cellSize);
            g2.drawLine(0, pos, Config.GRID_SIZE * cellSize, pos);
        }
    }

    private void desenharCesto(Graphics2D g2) {
        int halfSize = Config.BASKET_SIZE / 2;
        int center = Config.GRID_SIZE / 2;
        int startX = (center - halfSize) * cellSize;
        int startY = (center - halfSize) * cellSize;
        int size = Config.BASKET_SIZE * cellSize;
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(startX, startY, size, size);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(startX, startY, size, size);
        
        if (engine.getCesto() != null) {
            String info = String.format("%d / %d", engine.getCesto().getBallCount(), engine.getCesto().getCapacity());
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int tx = startX + (size - fm.stringWidth(info)) / 2;
            int ty = startY + (size + fm.getAscent()) / 2 - 5;
            g2.drawString(info, tx, ty);
        }
    }

    private void desenharCrianca(Graphics2D g2, Crianca c) {
        int size = cellSize / 2;
        int x = c.getGridX() * cellSize + (cellSize - size) / 2;
        int y = c.getGridY() * cellSize + (cellSize - size) / 2;
        EstadoCrianca estado = c.getEstado();

        switch (estado) {
            case BRINCANDO -> g2.setColor(Color.GREEN);
            case DESCANSANDO -> g2.setColor(Color.BLUE);
            case PEGAR_BOLA -> g2.setColor(Color.YELLOW);
            case GUARDAR_BOLA -> g2.setColor(Color.RED);
            default -> g2.setColor(Color.BLACK);
        }

        g2.fillOval(x, y, size, size);
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, size, size);

        // Texto ao lado (ID e tempo)
        String info = String.format("%d (%.1fs)", c.getCriancaId(), c.getTempoRestanteSec());
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString(info, x + size + 2, y + size - 5);
    }
}
