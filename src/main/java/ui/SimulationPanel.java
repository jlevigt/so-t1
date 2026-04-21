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
    private static final int OFFSET_X = 40; // Padding para ID na esquerda
    private static final int OFFSET_Y = 30; // Padding para ID no topo

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

        // Calcular cellSize dinâmico considerando o padding
        int availableWidth = getWidth() - (OFFSET_X * 2);
        int availableHeight = getHeight() - (OFFSET_Y * 2);
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
            // Aplicar offsets no desenho das linhas
            g2.drawLine(OFFSET_X + pos, OFFSET_Y, OFFSET_X + pos, OFFSET_Y + Config.GRID_SIZE * cellSize);
            g2.drawLine(OFFSET_X, OFFSET_Y + pos, OFFSET_X + Config.GRID_SIZE * cellSize, OFFSET_Y + pos);
        }
    }

    private void desenharCesto(Graphics2D g2) {
        int halfSize = Config.BASKET_SIZE / 2;
        int center = Config.GRID_SIZE / 2;
        int startX = OFFSET_X + (center - halfSize) * cellSize;
        int startY = OFFSET_Y + (center - halfSize) * cellSize;
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
        int x = OFFSET_X + c.getGridX() * cellSize + (cellSize - size) / 2;
        int y = OFFSET_Y + c.getGridY() * cellSize + (cellSize - size) / 2;
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

        // Lógica de texto dinâmico (ID e Tempo)
        int id = c.getCriancaId();
        String idStr = String.valueOf(id);
        String timeStr = String.format("%.1fs", c.getTempoRestanteSec());
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        int textHeight = fm.getAscent();

        int tx_id, ty_id, tx_time, ty_time;

        if (id >= 1 && id <= 5) { // TOPO: Tempo ACIMA do ID
            tx_id = x + (size / 2) - (fm.stringWidth(idStr) / 2);
            ty_id = y - 2;
            tx_time = x + (size / 2) - (fm.stringWidth(timeStr) / 2);
            ty_time = ty_id - textHeight - 1;
            g2.drawString(idStr, tx_id, ty_id);
            g2.drawString(timeStr, tx_time, ty_time);
        } else if (id >= 6 && id <= 10) { // DIREITA: Horizontal
            String info = idStr + " (" + timeStr + ")";
            g2.drawString(info, x + size + 2, y + (size / 2) + (textHeight / 2) - 2);
        } else if (id >= 11 && id <= 15) { // BASE: Tempo ABAIXO do ID
            tx_id = x + (size / 2) - (fm.stringWidth(idStr) / 2);
            ty_id = y + size + textHeight + 2;
            tx_time = x + (size / 2) - (fm.stringWidth(timeStr) / 2);
            ty_time = ty_id + textHeight + 1;
            g2.drawString(idStr, tx_id, ty_id);
            g2.drawString(timeStr, tx_time, ty_time);
        } else { // ESQUERDA: Horizontal
            String info = idStr + " (" + timeStr + ")";
            g2.drawString(info, x - fm.stringWidth(info) - 2, y + (size / 2) + (textHeight / 2) - 2);
        }
    }
}
